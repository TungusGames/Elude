package tungus.games.dodge.game.rockets;

import tungus.games.dodge.game.World;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Rocket extends Sprite {
	
	public static final float ROCKET_SIZE = 0.2f; // For both collision and drawing
	public static final float DEFAULT_DMG = 5f;
	
	public static interface RocketAI {
		public void modVelocity(Vector2 pos, Vector2 vel, float deltaTime);
	}
	
	private RocketAI ai;
	
	private World world;
	
	public Vector2 pos;
	public Vector2 vel;
	
	private boolean outOfOrigin = false;
	
	public final float dmg;
		
	// TODO: particle

	
	public Rocket(RocketAI ai, Vector2 pos, Vector2 dir, World world, TextureRegion texture) {
		this(ai, pos, dir, world, texture, DEFAULT_DMG);
	}
	
	public Rocket(RocketAI ai, Vector2 pos, Vector2 dir, World world, TextureRegion texture, float dmg) {
		super(texture);
		this.ai = ai;
		this.pos = pos;
		this.world = world;
		setBounds(pos.x - ROCKET_SIZE / 2, pos.y - ROCKET_SIZE / 2, ROCKET_SIZE, ROCKET_SIZE);
		this.dmg = dmg;
		vel = dir;
	}
	
	public boolean update(float deltaTime) {
		ai.modVelocity(pos, vel, deltaTime);
		pos.add(vel.x * deltaTime, vel.y * deltaTime);
		setPosition(pos.x - ROCKET_SIZE / 2, pos.y - ROCKET_SIZE / 2);
		int size = world.enemies.size();
		for (int i = 0; i < size; i++) {
			if (world.enemies.get(i).collisionBounds.overlaps(getBoundingRectangle())) {
				if (outOfOrigin) {
					world.enemies.get(i).hp -= dmg;
					return true;
				}
					
			} else {
				if (!outOfOrigin) {
					outOfOrigin = true;
				}					
			}
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
}
