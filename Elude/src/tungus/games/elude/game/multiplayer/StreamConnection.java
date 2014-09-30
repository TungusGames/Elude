package tungus.games.elude.game.multiplayer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class StreamConnection extends Connection {

	private ObjectInputStream objInStream;
	private ObjectOutputStream objOutStream;
	private final Disposable toClose;

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

	public StreamConnection(InputStream in, OutputStream out, Disposable c) {
		try {
			objOutStream = new ObjectOutputStream(out);
			objOutStream.flush();
			objInStream = new ObjectInputStream(in);
			thread.start();
		} catch (IOException e) {
			throw new GdxRuntimeException(e);
		} //TODO error handling
		toClose = c;
	}

	/* Call this from the main activity to send data to the remote device */
	@Override
	public void write(TransferData data) {
		try {
			objOutStream.reset();
			objOutStream.writeUnshared(data);
		} catch (IOException e) {
			Gdx.app.log("SEND ERROR", "Failed to send data");
		} //TODO exception handling
	}
	
	@Override
	public void close() {
		if (toClose != null) {
			toClose.dispose();
		}
	}
}
