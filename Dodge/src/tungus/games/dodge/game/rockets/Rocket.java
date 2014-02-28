package tungus.games.dodge.game.rockets;

import tungus.games.dodge.Assets;
import tungus.games.dodge.game.World;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public abstract class Rocket extends Sprite {
	
	public static final float ROCKET_SIZE = 0.2f; // For both collision and drawing
	public static final float DEFAULT_DMG = 5f;
	
	private World world;
	
	public Vector2 pos;
	public Vector2 vel;
	
	private boolean outOfOrigin = false;
	
	public final float dmg;
	
	private ParticleEffect particle;
		
	// TODO: particle

	
	public Rocket(Vector2 pos, Vector2 dir, World world, TextureRegion texture) {
		this(pos, dir, world, texture, DEFAULT_DMG, null);
	}
	
	public Rocket(Vector2 pos, Vector2 dir, World world, TextureRegion texture, float dmg, ParticleEffect particle) {
		super(texture);
		this.pos = pos;
		this.world = world;
		setBounds(pos.x - ROCKET_SIZE / 2, pos.y - ROCKET_SIZE / 2, ROCKET_SIZE, ROCKET_SIZE);
		this.dmg = dmg;
		vel = dir;
		this.particle = particle;
		this.world.particles.add(this.particle);
		
	}
	
	public final boolean update(float deltaTime) {
		aiUpdate(deltaTime);
		updateParticle();
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
				} else
					stillIn = true;
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
	
	protected void updateParticle() {
		ParticleEmitter particleEmitter = particle.getEmitters().get(0);
		particleEmitter.setPosition(pos.x, pos.y);
		particleEmitter.getRotation().setLow(vel.angle());
	}
	
	protected abstract void aiUpdate(float deltaTime);
	
}
