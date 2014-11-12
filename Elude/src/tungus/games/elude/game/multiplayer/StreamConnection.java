package tungus.games.elude.game.multiplayer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class StreamConnection extends Connection {
	
	//private final Object sendLock = new Object();
	
	private ObjectInputStream objInStream;
	private ObjectOutputStream objOutStream;
	private final Socket socket;
	
	private static class Timer implements Runnable {
		private int ms;
		private Thread t;
		public Timer(Thread t, int ms) {
			this.t = t;
			this.ms = ms;
		}
		@Override
		public void run() {
			try {
				Thread.sleep(ms);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			t.interrupt();
		}
	}
	
	private Thread thread = new Thread() {
		@Override
		public void run() {
			// Keep listening to the InputStream until an exception occurs
			while (true) {
				try {
					TransferData received = null;
					//synchronized(sendLock) {
					//	new Thread(new Timer(this, 1000)).start();
						received = (TransferData)objInStream.readObject();
					//}					
					// Read from the InputStream
					synchronized(StreamConnection.this) {
						newest = received;
						newest.handled = false;
					}
					// Send the obtained bytes to the UI activity
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
			}
		}
	};

	public StreamConnection(Socket s, boolean startRead) {
		try {
			objOutStream = new ObjectOutputStream(s.getOutputStream());
			objOutStream.flush();
			objInStream = new ObjectInputStream(s.getInputStream());
			thread.setName("StreamConnection read thread");
			if (startRead) {
				thread.start();
			}			
		} catch (IOException e) {
			throw new GdxRuntimeException(e);
		} //TODO error handling
		socket = s;
	}

	/* Call this from the main activity to send data to the remote device */
	@Override
	public void write(TransferData data) {
		try {
			//synchronized(sendLock) {
				objOutStream.reset();
				objOutStream.writeUnshared(data);
			//}
		} catch (IOException e) {
			Gdx.app.log("SEND ERROR", "Failed to send data");
			e.printStackTrace();
		} //TODO exception handling
	}
	
	@Override
	public void close() {
		socket.dispose();
	}
	
	public void startRead() {
		thread.start();
	}
}
