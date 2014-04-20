package tungus.games.elude.game.server.rockets;

import tungus.games.elude.Assets;
import tungus.games.elude.game.server.Vessel;
import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.enemies.Enemy;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class LowGravityRocket extends Rocket {
	
	private static final Vector2 tempVector = new Vector2();
	private static final float DEFAULT_TURNSPEED = 100;
	private static final float DEFAULT_MAX_SPEED = 7;
	private static final float DEFAULT_MIN_SPEED = 2;
	private static final float DEFAULT_SPEED_PER_DIST = 4f/20f;
	
	private final float turnSpeed;
	private final float maxSpeed;
	private final float minSpeed;
	private final float speedPerDist;
	
	private static final PooledEffect initParticle() {
		return Assets.matrixRocket.obtain();
	}
	
	public LowGravityRocket(Enemy origin, Vector2 pos, Vector2 dir, World world, TextureRegion texture, Vessel target) {
		this(origin, pos, dir, world, texture, target, 
				DEFAULT_MIN_SPEED, DEFAULT_MAX_SPEED, DEFAULT_SPEED_PER_DIST, DEFAULT_TURNSPEED, DEFAULT_DMG, DEFAULT_LIFE);
	}

	public LowGravityRocket(Enemy origin, Vector2 pos, Vector2 dir, World world, TextureRegion texture, 
			Vessel target, float minS, float maxS, float sPerD, float turnSpeed, float dmg, float life) {
		super(origin, pos, dir, world, texture, target, dmg, life, initParticle());
		this.turnSpeed = turnSpeed;
		this.maxSpeed = maxS;
		this.minSpeed = minS;
		this.speedPerDist = sPerD;
		float speed = maxSpeed - target.pos.dst(pos)*speedPerDist;
		if (speed < minSpeed)
			speed = minSpeed;
		vel.nor().scl(speed);
		
	}

	@Override
	protected void aiUpdate(float deltaTime) {
		float speed = maxSpeed - target.pos.dst(pos)*speedPerDist;		// Mod speed
		if (speed < minSpeed)
			speed = minSpeed;
		vel.nor().scl(speed);
		
		tempVector.set(target.pos).sub(pos);								// Mod direction
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
	}

}
