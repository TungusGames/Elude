package tungus.games.elude;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

import com.badlogic.gdx.backends.android.AndroidApplication;

public class BluetoothConnection {

	public static BluetoothConnection INSTANCE = new BluetoothConnection();
	
	public enum State {
		UNINITIALIZED, UNSUPPORTED, INITIALIZED, INIT_ENABLED, INIT_ENAB_VISIBLE
	}
	
	private static final int REQUEST_ENABLE_BT = 1; //Used for activities
	private static final int REQUEST_VISIBLE_BT = 2; 
	
	public static AndroidApplication app;
	private static BluetoothAdapter adapter;
	
	public State state = State.UNINITIALIZED;
	private float visibilityTime = 0f;
	
	public BluetoothConnection() {
		adapter = BluetoothAdapter.getDefaultAdapter();
		if (adapter == null)
			state = State.UNSUPPORTED;
		else state = State.INITIALIZED;
	}
	
	public boolean enable() {
		if (!adapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			app.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		try {
			wait();
		} catch (InterruptedException e) {
			throw new RuntimeException("Waiting for BT enabling interrupted");
		}
		if (state == State.INIT_ENABLED)
			return true;
		else return false;
	}
	
	public boolean enableVisibility() { // enable() isn't needed before this
		Intent discoverableIntent = new	Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
		app.startActivityForResult(discoverableIntent, REQUEST_VISIBLE_BT);
		try {
			wait();
		} catch (InterruptedException e) {
			throw new RuntimeException("Waiting for BT visibility enabling interrupted");
		}
		if (state == State.INIT_ENAB_VISIBLE)
			return true;
		else return false;
	} 
	
	public void processActivityResult(int requestCode, int resultCode) {
		if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
			state = State.INIT_ENABLED;
		}
		else if (requestCode == REQUEST_VISIBLE_BT) {
			state = State.INIT_ENAB_VISIBLE;
			
		}
		notifyAll();
	}
	
	public boolean updateVisibility(float deltaTime) {
		visibilityTime -= deltaTime;
		if (visibilityTime <= 0f) {
			state = State.INIT_ENABLED;
			return true;
		}
		return false;
	}
}
