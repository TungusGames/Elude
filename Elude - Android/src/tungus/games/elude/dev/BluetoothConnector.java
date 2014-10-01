package tungus.games.elude.dev;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import tungus.games.elude.game.multiplayer.StreamConnection;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.badlogic.gdx.Gdx;



/**
 * Class for managing Bluetooth connections
 * 
 * - Every device can act as a server xor as a client at once
 * - Sample usage in Elude's MultiplayerConnectScreen
 * - Use INSTANCE
 */
public class BluetoothConnector {
	
	// Used to identify the application via BT
	private static final UUID MY_UUID = UUID.fromString("c0dedb1b-3668-4773-9fb2-efbbda550b72");
	private static final int REQUEST_VISIBLE_BT = 2;  // Used for activity results
	private static final int REQUEST_ENABLE_BT = 1;  // Used for activity results
	
	// The mighty INSTANCE to rule them all...
	public static BluetoothConnector INSTANCE = new BluetoothConnector();
	
	public static Activity app; //Used for Android API calls
	private BluetoothAdapter adapter; //Used for everything BT
	public boolean supported;	
	
	// Instance of Server and Client...
	public Server server = new Server();
	public Client client = new Client();
	//public boolean isServer;
	
	// Will be instantiated in either the client or the server
	// when a connection is established
	public StreamConnection bluetoothConnection = null;
	
	public BluetoothConnector() {
		adapter = BluetoothAdapter.getDefaultAdapter();
		if (adapter == null)
			supported = false;
		else supported = true;
		
	}
	
	public void enable() {
		if (!adapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			app.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		} else {
			server.state = ServerState.ENABLED;
			client.state = ClientState.ENABLED;
		}
	}
	
