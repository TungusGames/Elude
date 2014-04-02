package tungus.games.elude.game.rockets;

import tungus.games.elude.Assets;
import tungus.games.elude.game.Vessel;
import tungus.games.elude.game.World;
import tungus.games.elude.game.enemies.Enemy;
import tungus.games.elude.levels.loader.FiniteLevelLoader;
import tungus.games.elude.util.CamShaker;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;

public abstract class Rocket extends Sprite {
	
	public static enum RocketType { SLOW_TURNING, FAST_TURNING, LOWGRAV };
	
	public static final float ROCKET_SIZE = 0.2f; // For both collision and drawing
	public static final float DEFAULT_DMG = 2f;
	public static final float DEFAULT_LIFE = 10f;
	
	public static final Rocket rocketFromType(RocketType t, Enemy origin, Vector2 pos, Vector2 dir, Vessel target, World w) {
		Rocket r = null;
		switch(t) {
		case SLOW_TURNING:
			r = new TurningRocket(origin, pos, dir, w, null, target);
			break;
		case FAST_TURNING:
			r = new TurningRocket(origin, pos, dir, w, null, target, true);
			break;
		case LOWGRAV:
			r = new LowGravityRocket(origin, pos, dir, w, null, target);
			break;
		default:
			throw new GdxRuntimeException("Unknown rocket type: " + t);
		}
		return r;
	}
	
	private World world;
	private Enemy origin;
	
	public Vessel target;
	
	public Vector2 pos;
	public Vector2 vel;
	
	private boolean outOfOrigin = false;
	
	public final float dmg;
	
	private PooledEffect particle;
	private float life;
	
	public Rocket(Enemy origin, Vector2 pos, Vector2 dir, World world, TextureRegion texture, Vessel target, PooledEffect particle) {
		this(origin, pos, dir, world, texture, target, DEFAULT_DMG, DEFAULT_LIFE, particle);
	}
	
	public Rocket(Enemy origin, Vector2 pos, Vector2 dir, World world, TextureRegion texture, Vessel target, float dmg, float life, PooledEffect particle) {
		super();
		this.origin = origin;
		this.life = life;
		this.pos = pos;
		this.world = world;
		setBounds(pos.x - ROCKET_SIZE / 2, pos.y - ROCKET_SIZE / 2, ROCKET_SIZE, ROCKET_SIZE);
		this.dmg = dmg;
		this.target = target;
		vel = dir;
		this.particle = particle;
		world.particles.add(this.particle);
		//particle.reset();
		particle.start();
	}
	
	public final boolean update(float deltaTime) {
		life -= deltaTime;
		if (life <= 0) {
			kill();
			return true;
		}
		aiUpdate(deltaTime);
		updateParticle(deltaTime);
		pos.add(vel.x * deltaTime, vel.y * deltaTime);
		setPosition(pos.x - ROCKET_SIZE / 2, pos.y - ROCKET_SIZE / 2);
		
		if (!world.outerBounds.contains(getBoundingRectangle())) {
			kill();
			return true;
		}
		
		int size = world.enemies.size();
		boolean stillIn = false;
		for (int i = 0; i < size; i++) {
			if (world.enemies.get(i).collisionBounds.overlaps(getBoundingRectangle())) {
				if (outOfOrigin) {
					if ((world.enemies.get(i).hp -= dmg) <= 0)
						world.enemies.get(i).kill(this);
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
			if (world.vessels.get(i).bounds.overlaps(getBoundingRectangle())) {
				if (!world.vessels.get(i).shielded) {
					Gdx.input.vibrate(100);
					CamShaker.INSTANCE.shake(0.65f, 2.5f);
					world.vessels.get(i).hp -= dmg;
					if (world.waveLoader instanceof FiniteLevelLoader)
						((FiniteLevelLoader)(world.waveLoader)).hpLost += dmg;
				}
				kill();
				return true;
			}
		}
		return false;
	}
	
	protected void updateParticle(float deltaTime) {
		ParticleEmitter particleEmitter = particle.getEmitters().get(0);
		particleEmitter.getAngle().setLow(vel.angle()-180);
		particle.setPosition(pos.x, pos.y);

	}
	
	public void kill() {
		particle.allowCompletion();
		world.rockets.remove(this);
		PooledEffect explosion = Assets.explosion.obtain();
		explosion.reset();
		explosion.setPosition(pos.x, pos.y);
		explosion.start();
		world.particles.add(explosion);
	}
	
	protected abstract void aiUpdate(float deltaTime);
	
}
