package tungus.games.elude.game.server.rockets;

import tungus.games.elude.Assets;
import tungus.games.elude.game.multiplayer.transfer.RenderInfo.Effect.EffectType;
import tungus.games.elude.game.multiplayer.transfer.RenderInfoPool;
import tungus.games.elude.game.server.Vessel;
import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.enemies.Enemy;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class Rocket {
	
	public static enum RocketType { 
		SLOW_TURNING(Assets.flameRocket), 
		FAST_TURNING(Assets.fastFlameRocket), 
		LOWGRAV(Assets.matrixRocket),
		HIGHGRAV(null),
		STRAIGHT(Assets.straightRocket);
		public ParticleEffectPool effect;
		RocketType(ParticleEffectPool e) {
			effect = e;
		}
	};
	
	public static final float ROCKET_SIZE = 0.2f; // For both collision and drawing
	public static final float DEFAULT_DMG = 3.5f;
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
		case LOWGRAV:
			r = new LowGravityRocket(origin, pos, dir, w, target);
			break;
		case STRAIGHT:
			r = new StraightRocket(origin, pos, dir, w, target);
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
	public Rectangle bounds;
	public final RocketType type;
	public final int id;
	
	private boolean outOfOrigin = false;
	
	public final float dmg;
	
	private float life;
	
	public Rocket(Enemy origin, RocketType t, Vector2 pos, Vector2 dir, World world, Vessel target) {
		this(origin, t, pos, dir, world, target, DEFAULT_DMG, DEFAULT_LIFE);
	}
	
	public Rocket(Enemy origin, RocketType t, Vector2 pos, Vector2 dir, World world, Vessel target, float dmg, float life) {
		super();
		this.origin = origin;
		this.type = t;
		this.life = life;
		this.pos = pos;
		this.world = world;
		this.bounds = new Rectangle(pos.x - ROCKET_SIZE / 2, pos.y - ROCKET_SIZE / 2, ROCKET_SIZE, ROCKET_SIZE);
		this.dmg = dmg;
		this.target = target;
		this.id = nextID++;
		vel = dir;
	}
	
	public final boolean update(float deltaTime) {
		life -= deltaTime;
		// If there are no enemies alive, speed up the pace by dying twice as fast
		if (world.enemies.size() == 0) { 
			life -= deltaTime;
		}
		if (life <= 0) {
			kill();
			return true;
		}
		aiUpdate(deltaTime);
		pos.add(vel.x * deltaTime, vel.y * deltaTime);
		bounds.x = pos.x - ROCKET_SIZE / 2;
		bounds.y = pos.y - ROCKET_SIZE / 2;
		if (!World.outerBounds.contains(pos)) {
			if (hitWall(pos.x < 0 || pos.x > World.WIDTH)) {
				return true;
			}
		}
		
		int size = world.enemies.size();
		boolean stillIn = false;
		for (int i = 0; i < size; i++) {
			if (world.enemies.get(i).collisionBounds.overlaps(bounds)) {
				if (outOfOrigin) {
					if ((world.enemies.get(i).hp -= dmg) <= 0)
						world.enemies.get(i).kill(this);
					//world.explosion.play();
					kill();
					return true;
				} else {
					if (world.enemies.get(i).equals(origin)) {
						stillIn = true;
					}
				}
			}
		}
		if (!stillIn) {
			outOfOrigin = true;
		}
		
		size = world.vessels.size();
		for (int i = 0; i < size; i++) {
			if (world.vessels.get(i).bounds.overlaps(bounds)) {
				if (!world.vessels.get(i).shielded) {
					world.effects.add(RenderInfoPool.newEffect(0f, 0f, EffectType.CAMSHAKE.ordinal())); //TODO pool
					world.vessels.get(i).hp -= dmg;
				}
				kill();
				return true;
			}
		}
		return false;
	}
	
	public void kill() {
		world.rockets.remove(this);
		world.effects.add(RenderInfoPool.newEffect(pos.x, pos.y, EffectType.EXPLOSION.ordinal())); // TODO pool
	}
	
	protected boolean hitWall(boolean vertical) {
		kill();
		return true;
	}
	
	protected abstract void aiUpdate(float deltaTime);
	
}
