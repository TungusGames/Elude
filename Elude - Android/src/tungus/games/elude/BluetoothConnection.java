package tungus.games.elude;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import tungus.games.elude.game.multiplayer.Connection;
import android.bluetooth.BluetoothSocket;

public class BluetoothConnection extends Connection {
    private BluetoothSocket socket;
    private ObjectInputStream objInStream;
    private ObjectOutputStream objOutStream;
 

    private Thread thread = new Thread() {
    	@Override
    	public void run() {
    		// Keep listening to the InputStream until an exception occurs
    		while (true) {
    			try {
    				// Read from the InputStream
    				synchronized(this) {
        				newest = objInStream.readObject();
    				}
    				// Send the obtained bytes to the UI activity
    			} catch (Exception e) {
    				break;
    			}
    		}
    	}
    };
    
    public BluetoothConnection(BluetoothSocket socket) {
        this.socket = socket;
        try {
        	objInStream = new ObjectInputStream(socket.getInputStream());
        	objOutStream = new ObjectOutputStream(socket.getOutputStream());
        	thread.start();
        } catch (IOException e) { } //TODO error handling
    }
 
    /* Call this from the main activity to send data to the remote device */
    @Override
    public void write(Object o) {
        try {
            objOutStream.writeObject(o);
        } catch (IOException e) { } //TODO exception handling
    }
 
    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) { } //TODO exception handling
    }
}
