package tungus.games.elude.game.server;

import tungus.games.elude.game.client.RenderInfo;
import tungus.games.elude.game.multiplayer.Connection;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

public class Server implements Runnable {
	
	private final World world;
	private final Connection[] connections;
	private Vector2[] dirs;
	private int[] dirOffset;
	
	private long lastTime;
	private long newTime;
	private float deltaTime;
	
	private RenderInfo render = new RenderInfo();
	
	public Server(int levelNum, boolean isFinite, Connection[] connections) {
		this.connections = connections;
		for (Connection c : connections) {
			c.newest = new UpdateInfo();
		}
		this.world = new World(levelNum, isFinite);
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
		lastTime = TimeUtils.millis();
		while (true) { //TODO: when to end?
			while(!hasNewData()) {
				/*try {
					wait(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}*/
			}
			readDirs();
			newTime = TimeUtils.millis();
			deltaTime = (newTime-lastTime) / 1000f;
			if (deltaTime > 0.05f) {
				Gdx.app.log("LagWarn", "Server deltaTime: " + deltaTime);
				deltaTime = 0.05f;
			}
			lastTime = newTime;
			world.update(deltaTime, dirs);
			render.setFromWorld(world);
			for (int i = 0; i < render.hp.length; i++) {
				render.hp[i] = world.vessels.get(i).hp / Vessel.MAX_HP;
			}
			long timeA = TimeUtils.millis();
			for (Connection c : connections)
				c.write(render);
			Gdx.app.log("Server to client send time", TimeUtils.millis()-timeA + " ms");
			Gdx.app.log("Vessel count:", render.vessels.size()+"");
			Gdx.app.log("Enemy count:", render.enemies.size()+"");
		}
	}
	
	private void readDirs() {
		for (int i = 0; i < connections.length; i++) {
			Connection c = connections[i];
			synchronized(c) {
				if (!((UpdateInfo)c.newest).handled) {
					for (int j = 0; j < ((UpdateInfo)c.newest).directions.length; j++) {
						Vector2 d = ((UpdateInfo)c.newest).directions[j];
						dirs[dirOffset[i]+j].set(d);
					}
					((UpdateInfo)c.newest).handled = true;
				}
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
