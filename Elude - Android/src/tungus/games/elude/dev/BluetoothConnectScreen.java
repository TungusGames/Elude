package tungus.games.elude.dev;

import java.util.LinkedList;
import java.util.List;

import tungus.games.elude.BaseScreen;
import tungus.games.elude.dev.BluetoothConnector.Client;
import tungus.games.elude.dev.BluetoothConnector.ClientState;
import tungus.games.elude.dev.BluetoothConnector.Server;
import tungus.games.elude.game.client.GameScreen;
import tungus.games.elude.game.multiplayer.Connection;
import tungus.games.elude.game.multiplayer.LocalConnection.LocalConnectionPair;
import tungus.games.elude.menu.mainmenu.MainMenu;
import android.bluetooth.BluetoothDevice;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;

public class BluetoothConnectScreen extends BaseScreen {
	
	private static final int levelNum = 4;
	
	private final Client client;
	private final Server server;
	
	private boolean serverReady = false;
	private boolean clientReady = false;
	
	private final BTListUI gui = new BTListUI();
	private List<BluetoothDevice> devices = new LinkedList<BluetoothDevice>();
	
	private InputAdapter listener = new InputAdapter() {
		@Override
		public boolean keyDown(int keycode) {
			if (keycode == Keys.BACK || keycode == Keys.ESCAPE) {
				game.setScreen(new MainMenu(game));
				return true;
			}
			return false;
		}
		
		@Override
		public boolean touchDown (int screenX, int screenY, int pointer, int button) {
			int n = gui.tapSelection(screenX, screenY);
			if (n != -1) {
				client.connectTo(devices.get(n));
			}
			return false;
		}
	};
	
	public BluetoothConnectScreen(Game game) {
		super(game);
		BluetoothConnector.INSTANCE.enable();
		server = BluetoothConnector.INSTANCE.server;
		client = BluetoothConnector.INSTANCE.client;
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setInputProcessor(listener);
		
	}
	
	@Override
	public void render(float deltaTime) {
		updateServer();
		updateClient();
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (client.state != ClientState.DISCOVERING) {
			gui.renderMessage("LOADING");
		} else {
			gui.renderList(devices);
		}
	}
	
	public void updateServer() {
		switch (server.state) {
		case ERROR:
			game.setScreen(new MainMenu(game));			
			break;
		case ENABLED:
			if (!serverReady) {
				server.acceptThread = server.new AcceptThread();
				server.acceptThread.setName("Accept thread");
				server.acceptThread.start();
				server.enableVisibility();
				serverReady = true;		
			}
			break;
		case CONNECTED:
			LocalConnectionPair c = new LocalConnectionPair();
			Thread t = new Thread(new tungus.games.elude.game.server.Server(levelNum, false, new Connection[] {c.c1, BluetoothConnector.INSTANCE.bluetoothConnection}));
			t.setName("Server thread (multiplayer)");
			t.start();
			game.setScreen(new GameScreen(game, levelNum, false, c.c2, 0));
			//game.setScreen(new BluetoothTestSend(game, BluetoothConnector.INSTANCE.bluetoothConnection));
			break;
		default:
			break;
		}
	}
	
	public void updateClient() {
		switch (client.state) {
		case ERROR:
			game.setScreen(new MainMenu(game));
			break;
		case ENABLED:
			if (!clientReady) {
				Gdx.app.log("Bluetooth", "Starting discovery...");
				client.enableDiscovery();
				clientReady = true;				
			}
			break;
		case CONNECTED:
			game.setScreen(new GameScreen(game, levelNum, false, BluetoothConnector.INSTANCE.bluetoothConnection, 1));
			//game.setScreen(new BluetoothTestReceive(game, BluetoothConnector.INSTANCE.bluetoothConnection));
			break;
		case DISCOVERING:
			client.listDevices(devices);
			break;
		default:
			break;
		}
	}
	
	@Override
	public void hide() {
		if (server.acceptThread != null && server.acceptThread.isAlive())
			server.acceptThread.cancel();
		if (client.connectThread != null && client.connectThread.isAlive())
			client.connectThread.cancel();
		client.disableDiscovery();
	}
	
}