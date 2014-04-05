package tungus.games.elude;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import tungus.games.elude.multiplayer.BluetoothConnector;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;



/**
 * Class for managing Bluetooth connections
 * 
 * - Every device can act as a server xor as a client at once
 * - Sample usage in Elude's MultiplayerConnectScreen
 * - Use INSTANCE
 */
public class AndroidBluetoothConnector extends BluetoothConnector {

	public static AndroidBluetoothConnector INSTANCE = new AndroidBluetoothConnector();
	
	private static final UUID uuid = UUID.fromString("c0dedb1b-3668-4773-9fb2-efbbda550b72");
	
	public Activity app; //Used for Android API calls
	private BluetoothAdapter adapter; //Used for everything BT
	

	
	public AndroidBluetoothConnector() {
		adapter = BluetoothAdapter.getDefaultAdapter();
		if (adapter == null)
			state = State.UNSUPPORTED;
		else {
			state = State.DISABLED;
			acceptThread = new AndroidAcceptThread();
		}
		
	}	
	
	public void processActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT) {
			if (resultCode == Activity.RESULT_OK)
				state = State.ENABLED;
			else state = State.ERROR;
		}
		else if (requestCode == REQUEST_VISIBLE_BT) {
			if (resultCode != Activity.RESULT_CANCELED) {
				state = State.VISIBLE;
				visibilityTime = (float)resultCode;
			}
		}
	}
	
	/**************************** SERVER CODE ********************************/
	
	private float visibilityTime = 0f;
	private static final int REQUEST_VISIBLE_BT = 2;  // Used for activity results
	
	public void enableVisibility() { // enable() isn't needed before this
		Intent discoverableIntent = new	Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
	} 
	
	public boolean updateVisibility(float deltaTime) {
		visibilityTime -= deltaTime;
		if (visibilityTime <= 0f) {
			state = State.ENABLED;
			return true;
		}
		return false;
	}
	
	public class AndroidAcceptThread extends AcceptThread {
		
	    private BluetoothServerSocket serverSocket;
	 
	    @Override
	    public void start() {
	    	try {	        	
	        	// uuid is the app's UUID string, also used by the client code
	    		serverSocket = adapter.listenUsingRfcommWithServiceRecord("Elude", uuid);
	        } catch (IOException e) {
	        	throw new RuntimeException("Unable to listen as BT server");
	        	//TODO error message
	        }
	    	super.start();
	    }
	    
	    @Override
	    public void run() {
	        BluetoothSocket socket = null;
	        // Keep listening until exception occurs or a socket is returned
	        while (true) {
	            try {
	                socket = serverSocket.accept();
	                // If a connection was accepted
	                if (socket != null) {
	                	// Do work to manage the connection (in a separate thread)
	                	AndroidBluetoothConnector.INSTANCE.connectedThread = new ConnectedThread(socket);
	                	AndroidBluetoothConnector.INSTANCE.connectedThread.run();
	                	serverSocket.close();
		                break;
		            }
	            } catch (IOException e) {
	                break;
	            }
	            
	        }
	    }
	 
	    /** Will cancel the listening socket, and cause the thread to finish */
	    public void cancel() {
	        try {
	            serverSocket.close();
	        } catch (IOException e) {} //TODO error message
	    }
	};
	
	
	/**************************** END SERVER CODE ****************************/
	/****************************** CLIENT CODE ******************************/
	
	private static final int REQUEST_ENABLE_BT = 1;  // Used for activity results
	public Set<BluetoothDevice> discoveredDevices;
	
	public void enable() {
		if (!adapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			app.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
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
				discoveredDevices.add(device);
			}
		}
	};
	
	public boolean enableDiscovery() {
		if (adapter.startDiscovery()) {
			// Register the BroadcastReceiver
			IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
			app.registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
			state = State.DISCOVERING;
			return true;
		} else return false;
	}
	
	public boolean disableDiscovery() {
		if (adapter.cancelDiscovery()) {
			app.unregisterReceiver(mReceiver);
			state = State.ENABLED;
			return true;
		}
		else return false; /**TODO SOME SAFETY CHECK HERE*/
	}
	
	public ArrayList<String> getPairedDevices() {
		ArrayList<String> result = new ArrayList<String>();
		Set<BluetoothDevice> devices = adapter.getBondedDevices();
		Iterator<BluetoothDevice> iterator= devices.iterator();
		while (iterator.hasNext())
			result.add(iterator.next().getName());
		return result;
	}
	
	/** Gets nearby/paired devices with services with the same uuid
	 * e.g. hosted games*/
	public ArrayList<String> getGames() {
		ArrayList<String> result = new ArrayList<String>();
		if (state == State.ENABLED)
			;
		return result;
	}
	
	public class ConnectThread extends Thread {
	    private BluetoothSocket socket;
	 
	    public ConnectThread(BluetoothDevice device) {	 
	        // Get a BluetoothSocket to connect with the given BluetoothDevice
	        try {
	            // MY_UUID is the app's UUID string, also used by the server code
	            socket = device.createRfcommSocketToServiceRecord(uuid);
	        } catch (IOException e) { } //TODO error handling
	    }
	 
	    
	    
	    public void run() {
	        // Cancel discovery because it will slow down the connection
	        adapter.cancelDiscovery();
	        try {
	            // Connect the device through the socket. This will block
	            // until it succeeds or throws an exception
	            socket.connect();
	        } catch (IOException connectException) {
	            // Unable to connect; close the socket and get out
	            try {
	                socket.close();
	            } catch (IOException closeException) { }
	            return;
	        }
	 
	        // Do work to manage the connection (in a separate thread)
	        AndroidBluetoothConnector.INSTANCE.connectedThread = 
	        		new AndroidBluetoothConnector.ConnectedThread(socket);
	        AndroidBluetoothConnector.INSTANCE.connectedThread.run();
	    }
	 
	    /** Will cancel an in-progress connection, and close the socket */
	    public void cancel() {
	        try {
	            socket.close();
	        } catch (IOException e) { }
	    }
	}
	
	/************************** END CLIENT CODE ******************************/
	/************************** CONNECTION CODE ******************************/
	
	public class ConnectedThread extends BluetoothConnector.ConnectedThread {
	    private BluetoothSocket socket;
	    private InputStream inStream;
	    private OutputStream outStream;
	    byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()
        public ByteBuffer bytebuffer;
	 
	    public ConnectedThread(BluetoothSocket socket) {
	        this.socket = socket;
	        try {
	        	inStream = socket.getInputStream();
	        	outStream = socket.getOutputStream();
	        } catch (IOException e) { } //TODO error handling
	    }
	 
	    @Override
	    public void run() {
	        // Keep listening to the InputStream until an exception occurs
	        while (true) {
	            try {
	                // Read from the InputStream
	                bytes = inStream.read(buffer);
	                // Send the obtained bytes to the UI activity
	            } catch (IOException e) {
	                break;
	            }
	        }
	    }
	 
	    /* Call this from the main activity to send data to the remote device */
	    public void write(byte[] bytes) {
	        try {
	            outStream.write(bytes);
	        } catch (IOException e) { } //TODO exception handling
	    }
	 
	    /* Call this from the main activity to shutdown the connection */
	    public void cancel() {
	        try {
	            socket.close();
	        } catch (IOException e) { } //TODO exception handling
	    }
	}
	
	/************************ END CONNECTION CODE ****************************/
}
