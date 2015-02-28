package tungus.games.elude;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;

import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class BTSocket implements Socket {
	
	private BluetoothSocket androidBTSocket;
	
	public BTSocket(BluetoothSocket s) {
		androidBTSocket = s;
	}
	
	@Override
	public void dispose() {
		try {
			androidBTSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isConnected() {
		return androidBTSocket.isConnected();
	}

	@Override
	public InputStream getInputStream() {
		try {
			return androidBTSocket.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
			throw new GdxRuntimeException(e);
		}
	}

	@Override
	public OutputStream getOutputStream() {
		try {
			return androidBTSocket.getOutputStream();
		} catch (IOException e) {
			throw new GdxRuntimeException(e);
		}
	}

	@Override
	public String getRemoteAddress() {
		return androidBTSocket.getRemoteDevice().getAddress();
	}

}
