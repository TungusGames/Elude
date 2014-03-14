package tungus.games.dodge.game.enemies;

import tungus.games.dodge.Assets;
import tungus.games.dodge.game.World;
import tungus.games.dodge.game.rockets.TurningRocket;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Kamikaze extends Enemy {
	
	private static final float DRAW_WIDTH = 0.9f;
	private static final float DRAW_HEIGHT = 0.85f;
	private static final float COLLIDER_SIZE = 0.6f;
	
	private static final float MAX_HP = 10f;
	private static final float SPEED = 3f;
	private static final float STANDING_TIME = 4f;
	private static final int ROCKETS_SHOT = 7;
	
	private final Vector2 targetPos;
	private boolean reachedTarget = false;
	
	private float timeStood = 0;
	
	public Kamikaze(Vector2 pos, World w) {
		super(pos, COLLIDER_SIZE, DRAW_WIDTH, DRAW_HEIGHT, MAX_HP, Assets.kamikaze, debrisFromColor(new float[]{0.1f,0.1f,0.6f,1}), w);
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
	protected boolean aiUpdate(float deltaTime) {
		if (!reachedTarget) {
			if (pos.dst2(targetPos) < SPEED*SPEED*deltaTime*deltaTime) {
				pos.set(targetPos);
				reachedTarget = true;
				vel.set(Vector2.Zero);
			}
		} else {
			timeStood += deltaTime;
			if (timeStood > STANDING_TIME) {
				explode();
				return true;
			}
		}
		return false;
	}

	private void explode() {
		kill(null);
		
		PooledEffect explosion = Assets.explosion.obtain();
		explosion.reset();
		explosion.setPosition(pos.x, pos.y);
		explosion.start();
		world.particles.add(explosion);
		
		for (int i = 0; i < ROCKETS_SHOT; i++) {
			world.rockets.add(new TurningRocket(this, pos.cpy(), new Vector2(1,0).rotate(MathUtils.random(360)), 
					world, Assets.rocket, world.vessels.get(0), true));
		}
	}

}
