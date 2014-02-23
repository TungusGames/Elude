package tungus.games.dodge.game;

import java.util.ArrayList;
import java.util.List;

import tungus.games.dodge.Assets;
import tungus.games.dodge.game.rockets.Rocket;
import tungus.games.dodge.game.rockets.TurningRocketAI;

import com.badlogic.gdx.math.Vector2;

public class World {

	public static final float WIDTH = 20f;
	public static final float HEIGHT = 12f;
	
	public List<Vessel> vessels;
	public List<Rocket> rockets;
	
	public World() {
		vessels = new ArrayList<Vessel>();
		rockets = new ArrayList<Rocket>();
		
		vessels.add(new Vessel());
		rockets.add(new Rocket(new TurningRocketAI(vessels.get(0).pos, 90, 5), new Vector2(18, 10), new Vector2(-1, -1), this, Assets.rocket));
	}
	
	public void update(float deltaTime) {
		int size = vessels.size();
		for(int i = 0; i < size; i++) {
			vessels.get(i).update(deltaTime);
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
