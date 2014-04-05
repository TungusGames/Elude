package tungus.games.elude.multiplayer;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;

import tungus.games.elude.BaseScreen;
import tungus.games.elude.menu.MainMenu;

public class HostMpGameScreen extends BaseScreen {

	private enum State {
		STARTING, WAITING, CONNECTING
	}
	
	private State state;
	
	public HostMpGameScreen(Game game) {
		super(game);
		BluetoothConnector.INSTANCE.enableVisibility();
		
	}

	@Override
	public void render(float deltaTime) {
		switch (state) {
			case STARTING:
				Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT); //TODO loading screen
				if (BluetoothConnector.INSTANCE.state == BluetoothConnector.State.ENABLED) {
					state = State.WAITING; //If BT turn-on succeeded, continue to waiting state
					BluetoothConnector.INSTANCE.acceptThread.start();
				}
				else if (BluetoothConnector.INSTANCE.state == BluetoothConnector.State.ERROR) {
					/**ERROR MESSAGE NEEDED*/ 
					Screen next = new MainMenu(game);
					game.setScreen(next);
				}
				break;
		
		}
	}
	
	@Override
	public void hide() {
		if (BluetoothConnector.INSTANCE.acceptThread.isAlive())
			BluetoothConnector.INSTANCE.acceptThread.cancel();
	}
}
