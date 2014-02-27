package tungus.games.dodge.game.rockets;

import tungus.games.dodge.game.World;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class TurningGravityRocket extends Rocket {

	private static final float DEFAULT_TURNSPEED = 90;
	private static final float DEFAULT_G = 15;
	private static final float MIN_SPEED = 3;

	private static final Vector2 tempVector = new Vector2();

	private final float turnSpeed;
	private final float g;

	private final Vector2 playerPos;

	public TurningGravityRocket(Vector2 pos, Vector2 dir, World world, TextureRegion texture, Vector2 playerPos,
			float turnSpeed, float g) {
		super(pos, dir, world, texture);
		this.playerPos = playerPos;
		this.turnSpeed = turnSpeed;
		this.g = g;
		vel.nor().scl(MIN_SPEED);
	}

	public TurningGravityRocket(Vector2 pos, Vector2 dir, World world, TextureRegion texture, Vector2 playerPos) {
		this(pos, dir, world, texture, playerPos, DEFAULT_TURNSPEED, DEFAULT_G);
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
