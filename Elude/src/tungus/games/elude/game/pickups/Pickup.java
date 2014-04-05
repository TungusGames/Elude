package tungus.games.elude.game.pickups;

import tungus.games.elude.game.Vessel;
import tungus.games.elude.game.World;
import tungus.games.elude.util.CustomInterpolations.FadeinFlash;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class Pickup extends Sprite {

	public static final float DRAW_SIZE = 0.9f;
	protected static final float DEFAULT_LIFETIME = 5f;
	
	private static final float APPEAR_TIME = 0.3f;
	private static final float TAKE_TIME = 0.2f;
	private static final float FLASH_TIME = 1.8f;
	private static final Interpolation PICKED_UP = Interpolation.fade;
	private static final Interpolation NOT_PICKED = new FadeinFlash(APPEAR_TIME/DEFAULT_LIFETIME, 1-FLASH_TIME/DEFAULT_LIFETIME);
	
	protected World world;
	private Rectangle collisionBounds;
	private float lifeTimeLeft;
	private float fullLifeTime;
	private boolean pickedUp = false;
	
	public Pickup(World world, Vector2 pos, TextureRegion texture, float lifeTime) {
		super(texture);
		this.world = world;
		collisionBounds = new Rectangle(pos.x-DRAW_SIZE/2, pos.y-DRAW_SIZE/2, DRAW_SIZE, DRAW_SIZE);
		setBounds(pos.x-DRAW_SIZE/2, pos.y-DRAW_SIZE/2, DRAW_SIZE, DRAW_SIZE);
		fullLifeTime = lifeTimeLeft = lifeTime;
	}
	
	public Pickup(World world, Vector2 pos, TextureRegion texture) {
		this(world, pos, texture, DEFAULT_LIFETIME);
	}
	
	public boolean update(float deltaTime) {
		lifeTimeLeft -= deltaTime;
		if (lifeTimeLeft <= 0f) {
			kill();
			return true;
		}
		if (!pickedUp) {
			if (lifeTimeLeft > fullLifeTime-APPEAR_TIME || lifeTimeLeft < FLASH_TIME) {
				Color c = getColor();
				c.a = NOT_PICKED.apply(1-lifeTimeLeft/fullLifeTime);
				setColor(c);
			}
			int size = world.vessels.size();
			for (int i = 0; i < size; i++) {
				Vessel vessel = world.vessels.get(i);
				if (collisionBounds.overlaps(vessel.bounds)) {
					produceEffect(vessel);
					pickedUp = true;
					lifeTimeLeft = TAKE_TIME;
				}
			}
		} else {
			Color c = getColor();
			c.a = PICKED_UP.apply(1, 0, 1-lifeTimeLeft/TAKE_TIME);
			setColor(c);
		}
		return false;
	}
	
	protected abstract void produceEffect(Vessel vessel);
	
	public void kill() {
		world.pickups.remove(this);
	}
}
