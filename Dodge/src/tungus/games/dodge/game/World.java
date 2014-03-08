package tungus.games.dodge.game;

import java.util.ArrayList;
import java.util.List;

import tungus.games.dodge.game.enemies.Enemy;
import tungus.games.dodge.game.enemies.MovingEnemy;
import tungus.games.dodge.game.enemies.StandingEnemy;
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
	
	public List<Vessel> vessels;
	public List<Rocket> rockets;
	public List<Enemy> enemies;
	public List<PooledEffect> particles;

	
	public final Rectangle outerBounds;
	public final Rectangle innerBounds;
	
	private final WaveLoader waveLoader;
	
	public World() {
		INSTANCE = this;
		vessels = new ArrayList<Vessel>();
		rockets = new ArrayList<Rocket>();
		enemies = new ArrayList<Enemy>();
		particles = new ArrayList<PooledEffect>();
		
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
			v.set(bottomSide ? -dist : HEIGHT+dist, f);
		}
		return v;
	}

}
