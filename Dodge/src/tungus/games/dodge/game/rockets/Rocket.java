package tungus.games.dodge.game.rockets;

import tungus.games.dodge.game.World;
import tungus.games.dodge.game.enemies.Enemy;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public abstract class Rocket extends Sprite {
	
	public static final float ROCKET_SIZE = 0.2f; // For both collision and drawing
	public static final float DEFAULT_DMG = 5f;
	
	private World world;
	private Enemy origin;
	
	public Vector2 pos;
	public Vector2 vel;
	
	private boolean outOfOrigin = false;
	
	public final float dmg;
	
	private PooledEffect particle;
	
	public Rocket(Enemy origin, Vector2 pos, Vector2 dir, World world, TextureRegion texture) {
		this(origin, pos, dir, world, texture, DEFAULT_DMG, null);
	}
	
	public Rocket(Enemy origin, Vector2 pos, Vector2 dir, World world, TextureRegion texture, float dmg, PooledEffect particle) {
		super(texture);
		this.origin = origin;
		this.pos = pos;
		this.world = world;
		setBounds(pos.x - ROCKET_SIZE / 2, pos.y - ROCKET_SIZE / 2, ROCKET_SIZE, ROCKET_SIZE);
		this.dmg = dmg;
		vel = dir;
		this.particle = particle;
		world.particles.add(this.particle);
		particle.reset();
		particle.start();
		
	}
	
	public final boolean update(float deltaTime) {
		aiUpdate(deltaTime);
		updateParticle(deltaTime);
		pos.add(vel.x * deltaTime, vel.y * deltaTime);
		setPosition(pos.x - ROCKET_SIZE / 2, pos.y - ROCKET_SIZE / 2);
		
		if (!world.bounds.contains(getBoundingRectangle())) {
			return true;
		}
		
		int size = world.enemies.size();
		boolean stillIn = false;
		for (int i = 0; i < size; i++) {
			if (world.enemies.get(i).collisionBounds.overlaps(getBoundingRectangle())) {
				if (outOfOrigin) {
					world.enemies.get(i).hp -= dmg;
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
				world.vessels.get(i).hp -= dmg;
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
	}
	
	protected abstract void aiUpdate(float deltaTime);
	
}
