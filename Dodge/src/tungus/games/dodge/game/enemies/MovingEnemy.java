package tungus.games.dodge.game.enemies;

import tungus.games.dodge.Assets;
import tungus.games.dodge.game.World;
import tungus.games.dodge.game.rockets.LowGravityRocket;
import tungus.games.dodge.game.rockets.Rocket;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class MovingEnemy extends Enemy {
	
	private static final float DRAW_WIDTH = 0.8f;
	private static final float DRAW_HEIGHT = 1.05f;
	private static final float COLLIDER_SIZE = 0.5f;
	
	private static final float MAX_HP = 10f;
	private static final float SPEED = 3f;
	private static final float MAX_TURNSPEED = 100f;
	private static final float RELOAD = 2f;
	
	private static final int STATE_ARRIVING = 0;
	private static final int STATE_MOVING_INSIDE = 1;
	private static final int STATE_TURNING_IN = 2;
	
	private final Rectangle moveBounds;
	
	private int state = STATE_ARRIVING;
	private final Vector2 arrivePos;
	
	private float turnSpeed;
	private float turnAccel;
	private float turnInOneDir = 0;
	
	private boolean turningRight;
	
	private float timeSinceShot = 0;
	
	public MovingEnemy(Vector2 pos) {
		super(pos, COLLIDER_SIZE, DRAW_WIDTH, DRAW_HEIGHT, MAX_HP, Assets.movingEnemy);
		moveBounds = new Rectangle(2*World.EDGE, 2*World.EDGE, World.WIDTH-4*World.EDGE, World.HEIGHT-4*World.EDGE);
		arrivePos = new Vector2();
		arrivePos.x = MathUtils.clamp(pos.x, moveBounds.x, moveBounds.width+moveBounds.x);
		arrivePos.y = MathUtils.clamp(pos.y, moveBounds.y, moveBounds.height+moveBounds.y);
		vel.set(arrivePos).sub(pos).nor().scl(SPEED);
		turnGoal = vel.angle()-90;
		setRotation(turnGoal);
	}

	@Override
	protected void aiUpdate(float deltaTime) {
		switch (state) {
		case STATE_ARRIVING:
			if (arrivePos.dst2(pos) < SPEED*SPEED*deltaTime*deltaTime) {
				state = STATE_MOVING_INSIDE;
				turnSpeed = MathUtils.random(-60f, 60f);
				turnAccel = MathUtils.random(-60f, 60f);
			}
			break;
		case STATE_MOVING_INSIDE:
			/*float aa = MathUtils.random(-100, 100) * deltaTime;
			turnAccel += aa;
			if (turnSpeed > MAX_TURNSPEED)
				turnAccel -= 2*aa;
			else if (turnSpeed > -MAX_TURNSPEED && MathUtils.randomBoolean())
				turnAccel -= 2*aa;*/
			turnSpeed += turnAccel * deltaTime;
			
			if (turnInOneDir > 200) {
				turnSpeed = MathUtils.random(-60, 60);
				turnAccel = MathUtils.random(-60, 60);
			}
			
			if (pos.y > moveBounds.y + moveBounds.height) {
				state = STATE_TURNING_IN;
				turningRight = (vel.x > 0);
			} else if (pos.y < moveBounds.y) {
				state = STATE_TURNING_IN;
				turningRight = (vel.x < 0);
			} else if (pos.x > moveBounds.x + moveBounds.width) {
				state = STATE_TURNING_IN;
				turningRight = (vel.y < 0);
			} else if (pos.x < moveBounds.x) {
				state = STATE_TURNING_IN;
				turningRight = (vel.y > 0);
			}
			break;
		case STATE_TURNING_IN:
			turnSpeed = turningRight ? -MAX_TURNSPEED : MAX_TURNSPEED;
			if (moveBounds.contains(pos)) {
				state = STATE_MOVING_INSIDE;
				turnSpeed = MathUtils.random(-60, 60);
				turnAccel = MathUtils.random(-60, 60);
			}
			break;
		}
		if (state != STATE_ARRIVING) {
			if (turnSpeed > MAX_TURNSPEED)
				turnSpeed = MAX_TURNSPEED;
			else if (turnSpeed < -MAX_TURNSPEED)
				turnSpeed = -MAX_TURNSPEED;
			vel.rotate(turnSpeed * deltaTime);
			turnGoal = vel.angle()-90;
			timeSinceShot += deltaTime;
			if (turningRight && turnSpeed > 0 || !turningRight && turnSpeed < 0) {
				turnInOneDir = 0;
			}
			turningRight = (turnSpeed < 0);
			turnInOneDir += Math.abs(turnSpeed*deltaTime);
			if (timeSinceShot > RELOAD) {
				timeSinceShot -= RELOAD;
				World w = World.INSTANCE;
				Vector2 playerPos = w.vessels.get(0).pos;
				Rocket r = new LowGravityRocket(this, pos.cpy(), new Vector2(playerPos).sub(pos), w, Assets.rocket, playerPos);
				w.rockets.add(r);
			}
		}
	}

}
