package tungus.games.elude.game.server.enemies;

import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.rockets.Rocket.RocketType;
import tungus.games.elude.game.server.rockets.StraightRocket;

import com.badlogic.gdx.math.Vector2;

public class Sharpshooter extends StandingBase {
	
	private static final float COLLIDER_SIZE = 0.75f;
	private static final float RELOAD = 2f;
			
	public Sharpshooter(Vector2 pos, World w) {
		super(pos, EnemyType.SHARPSHOOTER, RocketType.STRAIGHT, w, StandingBase.DEFAULT_HP, StandingBase.DEFAULT_SPEED, COLLIDER_SIZE);
	}
	
	@Override
	protected boolean standingUpdate(float deltaTime) {
		if (timeSinceShot > RELOAD) {
			shootRocket(calcAngle());
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
		
		// Math sometimes fails when the target is very close (float limits messing?), approximate
		return new Vector2(p).sub(pos);
	}

}
