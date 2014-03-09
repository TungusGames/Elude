package tungus.games.dodge.game;

import java.util.ArrayList;
import java.util.List;

import tungus.games.dodge.game.enemies.Enemy;
import tungus.games.dodge.game.pickups.HealthPickup;
import tungus.games.dodge.game.pickups.Pickup;
import tungus.games.dodge.game.pickups.SpeedPickup;
import tungus.games.dodge.game.rockets.Rocket;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class World {
	
	public static World INSTANCE;

	public static final float WIDTH = 20f;
	public static final float HEIGHT = 12f;
	public static final float EDGE = 2f; 	// Width of the area at the edge not targeted for movement
	public static final float PICKUP_FREQ = 6f; //1 pickup / PICKUP_FREQ seconds
	
	public List<Vessel> vessels;
	public List<Rocket> rockets;
	public List<Enemy> enemies;
	public List<PooledEffect> particles;
	public List<Pickup> pickups;
	
	public final Rectangle outerBounds;
	public final Rectangle innerBounds;
	
	public float pickupDeltaTime;
	
	private final WaveLoader waveLoader;
	
	public World() {
		INSTANCE = this;
		vessels = new ArrayList<Vessel>();
		rockets = new ArrayList<Rocket>();
		enemies = new ArrayList<Enemy>();
		particles = new ArrayList<PooledEffect>();
		pickups = new ArrayList<Pickup>();
		pickupDeltaTime = 0f;
		vessels.add(new Vessel());
		//for (int i = 0; i < 10; i++)
		//	enemies.add(new MovingEnemy(new Vector2(MathUtils.random()*20, -1)));
		//	enemies.add(new StandingEnemy(new Vector2(MathUtils.random()*20, -1)));
		outerBounds = new Rectangle(0, 0, WIDTH, HEIGHT);
		innerBounds = new Rectangle(EDGE, EDGE, WIDTH-2*EDGE, HEIGHT-2*EDGE);
		waveLoader = new WaveLoader(Gdx.files.internal("levels/level.lvl"), this);
	}
	
	public void update(float deltaTime, Vector2[] dirs) {
		int size = vessels.size();
		for(int i = 0; i < size; i++) {
			vessels.get(i).update(deltaTime, dirs[i]);
		}
		
		size = enemies.size();
		for (int i = 0; i < size; i++) {
			enemies.get(i).update(deltaTime);
		}
		
		size = rockets.size();
		for (int i = 0; i < size; i++) {
			if (rockets.get(i).update(deltaTime)) {
				i--;
				size--;
			}
		}
		
		size = particles.size();
		for (int i = 0; i < size; i++) {
			particles.get(i).update(deltaTime);
		}
		
		size = pickups.size();
		for (int i = 0; i < size; i++) {
			if (pickups.get(i).update(deltaTime)) {
				i--;
				size--;
			}
		}
		
		pickupDeltaTime += deltaTime;
		if (pickupDeltaTime > PICKUP_FREQ)
		{
			if (MathUtils.randomBoolean())
				pickups.add(new HealthPickup(this, new Vector2(MathUtils.random(WIDTH - Pickup.DRAW_SIZE) ,
								MathUtils.random(HEIGHT - Pickup.DRAW_SIZE))));
			else pickups.add(new SpeedPickup(this, new Vector2(MathUtils.random(WIDTH - Pickup.DRAW_SIZE) ,
					MathUtils.random(HEIGHT - Pickup.DRAW_SIZE))));
			pickupDeltaTime = 0f;
		}
		
		waveLoader.update(deltaTime);
	}
	
	public Vector2 randomPosOutsideEdge(Vector2 v, float dist) {
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
}
