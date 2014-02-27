package tungus.games.dodge.game;

import java.util.ArrayList;
import java.util.List;

import tungus.games.dodge.game.enemies.Enemy;
import tungus.games.dodge.game.enemies.StandingEnemy;
import tungus.games.dodge.game.rockets.Rocket;

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
	
	public final Rectangle bounds;
	
	public World() {
		vessels = new ArrayList<Vessel>();
		rockets = new ArrayList<Rocket>();
		enemies = new ArrayList<Enemy>();
		
		vessels.add(new Vessel());
		enemies.add(new StandingEnemy(new Vector2(MathUtils.random()*20, -1)));
		bounds = new Rectangle(0, 0, WIDTH, HEIGHT);

	}
	
	public void update(float deltaTime, Vector2[] dirs) {
		int size = vessels.size();
		for(int i = 0; i < size; i++) {
			vessels.get(i).update(deltaTime, dirs[i]);
		}
		
		size = enemies.size();
		for (int i = 0; i < size; i++) {
			Enemy e = enemies.get(i);
			if (e.hp <= 0) {
				enemies.remove(i);
				i--;
				size--;
				enemies.add(new StandingEnemy(new Vector2(MathUtils.random()*20, 13)));
				enemies.add(new StandingEnemy(new Vector2(MathUtils.random()*20, -1)));
				size += 2;
			} else {
				e.update(deltaTime);
			}		
		}
		
		size = rockets.size();
		for (int i = 0; i < size; i++) {
			if (rockets.get(i).update(deltaTime)) {
				rockets.remove(i);
				i--;
				size--;
			}
		}
		

	
	}

}
