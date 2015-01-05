package tungus.games.elude.game.server;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import tungus.games.elude.game.client.worldrender.renderable.Renderable;
import tungus.games.elude.game.server.enemies.Enemy;
import tungus.games.elude.levels.loader.EnemyLoader;
import tungus.games.elude.levels.loader.FiniteLevelLoader;
import tungus.games.elude.levels.loader.arcade.ArcadeLoaderBase;
import tungus.games.elude.levels.scoredata.ScoreData.ArcadeLevelScore;
import tungus.games.elude.levels.scoredata.ScoreData.FiniteLevelScore;
import tungus.games.elude.util.LinkedPool.Poolable;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class World {

	public static final float AREA = 20*12;
	public static float WIDTH = 20f;
	public static float HEIGHT = 12f;
	public static final float EDGE = 2.5f; 	// Width of the area at the edge not targeted for movement
	public static final float GAME_END_TIMEOUT = 3;
	public static final int STATE_PLAYING = 0;
	public static final int STATE_LOST = 1;
	public static final int STATE_WON = 2;
	
	public List<Vessel> vessels;
	public List<Updatable> updatables;
	public List<Updatable> addNextFrame;
	public List<Renderable> effects;

	public int enemyCount = 0;
	
	public static final Rectangle outerBounds = new Rectangle(0, 0, WIDTH, HEIGHT);
	public static final Rectangle innerBounds = new Rectangle(EDGE, EDGE, WIDTH-2*EDGE, HEIGHT-2*EDGE);
	
	public final EnemyLoader waveLoader;
	public final FreezeTimer freezeTimer;
	public FiniteLevelScore fScore = null;
	public ArcadeLevelScore aScore = null;
	
	public int state;
	
	public int levelNum;
	public boolean isFinite;
	
	public World(int levelNum, boolean finite) {
		Updatable.reset();
		vessels = new ArrayList<Vessel>();
		updatables = new LinkedList<Updatable>();
		addNextFrame = new LinkedList<Updatable>();
		effects = new LinkedList<Renderable>();		
		this.levelNum = levelNum;
		this.isFinite = finite;
		//vessels.add(new Vessel(this));
		//for (int i = 0; i < 10; i++)
		//	enemies.add(new MovingEnemy(new Vector2(MathUtils.random()*20, -1)));
		//	enemies.add(new StandingEnemy(new Vector2(MathUtils.random()*20, -1)));
		waveLoader = EnemyLoader.loaderFromLevelNum(this, levelNum, finite);
		freezeTimer = new FreezeTimer();
	}
	
	public static void calcBounds() {
		outerBounds.set(0, 0, WIDTH, HEIGHT);
		innerBounds.set(EDGE, EDGE, WIDTH-2*EDGE, HEIGHT-2*EDGE);
	}
	
	@SuppressWarnings("unchecked")
	public void update(float deltaTime, Vector2[] dirs) {
		while (!effects.isEmpty()) {
			((Poolable)(effects.remove(0))).free();
		}
		
		
		boolean isVesselAlive = false;
		for (int i = 0; i < vessels.size(); i++) {
			Vessel v = vessels.get(0);
			v.setInput(dirs[i]);
			if (!v.update(deltaTime)) {
				isVesselAlive = true;
			}
		}
		
		boolean gameContinuing = false;
		for (ListIterator<Updatable> it = updatables.listIterator(); it.hasNext();) {
			Updatable u = it.next();
			if (u.update(deltaTime)) {
				it.remove();
			} else if (u.keepsWorldGoing) {
				gameContinuing = true;
			}
		}
		
		while (!addNextFrame.isEmpty()) {
			((Deque<Updatable>)updatables).addFirst(addNextFrame.remove(0));
		}
		waveLoader.update(deltaTime);
		freezeTimer.update(deltaTime);
		if (!isVesselAlive || (!gameContinuing && waveLoader.isOver())) {
			state = vessels.get(0).hp <= 0 && isFinite ? STATE_LOST : STATE_WON;
			if (waveLoader instanceof ArcadeLoaderBase || vessels.get(0).hp > 0)
				waveLoader.saveScore();
			if (isFinite && fScore == null)
				fScore = ((FiniteLevelLoader)waveLoader).getScore();
			else if (!isFinite && aScore == null)
				aScore = ((ArcadeLoaderBase)waveLoader).getScore();
				
		}
	}
	
	/**
	 * Takes a random position from the perimeter of a rectangle outside the world's edge
	 * @param v The vector tol store the position
	 * @param dist How far the rectangle's sides should be outside the world
	 * @return The random position in <b>v</b> for chaining
	 */
	public Vector2 randomPosOnOuterRect(Vector2 v, float dist) {
		float longSides = 2*World.WIDTH + 4*dist;
		float shortSides = 2*World.HEIGHT + 4*dist;
		float f = MathUtils.random(longSides + shortSides);
		if (f > longSides) {
			f -= longSides;
			float side = shortSides/2;
			boolean leftSide = (f > side);
			if (leftSide)
				f -= side;
			v.set(leftSide ? -dist : WIDTH+dist, f);
		} else {
			float side = longSides/2;
			boolean bottomSide = (f > side);
			if (bottomSide)
				f -= side;
			v.set(f, bottomSide ? -dist : HEIGHT+dist);
		}
		return v;
	}
	
	/**
	 * Takes a random position inside the area of a rectangle in the middle of the world
	 * @param v The vector2 to store the position
	 * @param dist How far the rectangle's sides should be inside the world
	 * @return The random position in <b>v</b> for chaining
	 */
	public Vector2 randomPosInInnerRect(Vector2 v, float dist) {
		v.x = dist + MathUtils.random(WIDTH -2*dist);
		v.y = dist + MathUtils.random(HEIGHT-2*dist);
		return v;
	}
	
	/**
	 * @param v The vector to store the position
	 * @return {@code randomPosInInnerRect(v, World.EDGE)}
	 */
	public Vector2 randomPosInInnerRect(Vector2 v) {
		return randomPosInInnerRect(v, EDGE);
	}
	
	public void addEnemy(Enemy e) {
		enemyCount++;
		addNextFrame.add(e);
	}
}
