package tungus.games.elude.game.server.enemies;

import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.rockets.Rocket.RocketType;
import tungus.games.elude.game.server.rockets.StraightRocket;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class Sharpshooter extends Enemy {
	
	private static final float COLLIDER_SIZE = 0.5f;
	
	private static final float MAX_HP = 4f;
	private static final float SPEED = 3f;
	private static final float RELOAD = 2f;
	
	private static final Vector2 temp = new Vector2();
	
	private Vector2 targetPos = new Vector2();
	private boolean reachedTarget = false;
	private float timeSinceShot = 0;
	
	public Sharpshooter(Vector2 pos, World w) {
		super(pos, EnemyType.SHARPSHOOTER, COLLIDER_SIZE, MAX_HP, w, RocketType.STRAIGHT);
		targetPos = new Vector2();
		getInnerTargetPos(pos, targetPos);
		
		vel.set(targetPos).sub(pos).nor().scl(SPEED);
		turnGoal = vel.angle()-90;
		rot = turnGoal;
	}
	
	@Override
	protected boolean aiUpdate(float deltaTime) {
		if (!reachedTarget) {
			if (pos.dst2(targetPos) < SPEED*SPEED*deltaTime*deltaTime) {
				pos.set(targetPos);
				reachedTarget = true;
				vel.set(Vector2.Zero);
			}
		} else {
			timeSinceShot += deltaTime;
			if (timeSinceShot > RELOAD) 
			{
				timeSinceShot -= RELOAD;
				shootRocket(calcAngle());
			}
			turnGoal = temp.set(world.vessels.get(0).pos).sub(pos).angle()-90; // Turn towards player
		}
		return false;
	}
	
	private Vector2 calcAngle() {
		Vector2 p = world.vessels.get(0).pos;
		Vector2 v = world.vessels.get(0).vel;
		float dx = p.x-pos.x;
		float dy = p.y-pos.y;
		float q = dx/dy;
		float q2 = q*q;
		float s = StraightRocket.SPEED;
		float A = q2+1;
		float B = 2*q*v.x - 2*q2*v.y;
		float C = v.x*v.x + q2*v.y*v.y + 2*q*v.y*v.x - s*s;
		float twoA = 2*A;
		float mBPer2A = -B/twoA;
		float rootPer2A = (float)Math.sqrt(B*B-4*A*C)/(twoA);
		
		float y = mBPer2A + rootPer2A;
		float x = (float)Math.sqrt(s*s-y*y);
		float t = dy/(y-v.y);
		if (t > 0) {
			if (Math.abs(t-dx/(x-v.x)) < 0.05)
				return new Vector2(x, y);
			if (Math.abs(t-dx/(-x-v.x)) < 0.05)
				return new Vector2(-x, y);
		}
		
	
		y = mBPer2A - rootPer2A;
		x = (float)Math.sqrt(s*s-y*y);
		t = dy/(y-v.y);
		if (t > 0) {
			if (Math.abs(t-dx/(x-v.x)) < 0.05)
				return new Vector2(x, y);
			if (Math.abs(t-dx/(-x-v.x)) < 0.05)
				return new Vector2(-x, y);
		}
		Gdx.app.log("Sharpshooter", "Math failed! Approximating");
		Vector2 vec = new Vector2();
		float a = vel.angle() - vec.set(p).sub(pos).angle() + 90;
		float l = vec.len();
		return vec.rotate(MathUtils.sinDeg(a)*vel.len()/s*MathUtils.radiansToDegrees);
		//throw new GdxRuntimeException("Math failed");
	}

}
