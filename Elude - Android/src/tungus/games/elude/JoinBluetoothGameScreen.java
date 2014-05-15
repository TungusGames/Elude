package tungus.games.elude;

import java.util.ArrayList;
import java.util.List;

import tungus.games.elude.menu.mainmenu.MainMenu;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;

public class JoinBluetoothGameScreen extends BaseScreen {

	private enum State {
		STARTING, BROWSE, CONNECTING
	}
	
	private State state = State.STARTING;
	
	private BluetoothConnector.Client client;
	private List<String> deviceList = new ArrayList<String>();
	
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
	
	public JoinBluetoothGameScreen(Game game) {
		super(game);
	}

	@Override
	public void show() {
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setInputProcessor(listener);
		BluetoothConnector.INSTANCE = new BluetoothConnector();
		client = BluetoothConnector.INSTANCE.client = BluetoothConnector.INSTANCE.new Client();
		BluetoothConnector.INSTANCE.enable();
	}
	
	@Override
	public void render(float deltaTime) {
		if (client.state == BluetoothConnector.ClientState.ERROR) {
			Screen next = new MainMenu(game);
			game.setScreen(next);
		} else
		switch (state) {
			case STARTING:
				Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT); // TODO loading screen
				if (client.state == BluetoothConnector.ClientState.ENABLED) {
					client.enableDiscovery();
					if (client.state == BluetoothConnector.ClientState.DISCOVERING) 
						state = State.BROWSE; // If BT turn-on succeeded, continue to running state
					//Otherwise we'll exit in the next frame (ERROR)
				}
				break;
			case BROWSE:
				break;
			case CONNECTING:
		}
	}
	
	@Override
	public void hide() {
		client.disableDiscovery();
		if (client.connectThread.isAlive())
			client.connectThread.cancel();
	}


}
