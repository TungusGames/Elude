package tungus.games.dodge.game.rockets;

import tungus.games.dodge.game.World;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class TurningRocket extends Rocket {
	
	private static final Vector2 tempVector2 = new Vector2();
	private static final float DEFAULT_TURNSPEED = 100;
	private static final float DEFAULT_SPEED = 4;
	
	private Vector2 playerPos;
	private final float turnSpeed;
	
	public TurningRocket(Vector2 pos, Vector2 dir, World world, TextureRegion texture, Vector2 playerPos, float turnSpeed, float speed) {
		super(pos, dir, world, texture);
		this.playerPos = playerPos;
		this.turnSpeed = turnSpeed;
		vel.nor().scl(speed);

	}
	
	public TurningRocket(Vector2 pos, Vector2 dir, World world, TextureRegion texture, Vector2 playerPos) {
		this(pos, dir, world, texture, playerPos, DEFAULT_TURNSPEED, DEFAULT_SPEED);
	}
	
	@Override
	public void aiUpdate(float deltaTime) {
		tempVector2.set(playerPos).sub(pos);
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

}
