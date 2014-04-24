package tungus.games.elude.game.server.enemies;

import tungus.games.elude.Assets;
import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.rockets.Rocket;
import tungus.games.elude.game.server.rockets.Rocket.RocketType;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

public abstract class Enemy {
	
	public static enum EnemyType {
		STANDING(Assets.standingEnemyGreen, 0.6f, 1, new float[]{0.1f,1,0.1f,1}), 
		MOVING(Assets.movingEnemyBlue, 0.8f, 1.05f, new float[]{0.1f,1,1,1}), 
		KAMIKAZE(Assets.kamikaze, 0.9f, 0.85f, new float[]{0.1f,0.1f,0.6f,1}), 
		STANDING_FAST(Assets.standingEnemyRed, 0.6f, 1, new float[]{0.6f, 0.1f, 0.1f, 1f}), 
		MOVING_MATRIX(Assets.movingEnemyGreen, 0.8f, 1.05f, new float[]{0.4f, 1f, 0.25f, 1f});
		public TextureRegion tex;
		public float width;
		public float halfWidth;
		public float height;
		public float halfHeight;
		public float[] debrisColor;
		EnemyType(TextureRegion t, float w, float h, float[] c) {
			tex = t; width = w; height = h; debrisColor = c; halfWidth = w/2; halfHeight = h/2;
		}
	}
	
	public static final float MAX_GRAPHIC_TURNSPEED = 540;
	
	public static final Enemy newEnemy(World w, EnemyType t) {
		Enemy e = null;
		switch (t) {
		case STANDING:
			e = new StandingEnemy(w.randomPosOutsideEdge(new Vector2(), 1), w);
			break;
		case MOVING:
			e = new MovingEnemy(w.randomPosOutsideEdge(new Vector2(), 1), w);
			break;
		case KAMIKAZE:
			e = new Kamikaze(w.randomPosOutsideEdge(new Vector2(), 1), w);
			break;
		case STANDING_FAST:
			e = new StandingEnemy(w.randomPosOutsideEdge(new Vector2(), 1), EnemyType.STANDING_FAST, w, Assets.standingEnemyRed, RocketType.FAST_TURNING, 2.5f, 4.5f);
			break;
		case MOVING_MATRIX:
			e = new MovingEnemy(w.randomPosOutsideEdge(new Vector2(), 1), EnemyType.MOVING_MATRIX, w, Assets.movingEnemyGreen, RocketType.LOWGRAV, 2.2f, 4.5f);
			break;
		default:
			throw new GdxRuntimeException("Unknown enemy type: " + t);
		}
		return e;
	}
	
	protected static final PooledEffect debrisFromColor(float[] color) {
		PooledEffect p = Assets.debris.obtain();
		Array<ParticleEmitter> emitters = p.getEmitters();
		for (int i = 0; i < emitters.size; i++) {
			float[] separateColor = (i == emitters.size-1) ? color : color.clone(); // Color for each emitter - last one uses up the original array
			float mul = //(float)MathUtils.random.nextGaussian()+1;
					MathUtils.random() + 0.5f;
			//mul *= mul;
			for (int j = 0; j < 3; j++) {
				// Randomly change the color slightly
				mul = MathUtils.random() + 0.5f;
				color[j] = MathUtils.clamp(separateColor[j]*mul, 0f, 1f);
			}
			emitters.get(i).getTint().setColors(separateColor);
		}
		return p;
	}
	
	protected final Vector2 getInnerTargetPos(Vector2 pos, Vector2 targetPos) {
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
		return targetPos;
	}
	
	protected final Rocket shootRocket(Vector2 dir) {
		return shootRocket(rocketType, dir);
	}
	
	protected final Rocket shootRocket(RocketType t, Vector2 dir) {
		Rocket r = Rocket.rocketFromType(t, this, pos.cpy(), dir, world.vessels.get(0), world);
		world.rockets.add(r);
		return r;
	}
	
	protected final World world;
	protected final RocketType rocketType;
	
	public Vector2 pos;
	public Vector2 vel;
	public float rot = 0;
	public final EnemyType type;
	
	public final Rectangle collisionBounds;
	
	public float hp;
	
	protected float turnGoal;
	
	public final PooledEffect onDestroy;
	
	public Enemy(Vector2 pos, EnemyType t, float boundSize, float drawWidth, float drawHeight, float hp, PooledEffect onDestroy, World w,
				 RocketType type) {
		this.rocketType = type;
		this.type = t;
		this.pos = pos;
		this.onDestroy = onDestroy;
		this.world = w;
		vel = new Vector2(0,0);
		this.hp = hp;
		collisionBounds = new Rectangle(pos.x - boundSize/2, pos.y - boundSize/2, boundSize, boundSize);
	}
	
	public final boolean update(float deltaTime) {
		boolean b = aiUpdate(deltaTime);
		pos.add(vel.x * deltaTime, vel.y * deltaTime);
		collisionBounds.x = pos.x - collisionBounds.width/2;
		collisionBounds.y = pos.y - collisionBounds.height/2;
		float diff = turnGoal - rot;
		if (diff < -180)
			diff += 360;
		if (diff > 180)
			diff -= 360;
		if (Math.abs(diff) < MAX_GRAPHIC_TURNSPEED * deltaTime)
			rot = turnGoal;
		else
			rot += Math.signum(diff) * MAX_GRAPHIC_TURNSPEED * deltaTime;
		return b;
	}
	
	protected abstract boolean aiUpdate(float deltaTime);
	
	public void kill(Rocket r) {
		onDestroy.setPosition(pos.x, pos.y);
		Array<ParticleEmitter> emitters = onDestroy.getEmitters();
		for (int i = 0; i < emitters.size; i++) {
			if (r != null) {
				emitters.get(i).getAngle().setLow(r.vel.angle());
				emitters.get(i).getAngle().setHigh(-90, 90);
			}
			else {
				emitters.get(i).getAngle().setHigh(-180, 180);
			}
		}
		onDestroy.start();
		world.particles.add(onDestroy);
		
		world.enemies.remove(this);
		world.waveLoader.onEnemyDead(this);
	}
	
}
