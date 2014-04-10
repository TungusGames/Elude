package tungus.games.elude;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;

import tungus.games.elude.BaseScreen;
import tungus.games.elude.menu.MainMenu;

public class HostBtGameScreen extends BaseScreen {

	private enum State {
		STARTING, WAITING, CONNECTING
	}
	
	private State state;
	private BluetoothClient btc = BluetoothClient.INSTANCE;
	
	public HostBtGameScreen(Game game) {
		super(game);
		BluetoothClient.INSTANCE.enableVisibility();
		
	}

	@Override
	public void render(float deltaTime) {
		switch (state) {
			case STARTING:
				Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT); //TODO loading screen
				if (btc.state == BluetoothClient.State.ENABLED) {
					state = State.WAITING; //If BT turn-on succeeded, continue to waiting state
					//TODO not right instance
					btc.acceptThread = btc.new AcceptThread();
					btc.acceptThread.start();
				}
				else if (btc.state == BluetoothClient.State.ERROR) {
					// TODO ERROR MESSAGE NEEDED 
					Screen next = new MainMenu(game);
					game.setScreen(next);
				}
				break;
			case WAITING:
				
		}
	}
	
	@Override
	public void hide() {
		if (btc.acceptThread.isAlive())
			btc.acceptThread.cancel();
	}
}
