package tungus.games.dodge.game.pickups;

import tungus.games.dodge.game.Vessel;
import tungus.games.dodge.game.World;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class Pickup extends Sprite {

	public static final float DRAW_SIZE = 0.9f;
	protected static final float DEFAULT_LIFETIME = 5f;
	
	private World world;
	private Rectangle collisionBounds;
	private float lifeTime;
	
	public Pickup(World world, Vector2 pos, TextureRegion texture, float lifeTime) {
		super(texture);
		this.world = world;
		collisionBounds = new Rectangle(pos.x, pos.y, DRAW_SIZE, DRAW_SIZE);
		setBounds(pos.x, pos.y, DRAW_SIZE, DRAW_SIZE);
		this.lifeTime = lifeTime;
	}
	
	public Pickup(World world, Vector2 pos, TextureRegion texture) {
		this(world, pos, texture, DEFAULT_LIFETIME);
	}
	
	public boolean update(float deltaTime) {
		lifeTime -= deltaTime;
		if (lifeTime <= 0f) {
			kill();
			return true;
		}
		int size = world.vessels.size();
		for (int i = 0; i < size; i++) {
			Vessel vessel = world.vessels.get(i);
			if (collisionBounds.overlaps(vessel.bounds)) {
				produceEffect(vessel);
				kill();
				return true;
			}
		}
		return false;
	}
	
	protected abstract void produceEffect(Vessel vessel);
	
	public void kill() {
		world.pickups.remove(this);
	}
}
