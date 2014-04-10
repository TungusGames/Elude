package tungus.games.elude;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import android.bluetooth.BluetoothSocket;

public class BluetoothConnection extends Thread{
    private BluetoothSocket socket;
    private InputStream inStream;
    private OutputStream outStream;
    byte[] buffer = new byte[1024];  // buffer store for the stream
    int bytes; // bytes returned from read()
    public ByteBuffer bytebuffer;
 
    public BluetoothConnection(BluetoothSocket socket) {
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
