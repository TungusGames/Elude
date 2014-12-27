package tungus.games.elude.game.server.rockets;

import tungus.games.elude.Assets;
import tungus.games.elude.Assets.Particles;
import tungus.games.elude.game.client.worldrender.renderable.CamShake;
import tungus.games.elude.game.client.worldrender.renderable.ParticleAdder;
import tungus.games.elude.game.client.worldrender.renderable.ParticleRemover;
import tungus.games.elude.game.client.worldrender.renderable.Renderable;
import tungus.games.elude.game.client.worldrender.renderable.RocketRenderable;
import tungus.games.elude.game.server.Updatable;
import tungus.games.elude.game.server.Vessel;
import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.enemies.Enemy;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;

public abstract class Rocket extends Updatable {
	
	public interface Hittable {
		public boolean isHitBy(Rocket r);
	}
	
	public static enum RocketType { 
		SLOW_TURNING(Assets.Particles.FLAME_ROCKET), 
		FAST_TURNING(Assets.Particles.MATRIX_ROCKET), 
		STRAIGHT(Assets.Particles.STRAIGHT_ROCKET),
		MINE(Assets.Particles.FLAME_ROCKET/*null*/);
		public Particles effect;
		RocketType(Particles e) {
			effect = e;
		}
	};
	
	public static final float ROCKET_SIZE = 0.1f; // Diameter
	public static final float DEFAULT_DMG = 3f;
	public static final float DEFAULT_LIFE = 6f;
	
	public static final Rocket fromType(RocketType t, Enemy origin, Vector2 pos, Vector2 dir, Vessel target, World w) {
		Rocket r = null;
		switch(t) {
		case SLOW_TURNING:
			r = new TurningRocket(origin, pos, dir, w, target);
			break;
		case FAST_TURNING:
			r = new TurningRocket(origin, pos, dir, w, target, true);
			break;
		case STRAIGHT:
			r = new StraightRocket(origin, pos, dir, w, target);
			break;
		case MINE:
			r = new Mine(origin, pos, dir, w, target);
			break;
		default:
			throw new IllegalArgumentException("Unknown rocket type: " + t);
		}
		return r;
	}
	
	private static int nextID = 0;
	
	private World world;
	private Enemy origin;
	
	public Vessel target;
	
	public Vector2 pos;
	public Vector2 vel;
	public Circle boundsForEnemy;
	public Circle boundsForVessel;
	public final RocketType type;
	public final int id;
	
	private boolean outOfOrigin = false;
	
	public final float dmg;
	
	private float life;
	
	public Rocket(Enemy origin, RocketType t, Vector2 pos, Vector2 dir, World world, Vessel target) {
		this(origin, t, pos, dir, world, target, DEFAULT_DMG, DEFAULT_LIFE);
	}
	
	public Rocket(Enemy origin, RocketType t, Vector2 pos, Vector2 dir, World world, Vessel target, float dmg, float life) {
		this(origin, t, pos, dir, world, target, dmg, life, ROCKET_SIZE, ROCKET_SIZE);
	}
	public Rocket(Enemy origin, RocketType t, Vector2 pos, Vector2 dir, World world, Vessel target, float dmg, float life, float sizeForEnemy, float sizeForVessel) {
		super();
		this.origin = origin;
		this.type = t;
		this.life = life;
		this.pos = pos;
		this.world = world;
		this.boundsForEnemy = new Circle(pos, sizeForEnemy/2);
		this.boundsForVessel = new Circle(pos, sizeForVessel/2);
		this.dmg = dmg;
		this.target = target;
		this.id = nextID++;
		this.keepsWorldGoing = true;
		vel = dir;
	}
	
	public final boolean update(float deltaTime) {
		life -= deltaTime;
		// If there are no enemies alive, speed up the pace by dying twice as fast
		if (world.enemyCount == 0) { 
			life -= deltaTime;
		}
		if (life <= 0) {
			kill();
			return true;
		}
		aiUpdate(deltaTime);
		pos.add(vel.x * deltaTime, vel.y * deltaTime);
		boundsForEnemy.x = pos.x;
		boundsForEnemy.y = pos.y;
		boundsForVessel.x = pos.x;
		boundsForVessel.y = pos.y;
		if (!World.outerBounds.contains(pos)) {
			if (hitWall(pos.x < 0 || pos.x > World.WIDTH)) {
				return true;
			}
		}
		
		boolean stillIn = false;
		for (Updatable u : world.updatables) {
			if (!outOfOrigin && u == origin) {
				stillIn = (origin.collisionBounds.overlaps(boundsForVessel));
				continue;
			}
			if (u instanceof Hittable && ((Hittable)u).isHitBy(this)) {
				kill();
				return true;
			}
		}
		if (!stillIn) {
			outOfOrigin = true;
		}
		
		for (Vessel v : world.vessels) {
			if (Intersector.overlaps(v.bounds, boundsForVessel)) {
				if (!v.shielded) {
					world.effects.add(CamShake.create());
					v.hp -= dmg;
				}
				kill();
				return true;
			}
		}
		return false;
	}
	
	public void kill() {
		world.effects.add(ParticleRemover.create(id));
		world.effects.add(ParticleAdder.create(Particles.EXPLOSION, pos.x, pos.y, id));
	}
	
	protected boolean hitWall(boolean vertical) {
		kill();
		return true;
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
	
	protected abstract void aiUpdate(float deltaTime);
	
	@Override
	public Renderable getRenderable() {
		return RocketRenderable.create(pos.x, pos.y, vel.angle(), id, type.effect);
	}
	
}
