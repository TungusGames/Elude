package tungus.games.dodge.game.enemies;

import tungus.games.dodge.Assets;
import tungus.games.dodge.game.World;
import tungus.games.dodge.game.rockets.Rocket;
import tungus.games.dodge.game.rockets.TurningRocket;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class StandingEnemy extends Enemy {
	
	private static final float DRAW_WIDTH = 0.6f;
	private static final float DRAW_HEIGHT = 1f;
	private static final float COLLIDER_SIZE = 0.5f;
	
	private static final float MAX_HP = 10f;
	private static final float SPEED = 4f;
	private static final float RELOAD = 2f;
	
	private static Vector2 tempVector = new Vector2();
	
	private final Vector2 targetPos;
	private boolean reachedTarget = false;
	
	private float timeSinceShot = 0f;
	private int shots = 0;
	
	boolean rocketType = false;
	
	public StandingEnemy(Vector2 pos) {
		super(pos, COLLIDER_SIZE, DRAW_WIDTH, DRAW_HEIGHT, MAX_HP, Assets.standingEnemy);
		targetPos = new Vector2();
		targetPos.x = MathUtils.random() * (World.WIDTH - 2*World.EDGE) + World.EDGE;
		targetPos.y = MathUtils.random() * (World.HEIGHT - 2*World.EDGE) + World.EDGE;
		
		float move = targetPos.x - pos.x;							// Get how much we can decrease the movement without
		if (pos.x < World.EDGE || pos.x > World.WIDTH-World.EDGE) {					 	// 		getting out of the "edge" frame
			float minMove = 0;
			if (pos.x < World.EDGE)
				minMove = World.EDGE - pos.x;
			else if (pos.x > World.WIDTH - World.EDGE) {
				minMove = World.WIDTH - World.EDGE - pos.x;
			}
			move -= minMove;
		}
		targetPos.x -= MathUtils.random(move);						// Decrease the movement by up to this value
		
		move = targetPos.y - pos.y;									// Do the same for Y
		if (pos.y < World.EDGE || pos.y > World.HEIGHT-World.EDGE) {
			float minMove = 0;
			if (pos.y < World.EDGE)
				minMove = World.EDGE - pos.y;
			else if (pos.y > World.HEIGHT - World.EDGE) {
				minMove = World.HEIGHT - World.EDGE - pos.y;
			}
			move -= minMove;
		}
		targetPos.y -= MathUtils.random(move);	
		
		vel.set(targetPos).sub(pos).nor().scl(SPEED);
		turnGoal = vel.angle()-90;
		setRotation(turnGoal);
		
	}

	@Override
	protected void aiUpdate(float deltaTime) {
		if (!reachedTarget) {
			if (pos.dst2(targetPos) < SPEED*SPEED*deltaTime*deltaTime) {
				pos.set(targetPos);
				reachedTarget = true;
				vel.set(Vector2.Zero);
			}
		} else {
			timeSinceShot += deltaTime;
			if (timeSinceShot > RELOAD) 
			{
				shots++;
				timeSinceShot -= RELOAD;
				World w = World.INSTANCE;
				Vector2 playerPos = w.vessels.get(0).pos;
				Rocket r = null;
				if (!(shots % 3 == 0))
					r = new TurningRocket(this, pos.cpy(), new Vector2(playerPos).sub(pos), w, Assets.rocket, playerPos, false);
				else
					r = new TurningRocket(this, pos.cpy(), new Vector2(playerPos).sub(pos), w, Assets.rocket, playerPos, true);
				w.rockets.add(r);
			}
			turnGoal = tempVector.set(World.INSTANCE.vessels.get(0).pos).sub(pos).angle()-90; // Turn towards player
		}
	}

}
