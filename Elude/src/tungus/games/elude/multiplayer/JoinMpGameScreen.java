package tungus.games.elude.multiplayer;

import java.util.ArrayList;

import tungus.games.elude.BaseScreen;
import tungus.games.elude.menu.MainMenu;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;

public class JoinMpGameScreen extends BaseScreen {

	private enum State {
		STARTING, BROWSE_PAIRED, BROWSE_NEARBY, ERROR
	}
	
	private State state = State.STARTING;
	
	private ArrayList<String> deviceList = new ArrayList<String>();
	
	public JoinMpGameScreen(Game game) {
		super(game);
		BluetoothConnector.INSTANCE.enable(); //Turn on Bluetooth
	}

	@Override
	public void render(float deltaTime) {
		switch (state) {
		case STARTING:
			Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
			if (BluetoothConnector.INSTANCE.state == BluetoothConnector.State.ENABLED) {
				state = State.BROWSE_PAIRED; //If BT turn-on succeeded, continue to running state
				deviceList = BluetoothConnector.INSTANCE.getPairedDevices();
			}
			else if (BluetoothConnector.INSTANCE.state == BluetoothConnector.State.ERROR) {
				/**ERROR MESSAGE NEEDED*/ 
				Screen next = new MainMenu(game);
				game.setScreen(next);
			}
			break;
		case BROWSE_PAIRED: break;
		case BROWSE_NEARBY: break;
		case ERROR: break;
	}
	}
}
