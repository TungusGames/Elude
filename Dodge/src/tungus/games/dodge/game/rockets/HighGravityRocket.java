package tungus.games.dodge.game.rockets;

import tungus.games.dodge.Assets;
import tungus.games.dodge.game.World;
import tungus.games.dodge.game.enemies.Enemy;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class HighGravityRocket extends Rocket {

	private static final float DEFAULT_TURNSPEED = 90;
	private static final float DEFAULT_G = 15;
	private static final float MIN_SPEED = 3;

	private static final Vector2 tempVector = new Vector2();

	private final float turnSpeed;
	private final float g;

	private final Vector2 playerPos;
	
	private static final PooledEffect initParticle() {
		return Assets.matrixRocket.obtain();
	}
	
	public HighGravityRocket(Enemy origin, Vector2 pos, Vector2 dir, World world, TextureRegion texture, Vector2 playerPos,
			float turnSpeed, float g) {
		super(origin, pos, dir, world, texture, DEFAULT_DMG, initParticle());
		this.playerPos = playerPos;
		this.turnSpeed = turnSpeed;
		this.g = g;
		vel.nor().scl(MIN_SPEED);
	}

	public HighGravityRocket(Enemy origin, Vector2 pos, Vector2 dir, World world, TextureRegion texture, Vector2 playerPos) {
		this(origin, pos, dir, world, texture, playerPos, DEFAULT_TURNSPEED, DEFAULT_G);
	}

	@Override
	protected void aiUpdate(float deltaTime) {
		tempVector.set(playerPos).sub(pos);
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
		
		tempVector.set(playerPos).sub(pos);
		float r = tempVector.len();
		tempVector.scl(g / (r*r*r));	// Div by r once for normalizing, twice for the laws of gravity
		vel.add(tempVector.scl(deltaTime));
		
		float l = vel.len();
		if (l < MIN_SPEED) {
			vel.scl(MIN_SPEED / l);
		}
	}

}
