package tungus.games.elude.game.server.rockets;

import tungus.games.elude.game.server.Vessel;
import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.enemies.Enemy;

import com.badlogic.gdx.math.Vector2;

public class TurningRocket extends Rocket {
	
	private static final Vector2 tempVector2 = new Vector2();
	private static final float DEFAULT_TURNSPEED = 150;
	private static final float DEFAULT_SPEED = 5.5f;
	
	private static final float FAST_SPEED = 9f;
	private static final float FAST_TURNSPEED = 172;
	
	public TurningRocket(Enemy origin, RocketType type, Vector2 pos, Vector2 dir, World world, Vessel target) {
		super(origin, type, pos, dir, world, target);
	}
	
	@Override
	public void aiUpdate(float deltaTime) {
		target = targetPlayer(); //Needed?
		tempVector2.set(target.pos).sub(pos);
		float angleDiff = tempVector2.angle()-vel.angle();
		if (angleDiff < -180f) 
			angleDiff += 360;
		if (angleDiff > 180f)
			angleDiff -= 360;
		
		if (Math.abs(angleDiff) < type.turnSpeed * deltaTime) {
			vel.rotate(angleDiff);
		} else {
			vel.rotate(deltaTime * type.turnSpeed * Math.signum(angleDiff));
		}
	}
}
