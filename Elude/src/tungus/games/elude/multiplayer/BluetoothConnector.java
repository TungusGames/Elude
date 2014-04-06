package tungus.games.elude.multiplayer;

import java.util.ArrayList;

public abstract class BluetoothConnector {

	public static BluetoothConnector INSTANCE;
	
	public static enum State {
		UNSUPPORTED, DISABLED, ENABLED, DISCOVERING, VISIBLE, ERROR
	}
	
	public State state;
	
	public BluetoothConnector() {}

	public abstract void enableVisibility();
	public abstract boolean updateVisibility(float deltaTime);
	public class AcceptThread extends Thread {
		public void cancel() {}
	}
	public abstract void enable();
	public abstract boolean enableDiscovery();
	public abstract boolean disableDiscovery();
	public abstract ArrayList<String> getPairedDevices();
	public abstract class ConnectThread extends Thread {
		public abstract void cancel();
	}
	public abstract class ConnectedThread extends Thread {
		public abstract void write(byte[] bytes);
		public abstract void cancel();
	}
	
	public AcceptThread acceptThread;
	public ConnectThread connectThread;
	public ConnectedThread connectedThread;
}
