package tungus.games.elude.game.server.enemies;

import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.rockets.Rocket.RocketType;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Orbiter extends Enemy {
	
	private static final float COLL_SIZE = 0.85f;
	private static final float ROTATE_SPEED = 130;
	private static final float ORBIT_DISTANCE = 4f;
	private static final float ARRIVE_SPEED = 10f;
	private static final float RELOAD = 2f;
	
	private boolean arrived = false;
	private Vector2 fromPlayer = new Vector2();
	private Vector2 player;
	private Rectangle worldEdge;
	
	public Orbiter(Vector2 p, World w) {
		super(p, EnemyType.ORBITER, COLL_SIZE, 7f, w, RocketType.FAST_TURNING);
		player = w.vessels.get(0).pos;
		fromPlayer.set(player).sub(pos);
		worldEdge = new Rectangle(COLL_SIZE, COLL_SIZE, World.WIDTH - COLL_SIZE, World.HEIGHT - COLL_SIZE);
	}

	@Override
	protected boolean aiUpdate(float deltaTime) {
		fromPlayer.rotate(ROTATE_SPEED * deltaTime);
		if (!arrived) {
			float d = fromPlayer.len();
			d = Math.max(ORBIT_DISTANCE, d - ARRIVE_SPEED * deltaTime);
			fromPlayer.nor().scl(d);
			pos.set(player).add(fromPlayer);
			if (d == ORBIT_DISTANCE && worldEdge.contains(pos)) {
				arrived = true;
			}
		} else {
			pos.set(MathUtils.clamp(player.x + fromPlayer.x, worldEdge.x, worldEdge.x + worldEdge.width), 
					MathUtils.clamp(player.y + fromPlayer.y, worldEdge.y, worldEdge.y + worldEdge.height));
			if (timeSinceShot > RELOAD) {
				shootRocket(new Vector2(player).sub(pos));
			}
		}
		
		return false;
	}
	
	@Override
	protected float calcTurnGoal() {
		return fromPlayer.angle()+90;
	}
}
