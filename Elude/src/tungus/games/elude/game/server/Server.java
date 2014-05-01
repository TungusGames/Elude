package tungus.games.elude.game.server;

import tungus.games.elude.game.client.RenderInfo;
import tungus.games.elude.game.multiplayer.Connection;
import tungus.games.elude.util.log.AverageLogger;
import tungus.games.elude.util.log.FPSLogger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

public class Server implements Runnable {
	
	public static final int STATE_WAITING = 0;
	public static final int STATE_RUNNING = 1;
	public static final int STATE_OVER = 2;
	private int state = STATE_WAITING;
	
	private final FPSLogger fps = new FPSLogger("FPSLogger", "Server thread FPS: ");
	private final AverageLogger sendTime = new AverageLogger("SendLogger", "Server to client time (ms): ");
	
	private final World world;
	private final Connection[] connections;
	private Vector2[] dirs;
	private int[] dirOffset;
	
	private long lastTime;
	private long newTime;
	private float deltaTime;
	
	private RenderInfo render;
	
	public Server(int levelNum, boolean isFinite, Connection[] connections) {
		this.connections = connections;
		for (Connection c : connections) {
			c.newest = new UpdateInfo();
		}
		this.world = new World(levelNum, isFinite);
		render = new RenderInfo(world);
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
		while (state != STATE_OVER) { //TODO: when to end?
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
			switch (state) {
			case STATE_RUNNING:
				deltaTime = (newTime-lastTime) / 1000f;
				if (deltaTime > 0.05f) {
					Gdx.app.log("LagWarn", "Server deltaTime: " + deltaTime);
					deltaTime = 0.05f;
				}
				
				world.update(deltaTime, dirs);
				render.setFromWorld();
				break;
			}
			lastTime = newTime;
			
			for (int i = 0; i < render.hp.length; i++) {
				render.hp[i] = world.vessels.get(i).hp / Vessel.MAX_HP;
			}
			long sendStart = TimeUtils.millis();
			for (Connection c : connections)
				c.write(render);
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
				case STATE_WAITING:
					state = STATE_WAITING;
					break;
				case STATE_RUNNING:
					if (state == STATE_OVER) {
						state = STATE_RUNNING;
					}
					if (!u.handled) {
						for (int j = 0; j < u.directions.length; j++) {
							Vector2 d = u.directions[j];
							dirs[dirOffset[i]+j].set(d);
						}						
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
			if (((UpdateInfo)c.newest).handled)
				return false;
		}
		return true;
	}
	
	private boolean hasNewData() {
		for(Connection c : connections) {
			if (!((UpdateInfo)c.newest).handled)
				return true;
		}
		return false;
	}

}
