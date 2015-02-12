package tungus.games.elude.game.server.rockets;

import tungus.games.elude.Assets;
import tungus.games.elude.Assets.Particles;
import tungus.games.elude.game.client.worldrender.renderable.Renderable;
import tungus.games.elude.game.client.worldrender.renderable.Renderable.Effect;
import tungus.games.elude.game.client.worldrender.renderable.effect.ParticleRemover;
import tungus.games.elude.game.client.worldrender.renderable.RocketRenderable;
import tungus.games.elude.game.server.Updatable;
import tungus.games.elude.game.server.Vessel;
import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.enemies.Enemy;
import tungus.games.elude.game.server.enemies.Hittable;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

public abstract class Rocket extends Updatable {
		
	public static enum RocketType { 
		SLOW_TURNING(Assets.Particles.FLAME_ROCKET,	3f, 5.5f, 150f), 
		FAST_TURNING(Assets.Particles.MATRIX_ROCKET,	3f,   9f, 172f), 
		SWARM	    (Assets.Particles.FLAME_ROCKET,   0.5f, 6.5f, 150f),
		STRAIGHT    (Assets.Particles.STRAIGHT_ROCKET,	3f,17.5f,   0f);
		public Particles effect;
		
		public float speed, turnSpeed, dmg;
		RocketType(Particles e, float dmg, float speed, float turnSpeed) {
			effect = e;
			this.speed = speed;
			this.dmg = dmg;
			this.turnSpeed = turnSpeed;
		}
	};
	
	public static final float ROCKET_SIZE = 0.1f; // Diameter
	public static final float DEFAULT_DMG = 3f;
	public static final float DEFAULT_LIFE = 6f;
	
	public static final Rocket fromType(RocketType t, Enemy origin, Vector2 pos, Vector2 dir, Vessel target, World w) {
		Rocket r = null;
		switch(t) {
		case SLOW_TURNING:
		case FAST_TURNING:
		    r = new TurningRocket(origin, t, pos, dir, w, target);
		    break;
		case SWARM:
		    r = new SwarmRocket(origin, t, pos, dir, w, target);
		    break;
		case STRAIGHT:
		    r = new StraightRocket(origin, pos, dir, w, target);
		    break;
		default:
		    throw new IllegalArgumentException("Unknown rocket type: " + t);
		}
		return r;
	}
		
	protected World world;
	private Enemy origin;
	
	public Vessel target;
	
	public Vector2 pos;
	public Vector2 vel;
	public Circle bounds;
	public final RocketType type;
	
	private boolean outOfOrigin = false;
		
	private float life;
	
	public Rocket(Enemy origin, RocketType t, Vector2 pos, Vector2 dir, World world, Vessel target) {
		this(origin, t, pos, dir, world, target, DEFAULT_LIFE);
	}
	
	public Rocket(Enemy origin, RocketType t, Vector2 pos, Vector2 dir, World world, Vessel target, float life) {
		super();
		this.origin = origin;
		this.type = t;
		this.life = life;
		this.pos = pos;
		this.world = world;
		this.bounds = new Circle(pos, ROCKET_SIZE/2);
		this.target = target;
		this.keepsWorldGoing = true;
		vel = dir.nor().scl(type.speed);
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
		bounds.x = pos.x;
		bounds.y = pos.y;
		if (!World.outerBounds.contains(pos)) {
			if (hitWall(pos.x < 0 || pos.x > World.WIDTH)) {
				return true;
			}
		}
		
		boolean stillIn = false;
		for (Updatable u : world.updatables) {
			if (!outOfOrigin && u == origin) {
				stillIn = (origin.collisionBounds.overlaps(bounds));
				continue;
			}
			if (u instanceof Hittable && ((Hittable)u).isHitBy(bounds, type.dmg)) {
				kill();
				return true;
			}
		}
		if (!stillIn) {
			outOfOrigin = true;
		}
		
		for (Vessel v : world.vessels) {
			if (v.isHitBy(bounds, type.dmg)) {
				kill();
				return true;
			}
		}
		return false;
	}
	
	public void kill() {
		world.effects.add(ParticleRemover.create(id));
		Effect.addExplosion(world.effects, pos);
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
