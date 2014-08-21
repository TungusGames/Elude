package tungus.games.elude.game.server.enemies;

import tungus.games.elude.Assets;
import tungus.games.elude.game.multiplayer.transfer.RenderInfoPool;
import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.rockets.Rocket;
import tungus.games.elude.game.server.rockets.Rocket.RocketType;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

public abstract class Enemy {
	
	public static enum EnemyType {
		STANDING	 (Assets.standingEnemyGreen,0.6f, 1, 	 new float[]{0.1f,1,0.1f,1}), 
		MOVING		 (Assets.movingEnemyBlue,   0.8f, 1.05f, new float[]{1,1,0.2f,1}), 
		KAMIKAZE	 (Assets.kamikaze, 			0.9f, 0.85f, new float[]{0.25f,0.25f,0.8f,1}), 
		STANDING_FAST(Assets.standingEnemyRed,  0.6f, 1, 	 new float[]{0.6f, 0.1f, 0.1f, 1f}), 
		MOVING_MATRIX(Assets.movingEnemyGreen,  0.8f, 1.05f, new float[]{0.4f, 1f, 0.25f,  1f}),
		SHARPSHOOTER (Assets.sharpshooter,	 	1.05f,0.95f, new float[]{0.9f, 0.8f, 0.2f, 1f}),
		MACHINEGUNNER(Assets.machinegunner,		1.05f,0.8f,  new float[]{0.8f, 0.3f, 0.7f, 1f}),
		SHIELDED	 (Assets.shielded,			1.15f,0.86f, new float[]{0.7f, 0.5f, 0.4f, 1f}),
		ORBITER		 (Assets.orbiter,			1.4f, 0.7f,  new float[]{0.55f,0.7f, 0.1f, 1f}),		
		CLOSING		 (Assets.sharpshooter,		0.9f, 0.9f,  new float[]{0.7f, 0.8f, 0.3f, 1f});
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
	
	public static final Enemy fromType(World w, EnemyType t) {
		Enemy e = null;
		switch (t) {
		case STANDING:
			e = new StandingEnemy(w.randomPosOnOuterRect(new Vector2(), 1), w);
			break;
		case MOVING:
			e = new MovingEnemy(w.randomPosOnOuterRect(new Vector2(), 1), w);
			break;
		case KAMIKAZE:
			e = new Kamikaze(w.randomPosOnOuterRect(new Vector2(), 1), w);
			break;
		case STANDING_FAST:
			e = new StandingEnemy(w.randomPosOnOuterRect(new Vector2(), 1), EnemyType.STANDING_FAST, w, RocketType.FAST_TURNING, 4, 1.4f);
			break;
		case MOVING_MATRIX:
			e = new MovingEnemy(w.randomPosOnOuterRect(new Vector2(), 1), EnemyType.MOVING_MATRIX, w, RocketType.LOWGRAV, 2.5f, 2);
			break;
		case SHARPSHOOTER:
			e = new Sharpshooter(w.randomPosOnOuterRect(new Vector2(), 1), w);
			break;
		case MACHINEGUNNER:
			e = new MachineGunner(w.randomPosOnOuterRect(new Vector2(), 1), w);
			break;
		case SHIELDED:
			e = new Shielded(w.randomPosOnOuterRect(new Vector2(), 1), w);
			break;
		case ORBITER:
			e = new Orbiter(w.randomPosOnOuterRect(new Vector2(), 1), w);
			break;
		case CLOSING:
			e = new Closing(w.randomPosOnOuterRect(new Vector2(), 1), w);
			break;
		default:
			throw new IllegalArgumentException("Unknown enemy type: " + t);
		}
		return e;
	}
	
	
	
	protected final Rocket shootRocket(Vector2 dir) {
		return shootRocket(rocketType, dir);
	}
	
	protected final Rocket shootRocket(RocketType t, Vector2 dir) {
		timeSinceShot = 0;
		Rocket r = Rocket.fromType(t, this, pos.cpy(), dir, world.vessels.get(0), world);
		world.rockets.add(r);
		return r;
	}
	
	public static final float DEFAULT_TURNSPEED = 540;
	protected float turnSpeed = DEFAULT_TURNSPEED;
	
	private static int nextID = 0;
	public int id = nextID++;
	
	protected final World world;
	protected RocketType rocketType;
	
	public Vector2 pos;
	public Vector2 vel;
	public float rot = 0;
	public final EnemyType type;
	
	public final Circle collisionBounds;
	
	public final float maxHp;
	public float hp;
	
	protected float turnGoal;
	protected float timeSinceShot = 0f;
	
	protected static final Vector2 t = new Vector2();
		
	public Enemy(Vector2 pos, EnemyType t, float boundSize, float hp, World w,
				 RocketType type) {
		this.rocketType = type;
		this.type = t;
		this.pos = pos;
		this.world = w;
		vel = new Vector2(0,0);
		this.hp = maxHp = hp;
		collisionBounds = new Circle(pos, boundSize/2);
	}
	
	public boolean update(float deltaTime) {
		timeSinceShot += deltaTime;
		boolean b = aiUpdate(deltaTime);
		pos.add(vel.x * deltaTime, vel.y * deltaTime);
		//collisionBounds.x = pos.x - collisionBounds.width/2;
		//collisionBounds.y = pos.y - collisionBounds.height/2;
		collisionBounds.x = pos.x;
		collisionBounds.y = pos.y;
		turnGoal = calcTurnGoal();
		float diff = turnGoal - rot;
		if (diff < -180)
			diff += 360;
		if (diff > 180)
			diff -= 360;
		if (Math.abs(diff) < turnSpeed * deltaTime)
			rot = turnGoal;
		else
			rot += Math.signum(diff) * turnSpeed * deltaTime;
		return b;
	}
	
	protected float calcTurnGoal() {
		if (!vel.equals(Vector2.Zero)) {
			return vel.angle()-90;
		} else {
			return t.set(world.vessels.get(0).pos).sub(pos).angle();
		}
	}
	
	protected abstract boolean aiUpdate(float deltaTime);
	
	public void killByRocket(Rocket r) {
		world.effects.add(RenderInfoPool.newDebris(pos.x, pos.y, r != null ? r.vel.angle() : Float.NaN, type.ordinal()));
		world.enemies.remove(this);
		world.waveLoader.onEnemyDead(this);
	}
	
	public boolean isHitBy(Rocket r) {
		if (collisionBounds.overlaps(r.bounds)) {
			if ((hp -= r.dmg) <= 0) {
				killByRocket(r);
			}
			return true;
		}
		return false;
	}
	
}
