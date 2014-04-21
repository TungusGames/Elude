package tungus.games.elude.game.server.rockets;

import tungus.games.elude.Assets;
import tungus.games.elude.game.server.Vessel;
import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.enemies.Enemy;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class TurningRocket extends Rocket {
	
	private static final Vector2 tempVector2 = new Vector2();
	private static final float DEFAULT_TURNSPEED = 100;
	private static final float DEFAULT_SPEED = 4;
	
	private static final float FAST_SPEED = 7;
	private static final float FAST_TURNSPEED = 115;

	private final float turnSpeed;
	
	public TurningRocket(Enemy origin, Vector2 pos, Vector2 dir, World world, Vessel target, float turnSpeed, float speed) {
		super(origin, speed > 5 ? RocketType.FAST_TURNING : RocketType.SLOW_TURNING, pos, dir, world, target, initParticle(speed > 5));
		this.target = target;
		this.turnSpeed = turnSpeed;
		vel.nor().scl(speed);

	}
	
	public TurningRocket(Enemy origin, Vector2 pos, Vector2 dir, World world, TextureRegion texture, Vessel target) {
		this(origin, pos, dir, world, target, DEFAULT_TURNSPEED, DEFAULT_SPEED);
	}

	public TurningRocket(Enemy origin, Vector2 pos, Vector2 dir, World world, TextureRegion texture, Vessel target, boolean fast) {
		this(origin, pos, dir, world, target, fast ? FAST_TURNSPEED : DEFAULT_TURNSPEED, fast ? FAST_SPEED : DEFAULT_SPEED);
	}
	
	@Override
	public void aiUpdate(float deltaTime) {
		tempVector2.set(target.pos).sub(pos);
		float angleDiff = tempVector2.angle()-vel.angle();
		if (angleDiff < -180f) 
			angleDiff += 360;
		if (angleDiff > 180f)
			angleDiff -= 360;
		
		if (Math.abs(angleDiff) < turnSpeed* deltaTime) {
			vel.rotate(angleDiff);
		} else {
			vel.rotate(deltaTime * turnSpeed * Math.signum(angleDiff));
		}
	}
	
	private static final PooledEffect initParticle(boolean fast) {
		return fast ? Assets.fastFlameRocket.obtain() : Assets.flameRocket.obtain();
	}

}
