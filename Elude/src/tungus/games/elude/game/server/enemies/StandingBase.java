package tungus.games.elude.game.server.enemies;

import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.rockets.Rocket.RocketType;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public abstract class StandingBase extends Enemy {
	
	protected static final float DEFAULT_COLLIDER_SIZE = 0.6f;	
	protected static final float DEFAULT_SPEED = 4.5f;
	protected static final float DEFAULT_HP = 7;
		
	private static Vector2 tempVector = new Vector2();
	
	private final Vector2 targetPos;
	private boolean reachedTarget = false;
		
	private final float speed;
	
	public StandingBase(Vector2 pos, EnemyType t, RocketType r, World w) {
		this(pos, t, r, w, DEFAULT_HP, DEFAULT_SPEED, DEFAULT_COLLIDER_SIZE);
	}
	
	public StandingBase(Vector2 pos, EnemyType t, RocketType r, World w, float hp, float s, float collSize) {
		super(pos, t, collSize, hp, w, r);
		speed = s;
		targetPos = new Vector2();
		getInnerTargetPos(pos, targetPos);
		
		vel.set(targetPos).sub(pos).nor().scl(speed);
		turnGoal = vel.angle()-90;
		rot = turnGoal;
	}

	@Override
	protected boolean aiUpdate(float deltaTime) {
		if (!reachedTarget) {
			if (pos.dst2(targetPos) < speed*speed*deltaTime*deltaTime) {
				pos.set(targetPos);
				reachedTarget = true;
				vel.set(Vector2.Zero);
				timeSinceShot = 0;
			}
		} else {
			if (standingUpdate(deltaTime)) {
				return true;
			}
			
		}
		return false;
	}
	
	@Override
	protected float calcTurnGoal() {
		if (reachedTarget) {
			return tempVector.set(world.vessels.get(0).pos).sub(pos).angle()-90; // Turn towards player
		} else {
			return super.calcTurnGoal();
		}
	}

	protected abstract boolean standingUpdate(float deltaTime);
	
	private final Vector2 getInnerTargetPos(Vector2 pos, Vector2 targetPos) {
		targetPos.x = MathUtils.random() * (World.WIDTH - 2*World.EDGE) + World.EDGE;
		targetPos.y = MathUtils.random() * (World.HEIGHT - 2*World.EDGE) + World.EDGE;
		
		float move = targetPos.x - pos.x;							// Get how much we can decrease the movement without
		if (pos.x < World.EDGE || pos.x > World.WIDTH-World.EDGE) {					 	// 		getting out of the "edge" frame
			float minMove = 0;
			if (pos.x < World.EDGE)
				minMove = World.EDGE - pos.x;
			else if (pos.x > World.WIDTH - World.EDGE) {
				minMove = World.WIDTH - World.EDGE - pos.x;
			}
			move -= minMove;
		}
		targetPos.x -= MathUtils.random(move);						// Decrease the movement by up to this value
		
		move = targetPos.y - pos.y;									// Do the same for Y
		if (pos.y < World.EDGE || pos.y > World.HEIGHT-World.EDGE) {
			float minMove = 0;
			if (pos.y < World.EDGE)
				minMove = World.EDGE - pos.y;
			else if (pos.y > World.HEIGHT - World.EDGE) {
				minMove = World.HEIGHT - World.EDGE - pos.y;
			}
			move -= minMove;
		}
		targetPos.y -= MathUtils.random(move);
		return targetPos;
	}
	

}
