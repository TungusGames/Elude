package tungus.games.elude.game.server.enemies;

import java.util.LinkedList;
import java.util.List;

import tungus.games.elude.Assets;
import tungus.games.elude.Assets.Tex;
import tungus.games.elude.game.client.worldrender.renderable.EnemyRenderable;
import tungus.games.elude.game.client.worldrender.renderable.Renderable;
import tungus.games.elude.game.client.worldrender.renderable.effect.DebrisAdder;
import tungus.games.elude.game.server.Updatable;
import tungus.games.elude.game.server.Vessel;
import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.rockets.Rocket;
import tungus.games.elude.game.server.rockets.Rocket.RocketType;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;
import tungus.games.elude.game.server.enemies.boss.FactoryBoss;

public abstract class Enemy extends Updatable implements Hittable {

	public static enum EnemyType {

		STANDING	 (Assets.Tex.STANDINGENEMY,	0.6f, 1, 	new float[]{0.1f,    1, 0.1f, 1f}, 	true, 	StandingEnemy.class, 	2), 
		MOVING		 (Assets.Tex.MOVINGENEMY, 	0.8f,1.05f,	new float[]{   1,    1, 0.2f, 1f}, 	true, 	MovingEnemy.class,		2), 
		KAMIKAZE	 (Assets.Tex.KAMIKAZE,		0.9f,0.85f,	new float[]{0.25f,0.25f,0.8f, 1f}, 	true,	Kamikaze.class,			2), 
		SHARPSHOOTER (Assets.Tex.SHARPSHOOTER,	1.05f,0.95f,new float[]{0.9f, 0.8f, 0.2f, 1f}, 	true, 	Sharpshooter.class,		2),
		MACHINEGUNNER(Assets.Tex.MACHINEGUNNER, 1.05f,0.8f,	new float[]{0.8f, 0.3f, 0.7f, 1f}, 	true, 	MachineGunner.class,	2),
		SHIELDED	 (Assets.Tex.SHIELDED,		1.3f,1.016f,new float[]{0.7f, 0.5f, 0.4f, 1f}, 	true, 	Shielded.class,			2),
		SPLITTER	 (Assets.Tex.SPLITTER,		1.00f,0.8f,	new float[]{0.5f, 0.5f, 0.5f, 1f}, 	true,	Splitter.class,			2),
		MINION		 (Assets.Tex.MINION,		0.65f,0.65f,new float[]{0.5f, 0.5f, 0.5f, 1f},	false,	Minion.class,			1),
		FACTORY		 (Assets.Tex.FACTORY,     	2.0f, 2.0f,	new float[]{0.5f, 0.5f, 0.5f, 1f}, 	true, 	Factory.class,			12),
		MINER		 (Assets.Tex.MINER,			0.9f, 0.9f, new float[]{1f,   1f,   1f,   1f},	true,	Miner.class,			2),
		BOSS_FACTORY (Assets.Tex.BOSS1,   		4.0f, 4.0f,	new float[]{1f,   1f,   1f,   1f}, 	true, 	FactoryBoss.class, 		100);
		public Tex tex;
		public float width;
		public float halfWidth;
		public float height;
		public float halfHeight;
		public boolean spawns;
		public float[] debrisColor;
		public float hp;
		public Class<? extends Enemy> mClass;
		EnemyType(Tex t, float w, float h, float[] c, boolean spawnsNormally, Class<? extends Enemy> cl, float hits) {
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

	public static final float DEFAULT_TURNSPEED = 540;
	protected float turnSpeed = DEFAULT_TURNSPEED;

	protected final World world;
	protected RocketType rocketType;

	public Vector2 pos;
	public Vector2 vel;
	public float rot = 0;
	public final EnemyType type;

	public final Circle collisionBounds;

	public final float maxHp;
	public float hp;

	public boolean countsForProgress;
	public boolean solid;

	protected float turnGoal;
	protected float timeSinceShot = 0f;

	protected static final Vector2 t = new Vector2();

	protected Enemy(Vector2 pos, EnemyType t, float boundSize, World w, RocketType type) {
		this(pos, t, boundSize, t.hp, w, type);
	}

	protected Enemy(Vector2 pos, EnemyType t, float boundSize, float hp, World w,
			RocketType rType) {
		this.rocketType = rType;
		this.type = t;
		this.pos = pos;
		this.world = w;
		this.vel = new Vector2(0,0);
		this.hp = maxHp = hp;
		this.collisionBounds = new Circle(pos, boundSize/2);
		this.keepsWorldGoing = true;
		this.countsForProgress = type.spawns;
		this.solid = true;
	}

	public boolean update(float deltaTime) {
		timeSinceShot += deltaTime;
		boolean subclassWantsDeath = aiUpdate(deltaTime);
		pos.add(vel.x * deltaTime, vel.y * deltaTime);
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
		if (solid) {
			pushCollidingVessels();
		}
		return subclassWantsDeath || hp <= 0;
	}
	
	public void shootRocket() {
		shootRocket(rocketType, new Vector2(targetPlayer().pos).sub(pos));
	}

	public void shootRocket(Vector2 dir) {
		shootRocket(rocketType, dir);
	}

	public void shootRocket(RocketType t, Vector2 dir) {
		timeSinceShot = 0;
		Rocket r = Rocket.fromType(t, this, pos.cpy(), dir, targetPlayer(), world);
		world.addNextFrame.add(r);
	}
	
	private void pushCollidingVessels() {
		for (Vessel v : world.vessels) {
			if (v.bounds.overlaps(collisionBounds)) {
				v.pos.sub(pos).nor().scl(collisionBounds.radius + v.bounds.radius).add(pos);
			}
		}
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
	public void killBy(Circle hitter) {
		if (!died) {
			world.waveLoader.onEnemyDead(this);
			if (hitter != null) {
				world.effects.add(DebrisAdder.create(type.debrisColor, id, pos.x, pos.y, pos.angle(new Vector2(hitter.x, hitter.y))));
			} else {
				world.effects.add(DebrisAdder.create(type.debrisColor, id, pos.x, pos.y, Float.NaN));
			}

			died = true;
			world.enemyCount--;
		}
	}

	@Override
	public boolean isHitBy(Circle c, float damage) {
		if (!died && collisionBounds.overlaps(c)) {
			takeDamage(damage);
			if (hp <= 0) {
				killBy(c);
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

	@Override
	public Renderable getRenderable() {
		return EnemyRenderable.create(id, hp / maxHp, type.tex, pos.x, pos.y, width(), height(), rot);
	}
}
