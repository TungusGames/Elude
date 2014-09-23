package tungus.games.elude.game.client;

import java.util.ArrayList;
import java.util.List;

import tungus.games.elude.BaseScreen;
import tungus.games.elude.game.client.input.Controls;
import tungus.games.elude.game.client.input.KeyControls;
import tungus.games.elude.game.multiplayer.Connection;
import tungus.games.elude.game.multiplayer.transfer.RenderInfo;
import tungus.games.elude.game.multiplayer.transfer.UpdateInfo;
import tungus.games.elude.game.server.Server;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;

public class FakeDebugClientScreen extends BaseScreen implements Runnable {
	
	public static final int STATE_STARTING = 4;
	public static final int STATE_PLAYING = 0;
	public static final int STATE_PAUSED = 1;
	public static final int STATE_LOST = 2;
	public static final int STATE_WON = 3;
	private int state = STATE_STARTING;
	private float timeSinceStart = 0;
	
	public static final int MENU_NOCHANGE = -1;
	public static final int MENU_NEXTLEVEL = -2;
	public static final int MENU_QUIT = -3;
	public static final int MENU_RESTART = -4;
	
	private static final float START_TIME = 2f;
	
	private static final Vector2 tmp = new Vector2();
	
	private final Connection connection;
		
	private List<Controls> controls;
	
	
	private RenderInfo render;
	private UpdateInfo update;
	
	@Override
	public void run() {
		while (state != STATE_LOST && state != STATE_WON) {
			long time1 = System.nanoTime();
			render(1/60f);
			float delta = (System.nanoTime()-time1)/1000000f;
			try {
				Thread.sleep(Math.max((long)(((1/60f)-delta)*1000),0));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public FakeDebugClientScreen(Game game, int levelNum, boolean finite, Connection connection) {
		super(game);		
		this.connection = connection;
		
		controls = new ArrayList<Controls>();
		update = new UpdateInfo();
		update.directions = new Vector2[1];
		for (int i = 0; i < update.directions.length; i++) {
			controls.add(new KeyControls(new int[] {Keys.DPAD_UP, Keys.DPAD_LEFT, Keys.DPAD_DOWN, Keys.DPAD_RIGHT}));			
			update.directions[i] = controls.get(i).getDir(tmp.set(0,0), 0);
		}
		render = new RenderInfo(null);
		render.hp = new float[update.directions.length];
		connection.newest = new RenderInfo(null);
	}
	

	@Override
	public void render(float deltaTime) {
		if (deltaTime > 0.05f) {
			Gdx.app.log("LagWarn", "DeltaTime: " + deltaTime);
			deltaTime = 0.05f;
		}		
		synchronized(connection) {
			if (!connection.newest.handled) {
				switch(connection.newest.info) {
				case STATE_PLAYING:
					connection.newest.copyTo(render);
					break;
				case STATE_WON:
					state = STATE_WON;
					break;
				case STATE_LOST:
					state = STATE_LOST;
					break;
				default:
					throw new IllegalArgumentException("Unexpected transferdata info value: " + connection.newest.info);
				}
				connection.newest.handled = true;
			}
		}
		
		switch (state) {
		case STATE_STARTING:
			if (timeSinceStart > START_TIME) {
				state = STATE_PLAYING;
			} else {
				timeSinceStart += deltaTime;
			}
			update.info = Server.STATE_WAITING_START;
			break;
		case STATE_PLAYING:
			for (int i = 0; i < update.directions.length; i++) {
				update.directions[i] = controls.get(i).getDir(tmp.set(render.vessels.get(i).x, render.vessels.get(i).y), deltaTime);
			}
			update.info = Server.STATE_RUNNING;
			break;
		case STATE_PAUSED:
			update.info = Server.STATE_PAUSED;
			break;
		case STATE_WON:
		case STATE_LOST:
			update.info = Server.STATE_OVER;
			break;
		}
		
		connection.write(update);
		
		// RENDER
		
	}
}