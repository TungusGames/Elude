package tungus.games.dodge.game.rockets;

import tungus.games.dodge.Assets;
import tungus.games.dodge.game.World;
import tungus.games.dodge.game.enemies.Enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class LowGravityRocket extends Rocket {
	
	private static final Vector2 tempVector = new Vector2();
	private static final float DEFAULT_TURNSPEED = 100;
	private static final float DEFAULT_MAX_SPEED = 7;
	private static final float DEFAULT_MIN_SPEED = 2;
	private static final float DEFAULT_SPEED_PER_DIST = 4f/20f;
	
	private final Vector2 playerPos;
	private final float turnSpeed;
	private final float maxSpeed;
	private final float minSpeed;
	private final float speedPerDist;
	
	private static final ParticleEffect initParticle() {
		ParticleEffect particle = new ParticleEffect();
		particle.load(Gdx.files.internal(Assets.PARTICLE_LOCATION + "rocket_2"), Assets.atlas);
		return particle;
	}
	
	public LowGravityRocket(Enemy origin, Vector2 pos, Vector2 dir, World world, TextureRegion texture, Vector2 playerPos) {
		this(origin, pos, dir, world, texture, playerPos, DEFAULT_MIN_SPEED, DEFAULT_MAX_SPEED, DEFAULT_SPEED_PER_DIST, DEFAULT_TURNSPEED, DEFAULT_DMG);
	}

	public LowGravityRocket(Enemy origin, Vector2 pos, Vector2 dir, World world, TextureRegion texture, 
						Vector2 playerPos, float minS, float maxS, float sPerD, float turnSpeed, float dmg) {
		super(origin, pos, dir, world, texture, dmg, initParticle());
		this.playerPos = playerPos;
		this.turnSpeed = turnSpeed;
		this.maxSpeed = maxS;
		this.minSpeed = minS;
		this.speedPerDist = sPerD;
		float speed = maxSpeed - playerPos.dst(pos)*speedPerDist;
		if (speed < minSpeed)
			speed = minSpeed;
		vel.nor().scl(speed);
		
	}

	@Override
	protected void aiUpdate(float deltaTime) {
		float speed = maxSpeed - playerPos.dst(pos)*speedPerDist;		// Mod speed
		if (speed < minSpeed)
			speed = minSpeed;
		vel.nor().scl(speed);
		
		tempVector.set(playerPos).sub(pos);								// Mod direction
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
