package tungus.games.dodge.game.rockets;

import com.badlogic.gdx.math.Vector2;

import tungus.games.dodge.game.rockets.Rocket.RocketAI;

public class TurningRocketAI implements RocketAI {
	
	private static final Vector2 tempVector2 = new Vector2();
	private static final float DEFAULT_TURNSPEED = 90;
	private static final float DEFAULT_SPEED = 4;
	private boolean firstTime = true;
	
	private Vector2 playerPos;
	private final float turnSpeed;
	private final float speed;
	
	public TurningRocketAI(Vector2 playerPos, float turnSpeed, float speed) {
		this.playerPos = playerPos;
		this.turnSpeed = turnSpeed;
		this.speed = speed;
	}
	
	public TurningRocketAI(Vector2 playerPos) {
		this(playerPos, DEFAULT_TURNSPEED, DEFAULT_SPEED);
	}
	
	@Override
	public void modVelocity(Vector2 pos, Vector2 vel, float deltaTime) {
		if (firstTime) {
			vel.nor().scl(speed);
		}
		tempVector2.set(playerPos).sub(pos);
		float angleDiff = tempVector2.angle()-vel.angle();
		if (angleDiff < -180f) 
			angleDiff += 360;
		if (angleDiff > 180f)
			angleDiff -= 360;
		
		if (Math.abs(angleDiff) < turnSpeed) {
			vel.rotate(angleDiff);
		} else {
			vel.rotate(deltaTime * turnSpeed * Math.signum(angleDiff));
		}
	}

}
