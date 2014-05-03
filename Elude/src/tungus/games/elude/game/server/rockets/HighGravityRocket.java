package tungus.games.elude.game.server.rockets;

import tungus.games.elude.game.server.Vessel;
import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.enemies.Enemy;

import com.badlogic.gdx.math.Vector2;

public class HighGravityRocket extends Rocket {

	private static final float DEFAULT_TURNSPEED = 90;
	private static final float DEFAULT_G = 15;
	private static final float MIN_SPEED = 3;

	private static final Vector2 tempVector = new Vector2();

	private final float turnSpeed;
	private final float g;
	
	public HighGravityRocket(Enemy origin, Vector2 pos, Vector2 dir, World world, Vessel target,
			float turnSpeed, float g) {
		super(origin, RocketType.HIGHGRAV, pos, dir, world, target);
		this.turnSpeed = turnSpeed;
		this.g = g;
		vel.nor().scl(MIN_SPEED);
	}

	public HighGravityRocket(Enemy origin, Vector2 pos, Vector2 dir, World world, Vessel target) {
		this(origin, pos, dir, world, target, DEFAULT_TURNSPEED, DEFAULT_G);
	}

	@Override
	protected void aiUpdate(float deltaTime) {
		tempVector.set(target.pos).sub(pos);
		float angleDiff = tempVector.angle()-vel.angle();
		if (angleDiff < -180f) 
			angleDiff += 360;
		if (angleDiff > 180f)
			angleDiff -= 360;
		
		if (Math.abs(angleDiff) < turnSpeed* deltaTime) {
			vel.rotate(angleDiff);
		} else {
			vel.rotate(deltaTime * turnSpeed * Math.signum(angleDiff));
		}
		
		tempVector.set(target.pos).sub(pos);
		float r = tempVector.len();
		tempVector.scl(g / (r*r*r));	// Div by r once for normalizing, twice for the laws of gravity
		vel.add(tempVector.scl(deltaTime));
		
		float l = vel.len();
		if (l < MIN_SPEED) {
			vel.scl(MIN_SPEED / l);
		}
	}

}
