package tungus.games.elude.game.multiplayer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class StreamConnection extends Connection {

	private ObjectInputStream objInStream;
	private ObjectOutputStream objOutStream;
	private final Socket socket;

	private Thread thread = new Thread() {
		@Override
		public void run() {
			// Keep listening to the InputStream until an exception occurs
			while (true) {
				try {
					TransferData received = (TransferData)objInStream.readObject();
					// Read from the InputStream
					synchronized(StreamConnection.this) {
						newest = received;
						newest.handled = false;
					}
					// Send the obtained bytes to the UI activity
				} catch (Exception e) {
					break;
				}
			}
		}
	};

	public StreamConnection(Socket s) {
		try {
			objOutStream = new ObjectOutputStream(s.getOutputStream());
			objOutStream.flush();
			objInStream = new ObjectInputStream(s.getInputStream());
			thread.start();
		} catch (IOException e) {
			throw new GdxRuntimeException(e);
		} //TODO error handling
		socket = s;
	}

	/* Call this from the main activity to send data to the remote device */
	@Override
	public void write(TransferData data) {
		try {
			objOutStream.reset();
			objOutStream.writeUnshared(data);
		} catch (IOException e) {
			Gdx.app.log("SEND ERROR", "Failed to send data");
			e.printStackTrace();
		} //TODO exception handling
	}
	
	@Override
	public void close() {
		socket.dispose();
	}
}
