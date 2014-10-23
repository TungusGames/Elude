package tungus.games.elude.game.server.enemies;

import java.util.LinkedList;
import java.util.List;

import tungus.games.elude.Assets;
import tungus.games.elude.game.multiplayer.transfer.RenderInfoPool;
import tungus.games.elude.game.server.Vessel;
import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.rockets.Rocket;
import tungus.games.elude.game.server.rockets.Rocket.RocketType;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;

public abstract class Enemy {
	
	public static enum EnemyType {
		STANDING	 (Assets.standingEnemyGreen,0.6f, 1, 	 new float[]{0.1f,    1, 0.1f,  1}, true, StandingEnemy.class, 	2), 
		MOVING		 (Assets.movingEnemyBlue,   0.8f, 1.05f, new float[]{   1,    1, 0.2f,  1}, true, MovingEnemy.class,	2), 
		KAMIKAZE	 (Assets.kamikaze, 			0.9f, 0.85f, new float[]{0.25f,0.25f,0.8f,1  }, true, Kamikaze.class,		2), 
		SHARPSHOOTER (Assets.sharpshooter,	 	1.05f,0.95f, new float[]{0.9f, 0.8f, 0.2f, 1f}, true, Sharpshooter.class,	2),
		MACHINEGUNNER(Assets.machinegunner,		1.05f,0.8f,  new float[]{0.8f, 0.3f, 0.7f, 1f}, true, MachineGunner.class,	2),
		SHIELDED	 (Assets.shielded,			1.15f,0.86f, new float[]{0.7f, 0.5f, 0.4f, 1f}, true, Shielded.class,		2),
		SPLITTER	 (Assets.splitter,			1.00f,0.8f,  new float[]{0.5f, 0.5f, 0.5f, 1f}, true, Splitter.class,		2),
		MINION		 (Assets.splitter,			0.65f,0.65f, new float[]{0.5f, 0.5f, 0.5f, 1f}, false,Minion.class,			1),
		FACTORY		 (Assets.factory,           2.0f, 2.0f,  new float[]{0.5f, 0.5f, 0.5f, 1f}, true, Factory.class,		8),
		MINER		 (Assets.miner,				0.6f, 0.6f,  new float[]{ 1f,    1f,   1f, 1f}, true, Miner.class,			2);
		public TextureRegion tex;
		public float width;
		public float halfWidth;
		public float height;
		public float halfHeight;
		public boolean spawns;
		public float[] debrisColor;
		public float hp;
		public Class<? extends Enemy> mClass;
		EnemyType(TextureRegion t, float w, float h, float[] c, boolean spawnsNormally, Class<? extends Enemy> cl, float hits) {
			tex = t; width = w; height = h; debrisColor = c; halfWidth = w/2; halfHeight = h/2; 
			spawns = spawnsNormally; mClass = cl; hp = hits*Rocket.DEFAULT_DMG;
		}
		
		public static EnemyType[] normalSpawners() {
			EnemyType[] all = EnemyType.values();
			List<EnemyType> s = new LinkedList<EnemyType>();
			for (EnemyType t : all) {
				if (t.spawns) {
					s.add(t);
				}
			}
			EnemyType[] spawners = new EnemyType[s.size()];
			return s.toArray(spawners);
		}
	} 
	
	public static final Enemy fromType(World w, EnemyType t) {
		try {
			return (Enemy)(t.mClass.getConstructor(Vector2.class, World.class).newInstance(w.randomPosOnOuterRect(new Vector2(), 1), w));
		} catch (Exception ex) {
			Gdx.app.log("ERROR", "Enemy instantiation reflection magic failed.");
			throw new GdxRuntimeException(ex);
		}
	}
	
	protected final Rocket shootRocket() {
		return shootRocket(rocketType, new Vector2(targetPlayer().pos).sub(pos));
	}
	
	protected final Rocket shootRocket(Vector2 dir) {
		return shootRocket(rocketType, dir);
	}
	
	protected final Rocket shootRocket(RocketType t, Vector2 dir) {
		timeSinceShot = 0;
		Rocket r = Rocket.fromType(t, this, pos.cpy(), dir, targetPlayer(), world);
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
			return t.set(targetPlayer().pos).sub(pos).angle()-90;
		}
	}
	
	protected abstract boolean aiUpdate(float deltaTime);
	
	private boolean died = false;
	public void killByRocket(Rocket r) {
		if (!died) {
			world.effects.add(RenderInfoPool.newDebris(pos.x, pos.y, r != null ? r.vel.angle() : Float.NaN, type.ordinal()));
			world.waveLoader.onEnemyDead(this);
			died = true;
		}
	}
	
	public boolean hitBy(Rocket r) {
		if (!died && collisionBounds.overlaps(r.boundsForEnemy)) {
			takeDamage(r.dmg);
			if (hp <= 0) {
				killByRocket(r);
			}
			return true;
		}
		return false;
	}
	
	protected void takeDamage(float d) {
		float diff = (float)Math.min(hp, d);
		world.waveLoader.onEnemyHurt(this, diff);
		hp -= diff;
	}
	
	public float width() {
		return type.width;
	}

	public float height() {
		return type.height;
	}
	
	protected Vessel targetPlayer() {
		Vessel r = null;
		float bestDist = 10000;
		for (Vessel v : world.vessels) {
			float d = v.pos.dst2(pos);
			if (r == null || d < bestDist) {
				r = v;
				bestDist = d;
			}
		}
		return r;
	}
}