	// Processes the result of the requested "Enable BT" and "Enable visibility" dialogs
	public void processActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT) {
			if (resultCode == Activity.RESULT_OK) {
				server.state = ServerState.ENABLED;
				client.state = ClientState.ENABLED;
			}
			server.state = ServerState.ERROR;
			client.state = ClientState.ERROR;
		}
		else if (requestCode == REQUEST_VISIBLE_BT) {
			if (resultCode != Activity.RESULT_CANCELED) {
				server.state = ServerState.VISIBLE;
				server.visibilityTime = (float)resultCode;
			}
		}
	}
	
	/**************************** SERVER CODE ********************************/
	
	// Possible States for the Server
	public static enum ServerState { // FIXME Cannot be in Server for some reason
		DISABLED, ENABLED, VISIBLE, CONNECTED, ERROR
	}
	
	public class Server {
		
		// State, the main communication method between the threads
		public ServerState state = ServerState.DISABLED;
		
		// How much time is left from the visibility period
		private float visibilityTime = 0f;
		
		// The thread in which the server waits for incoming connections
		public AcceptThread acceptThread;

		public Server() {}
		
		public void enableVisibility() { 
			Intent discoverableIntent = new	Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			app.startActivityForResult(discoverableIntent, REQUEST_VISIBLE_BT);
		} 
		
		// Should be called frame by frame
		// TODO Call it!
		public boolean updateVisibility(float deltaTime) {
			visibilityTime -= deltaTime;
			if (visibilityTime <= 0f) {
				state = ServerState.ENABLED;
				return true;
			}
			return false;
		}
		
		public class AcceptThread extends Thread {
			
		    private BluetoothServerSocket serverSocket;
		 
		    public AcceptThread() {
		    	super();
		    	try {	        	
		    		// MY_UUID is the app's UUID string, also used by the client code		    		
		    		serverSocket = adapter.listenUsingRfcommWithServiceRecord("Elude", MY_UUID);
			      	} catch (IOException e) {
			      		state = ServerState.ERROR;
			      		e.printStackTrace();
			        	//TODO error message
			    }
			}
		    
		    @Override
		    public void run() {
		        // Keep listening until exception occurs or a socket is returned
		        while (true) {
		            try {
		            	Gdx.app.log("Bluetooth", "Server trying to accept");
		            	BluetoothSocket socket = serverSocket.accept();
		            	Gdx.app.log("Bluetooth", "Server accepted");
		                // If a connection was accepted
		                if (socket != null) {
		                	// Create the Connection object and sign to the render thread
		                	if (bluetoothConnection == null) {
		                		bluetoothConnection = new StreamConnection(socket.getInputStream(), socket.getOutputStream(), new BluetoothCloser(socket));
			                	state = ServerState.CONNECTED;
		                	}
		                	serverSocket.close();
			                break;
			            }
		            } catch (IOException e) { // TODO Is error needed here?
		            	Gdx.app.log("Bluetooth", "Server exception during accept");
		            	e.printStackTrace();
		            	state = ServerState.ERROR;
		                break;
		            }
		            
		        }
		    }
		 
		    /** Will cancel the listening socket, and cause the thread to finish */
		    public void cancel() {
		        try {
		            serverSocket.close();
		        } catch (IOException e) {
		        	e.printStackTrace();
		        } //TODO error message
		    }
		}
	};
	
	/**************************** END SERVER CODE ****************************/	
	
	/****************************** CLIENT CODE ******************************/
	
	public static enum ClientState {
		DISABLED, ENABLED, DISCOVERING, CONNECTING, CONNECTED, ERROR
	}
	
	public class Client {
		
		// State, the main communication method between the threads
		public ClientState state = ClientState.DISABLED;
		
		private HashSet<BluetoothDevice> discoveredDevices;
		
		// List of nearby devices currently hosting Elude
		//private HashSet<String> discoveredGames; TODO API 15
		
		public ConnectThread connectThread;
		BluetoothSocket socket = null;
		
		public Client() {}
		
		public void listDevices(List<BluetoothDevice> destination) {
			destination.clear();
			synchronized(discoveredDevices) {
				for (BluetoothDevice d : discoveredDevices) {
					destination.add(d);
				}
			}
		}
		
		// Create a BroadcastReceiver for ACTION_FOUND
		private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				// When discovery finds a device
				if (BluetoothDevice.ACTION_FOUND.equals(action)) {
					// Get the BluetoothDevice object from the Intent
					BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					// Add the name and address to an array adapter to show in a ListView
					synchronized(discoveredDevices) {
						discoveredDevices.add(device);	
						Gdx.app.log("Bluetooth", "Discovered " + device.getName());
					}					
				} /*else if (BluetoothDevice.ACTION_UUID.equals(action)) {
					ParcelUuid uuid = intent.getParcelableExtra(BluetoothDevice.EXTRA_UUID);
					if (uuid.getUuid().equals(MY_UUID)) {
						BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
						discoveredGames.add(((BluetoothDevice)intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)).getName());
					}
				} TODO API 15 */
			}
		};
		
		public boolean enableDiscovery() {
			if (adapter.startDiscovery()) {
				discoveredDevices = new HashSet<BluetoothDevice>();
				//discoveredGames = new HashSet<String>(); TODO API 15
				// Register the BroadcastReceiver
				IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
				//filter.addAction(BluetoothDevice.ACTION_UUID); TODO API 15
				app.registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
				state = ClientState.DISCOVERING;
				Gdx.app.log("Bluetooth", "Started discovery");
				return true;
			} else {
				state = ClientState.ERROR;
				Gdx.app.log("Bluetooth", "Error: BT not on!");
				return false;
			}
		}
		
		public boolean disableDiscovery() {
			if (adapter.cancelDiscovery()) {
				app.unregisterReceiver(mReceiver);
				state = ClientState.ENABLED;
				return true;
			}
			else return false; /**TODO SOME SAFETY CHECK HERE*/
		}
		
		/*public ArrayList<String> getPairedDevices() {
			ArrayList<String> result = new ArrayList<String>();
			Set<BluetoothDevice> devices = adapter.getBondedDevices();
			Iterator<BluetoothDevice> iterator= devices.iterator();
			while (iterator.hasNext())
				result.add(iterator.next().getName());
			return result;
		}*/
		/*
		// Gets nearby devices hosting Elude -- asynchronous API
		public ArrayList<String> getGames() {
			ArrayList<String> result = new ArrayList<String>();
			if (state == ClientState.DISCOVERING) {
				 
			}
			return result;
		}
		
		// Same, but from cached data
		public ArrayList<String> getGamesFromCache() {
			ArrayList<String> result = new ArrayList<String>();
			if (state == ClientState.DISCOVERING) {
				Iterator<BluetoothDevice> iterator = discoveredDevices.iterator();
				while (iterator.hasNext()) {
					BluetoothDevice device = iterator.next();
					ParcelUuid[] uuids = device.getUuids();
					for (int i = 0; i < uuids.length; i++)
						if (uuids[i].getUuid().equals(MY_UUID)) {
							discoveredGames.add(device.getName());
							break;
						}
				}
			}
			return result;
		} TODO API 15*/
		
		public boolean connectTo(String name) {
			Iterator<BluetoothDevice> iterator = discoveredDevices.iterator();
			BluetoothDevice device = null;
			while (iterator.hasNext()) {
				BluetoothDevice current = iterator.next();
				if (current.getName().equals(name)) {
					device = current;
					break;
				}
			}
			if (device != null) {
				return connectTo(device);
			} return false;
		}
		
		public boolean connectTo(BluetoothDevice device) {
			connectThread = new ConnectThread(device);
			connectThread.start();
			if (state == ClientState.CONNECTING) {
				return true;
			}
			return false;
		}
		
		public class ConnectThread extends Thread {
			
			private BluetoothSocket socket = null;
			
		    public ConnectThread(BluetoothDevice device) {	 
		        // Get a BluetoothSocket to connect with the given BluetoothDevice
		        try {
		        	state = ClientState.CONNECTING;
		            // uuid is the app's UUID string, also used by the server code
		            socket = device.createRfcommSocketToServiceRecord(MY_UUID);
		        } catch (IOException e) {
		        	state = ClientState.ERROR;
		        }
		    }
		 
		    
		    
		    public void run() {
		        // Cancel discovery because it will slow down the connection
		        disableDiscovery();
		        state = ClientState.CONNECTING;
		        try {
		            // Connect the device through the socket. This will block
		            // until it succeeds or throws an exception
		        	Gdx.app.log("Bluetooth", "Client trying to connect");
		            socket.connect();
		            Gdx.app.log("Bluetooth", "Client connected");
		        } catch (IOException connectException) {
		            // Unable to connect; close the socket and get out
		            try {
		                socket.close();
		            } catch (IOException closeException) { }
		            return;
		        }
		        // Create the Connection object and sign to the render thread
		        try {
		        	if (bluetoothConnection == null) {
		        		bluetoothConnection = new StreamConnection(socket.getInputStream(), socket.getOutputStream(), new BluetoothCloser(socket));
		        		state = ClientState.CONNECTED;
		        	}		        	
		        } catch (IOException e) {
		        	e.printStackTrace();
		        	state = ClientState.ERROR;
		        }
		    }
		 
		    /** Will cancel an in-progress connection, and close the socket */
		    public void cancel() {
		        try {
		            socket.close();
		        } catch (IOException e) { }
		    }
		}
	}
	
	/************************** END CLIENT CODE ******************************/
}
