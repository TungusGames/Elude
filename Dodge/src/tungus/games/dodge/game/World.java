package tungus.games.dodge.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tungus.games.dodge.game.enemies.Enemy;
import tungus.games.dodge.game.enemies.MovingEnemy;
import tungus.games.dodge.game.enemies.StandingEnemy;
import tungus.games.dodge.game.pickups.HealthPickup;
import tungus.games.dodge.game.pickups.Pickup;
import tungus.games.dodge.game.rockets.Rocket;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

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

	public Random rand;
	
	public final Rectangle outerBounds;
	public final Rectangle innerBounds;
	
	public float pickupDeltaTime;
	
	public World() {
		INSTANCE = this;
		vessels = new ArrayList<Vessel>();
		rockets = new ArrayList<Rocket>();
		enemies = new ArrayList<Enemy>();
		particles = new ArrayList<PooledEffect>();
		pickups = new ArrayList<Pickup>();
		pickupDeltaTime = 0f;
		rand = new Random(TimeUtils.millis());
		vessels.add(new Vessel());
		//for (int i = 0; i < 10; i++)
			enemies.add(new MovingEnemy(new Vector2(MathUtils.random()*20, -1)));
			enemies.add(new StandingEnemy(new Vector2(MathUtils.random()*20, -1)));
		outerBounds = new Rectangle(0, 0, WIDTH, HEIGHT);
		innerBounds = new Rectangle(EDGE, EDGE, WIDTH-2*EDGE, HEIGHT-2*EDGE);

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
			pickups.get(i).update(deltaTime);
		}
		
		pickupDeltaTime += deltaTime;
		if (pickupDeltaTime > PICKUP_FREQ)
		{
			pickups.add(new HealthPickup(this, new Vector2(rand.nextFloat() * WIDTH, rand.nextFloat() * HEIGHT)));
			pickupDeltaTime = 0f;
		}
	}

}
