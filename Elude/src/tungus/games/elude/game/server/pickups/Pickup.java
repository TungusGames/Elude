package tungus.games.elude.game.server.pickups;

import tungus.games.elude.Assets;
import tungus.games.elude.game.server.Vessel;
import tungus.games.elude.game.server.World;
import tungus.games.elude.util.CustomInterpolations.FadeinFlash;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;

public abstract class Pickup {
	
	public enum PickupType{
		HEALTH(Assets.hpBonus), 
		SPEED(Assets.speedBonus), 
		SHIELD(Assets.shieldBonus), 
		FREEZER(Assets.freezerBonus);
		public TextureRegion tex;
		PickupType(TextureRegion t) {
			tex = t;
		}
	}
	
	public static final float DRAW_SIZE = 0.9f;
	public static final float HALF_SIZE = DRAW_SIZE/2;
	protected static final float DEFAULT_LIFETIME = 3f;
	
	private static final float APPEAR_PORTION = 0.1f;
	private static final float TAKE_TIME = 0.2f;
	private static final float FLASH_PORTION = 0.35f;
	private static final Interpolation PICKED_UP = Interpolation.fade;
	private static final Interpolation NOT_PICKED = new FadeinFlash(APPEAR_PORTION, 1-FLASH_PORTION);
	
	public static Pickup fromType(World w, PickupType t) {
		Pickup p = null;
		switch (t) {
		case HEALTH:
			p = new HealthPickup(w, w.randomPosInInnerRect(new Vector2()));
			break;
		case SPEED:
			p = new SpeedPickup(w, w.randomPosInInnerRect(new Vector2()));
			break;
		case SHIELD:
			p = new ShieldPickup(w, w.randomPosInInnerRect(new Vector2()));
			break;
		case FREEZER:
			p = new FreezerPickup(w, w.randomPosInInnerRect(new Vector2()));
			break;
		default:
			throw new IllegalArgumentException("Unknown PickupType " + t);
		}
		return p;
	}
	
	protected World world;
	public Circle collisionBounds;
	private float lifeTimeLeft;
	private float fullLifeTime;
	private boolean pickedUp = false;
	
	public float alpha = 0;
	public PickupType type;
	
	public Pickup(World world, Vector2 pos, PickupType type, float lifeTime) {
		this.world = world;
		collisionBounds = new Circle(pos, DRAW_SIZE/2);
		fullLifeTime = lifeTimeLeft = lifeTime;
		this.type = type;
	}
	
	public Pickup(World world, Vector2 pos, PickupType type) {
		this(world, pos, type, DEFAULT_LIFETIME);
	}
	
	public boolean update(float deltaTime) {
		lifeTimeLeft -= deltaTime;
		if (lifeTimeLeft <= 0f) {
			return true;
		}
		if (!pickedUp) {
			float currentPortion = 1 - lifeTimeLeft/fullLifeTime;
			if (currentPortion < APPEAR_PORTION || currentPortion > 1-FLASH_PORTION) {
				alpha = NOT_PICKED.apply(currentPortion);
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
			alpha = PICKED_UP.apply(1, 0, 1-lifeTimeLeft/TAKE_TIME);
		}
		return false;
	}
	
	protected abstract void produceEffect(Vessel vessel);
}
