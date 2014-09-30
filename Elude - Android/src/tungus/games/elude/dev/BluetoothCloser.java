package tungus.games.elude.dev;

import java.io.IOException;

import android.bluetooth.BluetoothSocket;

import com.badlogic.gdx.utils.Disposable;

public class BluetoothCloser implements Disposable {
	private final BluetoothSocket socket;
	
	public BluetoothCloser(BluetoothSocket s) {
		socket = s;
	}
	
	@Override
	public void dispose() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
