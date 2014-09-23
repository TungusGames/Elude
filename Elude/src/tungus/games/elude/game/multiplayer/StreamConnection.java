package tungus.games.elude.game.multiplayer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class StreamConnection extends Connection {
	
	private ObjectInputStream objInStream;
    private ObjectOutputStream objOutStream;
 

    private Thread thread = new Thread() {
    	@Override
    	public void run() {
    		// Keep listening to the InputStream until an exception occurs
    		while (true) {
    			try {
    				// Read from the InputStream
    				synchronized(StreamConnection.this) {
        				newest = (TransferData)objInStream.readObject();
    				}
    				// Send the obtained bytes to the UI activity
    			} catch (Exception e) {
    				break;
    			}
    		}
    	}
    };
    
    public StreamConnection(InputStream in, OutputStream out) {
        try {
        	objInStream = new ObjectInputStream(in);
        	objOutStream = new ObjectOutputStream(out);
        	thread.start();
        } catch (IOException e) { } //TODO error handling
    }
 
    /* Call this from the main activity to send data to the remote device */
    @Override
    public void write(TransferData data) {
        try {
            objOutStream.writeObject(data);
        } catch (IOException e) { } //TODO exception handling
    }
}
