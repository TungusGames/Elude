package tungus.games.elude;

import java.io.IOException;
import java.util.UUID;

import tungus.games.elude.BluetoothConnection;
import tungus.games.elude.BluetoothClient.State;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;

public class BluetoothServer {

	private static final UUID uuid = UUID.fromString("c0dedb1b-3668-4773-9fb2-efbbda550b72");
	
	public Activity app; //Used for Android API calls
	private BluetoothAdapter adapter; //Used for everything BT
	
	public static enum State {
		UNSUPPORTED, DISABLED, ENABLED, DISCOVERING, VISIBLE, ERROR
	}
	
	public State state;
	
	public BluetoothServer() {
		adapter = BluetoothAdapter.getDefaultAdapter();
		if (adapter == null)
			state = State.UNSUPPORTED;
		else {
			state = State.DISABLED;
		}
		
	}	
	
	public AcceptThread acceptThread;
	public BluetoothConnection bluetoothConnection;
	
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
	
	public class AcceptThread extends Thread {
		
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
	
}
