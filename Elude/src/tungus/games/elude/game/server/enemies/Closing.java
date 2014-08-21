package tungus.games.elude.game.server.enemies;

import tungus.games.elude.game.server.Vessel;
import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.rockets.Rocket.RocketType;

import com.badlogic.gdx.math.Vector2;

public class Closing extends Enemy {
	
	private static final float COLL_SIZE = 0.85f;
	private static final float GOAL_DISTANCE = 2f;
	private static final float GOAL_DISTANCE2 = GOAL_DISTANCE * GOAL_DISTANCE;
	private static final float SPEED = Vessel.MAX_SPEED * 0.6f;
	private static final float RELOAD = 2f;
	
	private Vector2 player;
	
	public Closing(Vector2 p, World w) {
		super(p, EnemyType.CLOSING, COLL_SIZE, 7f, w, RocketType.FAST_TURNING);
		player = w.vessels.get(0).pos;
	}

	@Override
	protected boolean aiUpdate(float deltaTime) {
		
		vel.set(player).sub(pos).nor().scl(SPEED);
		if (pos.dst2(player) < GOAL_DISTANCE2) {
			vel.scl(0.0001f);
		}
		if (timeSinceShot > RELOAD) {
			shootRocket(new Vector2(player).sub(pos));
		}
		return false;
	}
}
