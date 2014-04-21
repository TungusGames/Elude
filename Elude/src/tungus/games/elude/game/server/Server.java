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
		this.world = new World(levelNum, isFinite);
	}
	
	@Override
	public void run() {
		while(!allNewData()) {
			try {
				wait(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		setupArrays();
		lastTime = TimeUtils.millis();
		while (true) { //TODO: when to end?
			while(!hasNewData()) {
				try {
					wait(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			readDirs();
			newTime = TimeUtils.millis();
			deltaTime = (newTime-lastTime) / 1000f;
			if (deltaTime > 0.05f) {
				Gdx.app.log("LagWarn", "Server deltaTime: " + deltaTime);
				deltaTime = 0.05f;
			}
			world.update(deltaTime, dirs);
			render.setFromWorld(world);
			for (Connection c : connections)
				c.write(render);
		}
	}
	
	private void readDirs() {
		for (int i = 0; i < connections.length; i++) {
			Connection c = connections[i];
			synchronized(c) {
				if (c.newest != null) {
					for (int j = 0; j < ((UpdateInfo)c.newest).positions.length; j++) {
						Vector2 d = ((UpdateInfo)c.newest).positions[j];
						dirs[dirOffset[i]+j].set(d);
					}
					c.newest = null;
				}
			}
		}
	}

	private void setupArrays() {
		int s = 0;
		for (Connection c : connections) {
			synchronized(c) {
				s += ((UpdateInfo)c.newest).positions.length;
			}
		}
		dirs = new Vector2[s];
		dirOffset = new int[s];
		s = 0;
		for (int i = 0; i < connections.length; i++) {
			Connection c = connections[i];
			synchronized(c) {
				dirOffset[i] = s;
				s += ((UpdateInfo)c.newest).positions.length;				
			}
		}
	}

	private boolean allNewData() {
		for(Connection c : connections) {
			if (c.newest == null)
				return false;
		}
		return true;
	}
	
	private boolean hasNewData() {
		for(Connection c : connections) {
			if (c.newest != null)
				return true;
		}
		return false;
	}

}
