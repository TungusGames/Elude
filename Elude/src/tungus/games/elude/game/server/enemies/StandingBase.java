package tungus.games.elude.game.server.enemies;

import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.rockets.Rocket.RocketType;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public abstract class StandingBase extends Enemy {
	
	protected static final float DEFAULT_COLLIDER_SIZE = 0.6f;	
	protected static final float DEFAULT_SPEED = 4.5f;
			
	private final Vector2 targetPos;
	private boolean reachedTarget = false;
		
	private final float speed;
	
	protected StandingBase(Vector2 pos, EnemyType t, RocketType r, World w) {
		this(pos, t, r, w, t.hp, DEFAULT_SPEED, DEFAULT_COLLIDER_SIZE, World.EDGE);
	}
	
	protected StandingBase(Vector2 pos, EnemyType t, RocketType r, World w, float s, float collSize) {
		this(pos, t, r, w, t.hp, s, collSize, World.EDGE);
	}
	
	protected StandingBase(Vector2 pos, EnemyType t, RocketType r, World w, float hp, float s, float collSize, float edge) {
		super(pos, t, collSize, hp, w, r);
		speed = s;
		targetPos = new Vector2();
		getInnerTargetPos(pos, targetPos, edge);
		
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

	protected abstract boolean standingUpdate(float deltaTime);
	
	private final Vector2 getInnerTargetPos(Vector2 pos, Vector2 targetPos, float distFromEdge) {
		targetPos.x = MathUtils.random() * (World.WIDTH - 2*distFromEdge) + distFromEdge;
		targetPos.y = MathUtils.random() * (World.HEIGHT - 2*distFromEdge) + distFromEdge;
		
		float move = targetPos.x - pos.x;							// Get how much we can decrease the movement without
		if (pos.x < distFromEdge || pos.x > World.WIDTH-distFromEdge) {					 	// 		getting out of the "edge" frame
			float minMove = 0;
			if (pos.x < distFromEdge)
				minMove = distFromEdge - pos.x;
			else if (pos.x > World.WIDTH - distFromEdge) {
				minMove = World.WIDTH - distFromEdge - pos.x;
			}
			move -= minMove;
		}
		targetPos.x -= MathUtils.random(move);						// Decrease the movement by up to this value
		
		move = targetPos.y - pos.y;									// Do the same for Y
		if (pos.y < distFromEdge || pos.y > World.HEIGHT-distFromEdge) {
			float minMove = 0;
			if (pos.y < distFromEdge)
				minMove = distFromEdge - pos.y;
			else if (pos.y > World.HEIGHT - distFromEdge) {
				minMove = World.HEIGHT - distFromEdge - pos.y;
			}
			move -= minMove;
		}
		targetPos.y -= MathUtils.random(move);
		return targetPos;
	}
	

}
