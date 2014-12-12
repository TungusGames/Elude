package tungus.games.elude.game.server;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import tungus.games.elude.game.multiplayer.transfer.RenderInfo.Effect;
import tungus.games.elude.game.server.enemies.Enemy;
import tungus.games.elude.game.server.pickups.Pickup;
import tungus.games.elude.game.server.rockets.Rocket;
import tungus.games.elude.levels.loader.EnemyLoader;
import tungus.games.elude.levels.loader.FiniteLevelLoader;
import tungus.games.elude.levels.loader.arcade.ArcadeLoaderBase;
import tungus.games.elude.levels.scoredata.ScoreData.ArcadeLevelScore;
import tungus.games.elude.levels.scoredata.ScoreData.FiniteLevelScore;

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
	public List<Rocket> rockets;
	public List<Enemy> enemies;
	public List<Enemy> enemiesToAdd;
	public List<Effect> effects;
	public List<Pickup> pickups;
	
	public static final Rectangle outerBounds = new Rectangle(0, 0, WIDTH, HEIGHT);
	public static final Rectangle innerBounds = new Rectangle(EDGE, EDGE, WIDTH-2*EDGE, HEIGHT-2*EDGE);
	
	public final EnemyLoader waveLoader;
	public FiniteLevelScore fScore = null;
	public ArcadeLevelScore aScore = null;
	
	public int state;
	
	public int levelNum;
	public boolean isFinite;
	
	public float freezeTime = 0f;
	
	public World(int levelNum, boolean finite) {
		vessels = new LinkedList<Vessel>();
		rockets = new LinkedList<Rocket>();
		enemies = new LinkedList<Enemy>();
		enemiesToAdd = new LinkedList<Enemy>();
		effects = new LinkedList<Effect>();
		pickups = new LinkedList<Pickup>();
		this.levelNum = levelNum;
		this.isFinite = finite;
		//vessels.add(new Vessel(this));
		//for (int i = 0; i < 10; i++)
		//	enemies.add(new MovingEnemy(new Vector2(MathUtils.random()*20, -1)));
		//	enemies.add(new StandingEnemy(new Vector2(MathUtils.random()*20, -1)));
		waveLoader = EnemyLoader.loaderFromLevelNum(this, levelNum, finite);
	}
	
	public static void calcBounds() {
		outerBounds.set(0, 0, WIDTH, HEIGHT);
		innerBounds.set(EDGE, EDGE, WIDTH-2*EDGE, HEIGHT-2*EDGE);
	}
	
	@SuppressWarnings("unchecked")
	public void update(float deltaTime, Vector2[] dirs) {
		effects.clear(); //TODO pool / trash?
		int size = vessels.size();
		boolean isVesselAlive = false;
		for(int i = 0; i < size; i++) {
			Vessel v = vessels.get(i);
			v.update(deltaTime, dirs[i]);
			isVesselAlive = isVesselAlive || v.hp > 0;
		}
		float deltaForEnemy = deltaTime;
		if (freezeTime > 0) {
			freezeTime -= deltaTime;
			deltaForEnemy = 0;
		}
		for (ListIterator<Enemy> it = enemies.listIterator(); it.hasNext(); ) {
			Enemy e = it.next();
			if (e.update(deltaForEnemy) || e.hp <= 0) {
				it.remove();
			}
		}
		while (!enemiesToAdd.isEmpty()) {
			((Deque<Enemy>)enemies).addFirst(enemiesToAdd.remove(0));
		}
		
		size = rockets.size();
		for (ListIterator<Rocket> it = rockets.listIterator(); it.hasNext(); ) {
			Rocket r = it.next();
			if (r.update(deltaTime)) {
				it.remove();
			}
		}
		
		size = pickups.size();
		for (ListIterator<Pickup> it = pickups.listIterator(); it.hasNext(); ) {
			Pickup p = it.next();
			if (p.update(deltaTime)) {
				it.remove();
			}
		}
		
		waveLoader.update(deltaTime);
		if (!isVesselAlive || (enemies.size() == 0 && rockets.size() == 0 && waveLoader.isOver())) {
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
}
