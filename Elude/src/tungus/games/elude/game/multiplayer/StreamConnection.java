package tungus.games.elude.game.multiplayer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class StreamConnection extends Connection {

	private ObjectInputStream objInStream;
	private ObjectOutputStream objOutStream;


	private Thread thread = new Thread() {
		@Override
		public void run() {
			// Keep listening to the InputStream until an exception occurs
			while (true) {
				try {
					Gdx.app.log("MPDEBUG", "Stream trying to read");
					TransferData received = (TransferData)objInStream.readObject();
					// Read from the InputStream
					synchronized(StreamConnection.this) {
						newest = received;
						newest.handled = false;
					}
					Gdx.app.log("MPDEBUG", "Stream managed to read");
					// Send the obtained bytes to the UI activity
				} catch (Exception e) {
					Gdx.app.log("MPDEBUG", "Stream in exc");
					break;
				}
			}
		}
	};

	public StreamConnection(InputStream in, OutputStream out) {
		try {
			Gdx.app.log("MPDEBUG", "Creating StreamC");
			objOutStream = new ObjectOutputStream(out);
			Gdx.app.log("MPDEBUG", "Stream out OK");
			objOutStream.flush();
			Gdx.app.log("MPDEBUG", "Stream out flushed");
			objInStream = new ObjectInputStream(in);
			Gdx.app.log("MPDEBUG", "Stream in OK");

			thread.start();
			Gdx.app.log("MPDEBUG", "Stream thread OK");
		} catch (IOException e) {
			throw new GdxRuntimeException(e);
		} //TODO error handling
	}

	/* Call this from the main activity to send data to the remote device */
	@Override
	public void write(TransferData data) {
		try {
			objOutStream.writeObject(data);
		} catch (IOException e) {
			throw new GdxRuntimeException(e);
		} //TODO exception handling
	}
}
