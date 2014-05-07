package tungus.games.elude.game.server;

import tungus.games.elude.game.client.GameScreen;
import tungus.games.elude.game.multiplayer.Connection;
import tungus.games.elude.game.multiplayer.Connection.TransferData;
import tungus.games.elude.game.multiplayer.transfer.ArcadeScoreInfo;
import tungus.games.elude.game.multiplayer.transfer.FiniteScoreInfo;
import tungus.games.elude.game.multiplayer.transfer.RenderInfo;
import tungus.games.elude.game.multiplayer.transfer.UpdateInfo;
import tungus.games.elude.levels.loader.FiniteLevelLoader;
import tungus.games.elude.levels.loader.arcade.ArcadeLoaderBase;
import tungus.games.elude.util.log.AverageLogger;
import tungus.games.elude.util.log.FPSLogger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

public class Server implements Runnable {
	
	public static final int STATE_WAITING_START = 0;
	public static final int STATE_RUNNING = 1;
	public static final int STATE_PAUSED = 2;
	public static final int STATE_OVER = 3;
	private int state = STATE_WAITING_START;
	
	private static final float END_DELAY = 2f;
	
	private final FPSLogger fps = new FPSLogger("FPSLogger", "Server thread FPS: ");
	private final AverageLogger sendTime = new AverageLogger("SendLogger", "Server to client time (ms): ");
	
	private final World world;
	private final Connection[] connections;
	private Vector2[] dirs;
	private int[] dirOffset;
	
	private long lastTime;
	private long newTime;
	private float deltaTime;
	private float timeSinceEnd = 0;
	
	private RenderInfo render;
	private TransferData sendData;
	
	public Server(int levelNum, boolean isFinite, Connection[] connections) {
		this.connections = connections;
		for (Connection c : connections) {
			c.newest = new UpdateInfo();
		}
		this.world = new World(levelNum, isFinite);
		sendData = render = new RenderInfo(world);
		sendData.info = GameScreen.STATE_PLAYING;
	}
	
	@Override
	public void run() {
		while(!allNewData()) {
			/*try {
				wait(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}*/
		}
		setupArrays();
		render.setFromWorld();
		lastTime = TimeUtils.millis();
		while (state != STATE_OVER) {
			fps.log();
			while(!hasNewData()) {
				/*try {
					wait(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}*/
			}			
			readInput();
			
			newTime = TimeUtils.millis();
			deltaTime = (newTime-lastTime) / 1000f;
			lastTime = newTime;
			
			if (state == Server.STATE_RUNNING) {
				if (deltaTime > 0.05f) {
					Gdx.app.log("LagWarn", "Server deltaTime: " + deltaTime);
					deltaTime = 0.05f;
				}
				world.update(deltaTime, dirs);
				switch (world.state) {
				case World.STATE_PLAYING:
					render.setFromWorld();
					break;
				case World.STATE_LOST:
					if ((timeSinceEnd += deltaTime) > END_DELAY) {
						sendData = new TransferData(GameScreen.STATE_LOST);
						state = STATE_OVER;
					} else {
						render.setFromWorld();
					}
					break;
				case World.STATE_WON:
					if ((timeSinceEnd += deltaTime) > END_DELAY) {
						sendData = world.isFinite ? 
								new FiniteScoreInfo(((FiniteLevelLoader)world.waveLoader).getScore()) :
								new ArcadeScoreInfo(((ArcadeLoaderBase) world.waveLoader).getScore());
						sendData.info = GameScreen.STATE_WON;
						state = STATE_OVER;
					} else {
						render.setFromWorld();
					}
				}
			}
			
			long sendStart = TimeUtils.millis();
			for (Connection c : connections)
				c.write(sendData);
			sendTime.log(TimeUtils.millis()-sendStart);
		}
		Gdx.app.log("Server", "Server stopped!");
	}
	
	private void readInput() {
		state = STATE_OVER;
		for (int i = 0; i < connections.length; i++) {
			Connection c = connections[i];
			UpdateInfo u = (UpdateInfo)c.newest;
			synchronized(c) {
				switch (u.info) {
				case STATE_WAITING_START:
					state = STATE_WAITING_START;
					break;
				case STATE_RUNNING:
					if (state != STATE_WAITING_START) {
						state = STATE_RUNNING;
					}
					if (!u.handled) {
						for (int j = 0; j < u.directions.length; j++) {
							Vector2 d = u.directions[j];
							dirs[dirOffset[i]+j].set(d);
						}						
					}
					break;
				case STATE_PAUSED:
					if (state == STATE_OVER) {
						state = STATE_PAUSED;
					}
					break;
				case STATE_OVER:
					break;
				}
				u.handled = true;
			}
		}
	}

	private void setupArrays() {
		int s = 0;
		for (Connection c : connections) {
			synchronized(c) {
				s += ((UpdateInfo)c.newest).directions.length;
			}
		}
		dirs = new Vector2[s];
		dirOffset = new int[s];
		s = 0;
		for (int i = 0; i < connections.length; i++) {
			Connection c = connections[i];
			synchronized(c) {
				dirOffset[i] = s;
				dirs[i] = new Vector2();
				s += ((UpdateInfo)c.newest).directions.length;				
			}
		}
		render.hp = new float[s];
		for (int i = 0; i < s; i++) {
			world.vessels.add(new Vessel(world));
		}
	}

	private boolean allNewData() {
		for(Connection c : connections) {
			if (c.newest.handled)
				return false;
		}
		return true;
	}
	
	private boolean hasNewData() {
		for(Connection c : connections) {
			if (!c.newest.handled)
				return true;
		}
		return false;
	}

}