package tungus.games.elude;

import tungus.games.elude.game.client.GameScreen;
import tungus.games.elude.menu.MainMenu;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;

public class HostBluetoothGameScreen extends BaseScreen {

	private enum State {
		STARTING, WAITING
	}
	
	private State state;
	private BluetoothConnector.Server server;
	
	private InputAdapter listener = new InputAdapter() {
		@Override
		public boolean keyDown(int keycode) {
			if (keycode == Keys.BACK || keycode == Keys.ESCAPE) {
				game.setScreen(new MainMenu(game));
				return true;
			}
			return false;
		}
	};
	
	public HostBluetoothGameScreen(Game game) {
		super(game);
	}

	@Override
	public void show() {
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setInputProcessor(listener);
		BluetoothConnector.INSTANCE = new BluetoothConnector();
		server = BluetoothConnector.INSTANCE.server = BluetoothConnector.INSTANCE.new Server();
		BluetoothConnector.INSTANCE.enable();
	}
	
	@Override
	public void render(float deltaTime) {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		if (server.state == BluetoothConnector.ServerState.ERROR) {
			Screen next = new MainMenu(game);
			game.setScreen(next);
		} else
		switch (state) {
			case STARTING:
				//TODO loading screen
				switch (server.state) {
					case ENABLED: 
						server.acceptThread = server.new AcceptThread();
						if (server.state != BluetoothConnector.ServerState.ERROR) {
							server.acceptThread.start();
							server.enableVisibility();
						} // On an error, the ERROR case is triggered in the next frame
						break;
					case VISIBLE:
						state = State.WAITING; //If BT visibility turn-on succeeded, continue to waiting state
					default: 
				}
				break;
			case WAITING:
				// TODO "Waiting..." message
				if (server.updateVisibility(deltaTime)) // If visibility time is up
					server.enableVisibility(); // Re-enable it
				if (server.state == BluetoothConnector.ServerState.CONNECTED) {
					Screen next = new GameScreen(game, 1, true);
					game.setScreen(next);
					// TODO Level selection, start game server
				}
		}
	}
	
	@Override
	public void hide() {
		if (server.acceptThread.isAlive())
			server.acceptThread.cancel();
	}
}
