package tungus.games.elude.game.server.enemies;

import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.rockets.Rocket.RocketType;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class MachineGunner extends Enemy {
	private static final int SHOTS_AT_ONCE = 3;
	private static final float SHORT_RELOAD = 0.5f;
	private static final float LONG_RELOAD = 5f;
	
	private static final float STRAIGHT_MIN = 0.5f;
	private static final float STRAIGHT_MAX = 2f;
	
	private static final float COLL = 0.85f;
	private static final float SPEED = 5f;
	
	private final Rectangle moveBounds;
	private boolean arrived = false;
	private float timeToTurn = 0;
	
	private int shots = 0;
	
	public MachineGunner(Vector2 pos, World w) {
		super(pos, EnemyType.MACHINEGUNNER, COLL, w, RocketType.FAST_TURNING);
		moveBounds = new Rectangle(World.EDGE, World.EDGE, World.WIDTH-2*World.EDGE, World.HEIGHT-2*World.EDGE);
		vel.set(MathUtils.random(moveBounds.width)+moveBounds.x, MathUtils.random(moveBounds.height)+moveBounds.y).sub(pos).nor().scl(SPEED);
	}

	@Override
	protected boolean aiUpdate(float deltaTime) {
		if (!arrived) {
			if (moveBounds.contains(pos)) {
				arrived = true;
				timeToTurn = STRAIGHT_MIN + MathUtils.random(STRAIGHT_MAX-STRAIGHT_MIN);
				timeSinceShot = LONG_RELOAD/2;
			}
			return false;
		} else {
			timeToTurn -= deltaTime;
			if (!moveBounds.contains(pos) || timeToTurn < 0) {
				turnAtEdge();
			}
			if (timeSinceShot > (shots == 0 ? LONG_RELOAD : SHORT_RELOAD)) {
				shootRocket();
				if (++shots == SHOTS_AT_ONCE) {
					shots = 0;
				}
			}
			return false;
		}
	}
	
	private void turn() {
		vel.rotate(90+MathUtils.random(180));
		timeToTurn = STRAIGHT_MIN + MathUtils.random(STRAIGHT_MAX-STRAIGHT_MIN);
	}
	
	private void turnAtEdge() {
		if (pos.x < moveBounds.x) {
			vel.set(-SPEED, 0);
		} else  if (pos.x > moveBounds.x + moveBounds.width) {
			vel.set(SPEED, 0);
		}
		if (pos.y < moveBounds.y) {
			vel.set(0, -SPEED);
		} else  if (pos.y > moveBounds.y + moveBounds.height) {
			vel.set(0, SPEED);
		}
		turn();
	}
}
