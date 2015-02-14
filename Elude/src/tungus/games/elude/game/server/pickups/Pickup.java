package tungus.games.elude.game.server.pickups;

import tungus.games.elude.Assets;
import tungus.games.elude.Assets.Tex;
import tungus.games.elude.game.client.worldrender.phases.RenderPhase;
import tungus.games.elude.game.client.worldrender.renderable.Renderable;
import tungus.games.elude.game.client.worldrender.renderable.Sprite;
import tungus.games.elude.game.server.Updatable;
import tungus.games.elude.game.server.Vessel;
import tungus.games.elude.game.server.World;
import tungus.games.elude.util.CustomInterpolations.FadeinFlash;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public abstract class Pickup extends Updatable {

	public enum PickupType{
		HEALTH(Assets.Tex.HPBONUS), 
		SPEED(Assets.Tex.SPEEDBONUS), 
		SHIELD(Assets.Tex.SHIELDBONUS), 
		FREEZER(Assets.Tex.FREEZERBONUS);
		public Tex tex;
		PickupType(Tex t) {
			tex = t;
		}
	}

	public static final float START_DRAW_SIZE = 0.9f;
	public static final float HALF_SIZE = START_DRAW_SIZE/2;
	protected static final float DEFAULT_LIFETIME = 4f;

	private static final float APPEAR_PORTION = 0.1f;
	private static final float TAKE_TIME = 1f;
	private static final float FLASH_PORTION = 0.35f;
	private static final float ENLARGE_TO = 3f;
	private static final float OFFSET_SPEED = 1.6f;
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
	private Vector2 vel = new Vector2(0,0);
	private float currentDrawSize = START_DRAW_SIZE;

	public float alpha = 0;
	public PickupType type;

	public Pickup(World world, Vector2 pos, PickupType type, float lifeTime) {
		this.world = world;
		collisionBounds = new Circle(pos, START_DRAW_SIZE/2);
		fullLifeTime = lifeTimeLeft = lifeTime;
		this.type = type;
	}

	public Pickup(World world, Vector2 pos, PickupType type) {
		this(world, pos, type, DEFAULT_LIFETIME);
	}

	@Override
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
					getPickedUp(vessel);
					pickedUp = true;
					lifeTimeLeft = TAKE_TIME;
				}
			}
		} else {
			alpha = PICKED_UP.apply(1, 0, 1-lifeTimeLeft/TAKE_TIME);
			collisionBounds.x += deltaTime * vel.x;
			collisionBounds.y += deltaTime * vel.y;
			currentDrawSize += (ENLARGE_TO-START_DRAW_SIZE)*(deltaTime/TAKE_TIME);
		}
		return false;
	}

	@Override
	public Renderable getRenderable() {
		return Sprite.create(RenderPhase.PICKUP, type.tex, collisionBounds.x, collisionBounds.y, currentDrawSize, currentDrawSize, 0, alpha);
	}

	private void getPickedUp(Vessel vessel) {
		vel.set(1, 0);
		vel.rotate(MathUtils.random.nextFloat()*360f);
		vel.scl(OFFSET_SPEED);
		produceEffect(vessel);
	}
	
	protected abstract void produceEffect(Vessel vessel);
}
