package tungus.games.elude.game.server.rockets;

import tungus.games.elude.game.client.worldrender.renderable.MineRenderable;
import tungus.games.elude.game.client.worldrender.renderable.Renderable;
import tungus.games.elude.game.server.Updatable;
import tungus.games.elude.game.server.Vessel;
import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.enemies.Hittable;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

public class Mine extends Updatable {

	private static final float DAMAGE = 15f;
	public static final float RADIUS = 1.5f;
	public static final float LIFETIME = 10f;
	
	private final World world;
	
	public final Circle bounds;	
	private float lifeLeft = LIFETIME;
	
	public Mine(World world, Vector2 pos) {
		bounds = new Circle(pos, RADIUS);
		this.world = world;
	}

	@Override
	public boolean update(float deltaTime) {
		for (Vessel vessel : world.vessels) {
			if (vessel.isHitBy(bounds, DAMAGE)) {
				explodeBy(vessel);
				return true;
			}
		}
		lifeLeft -= deltaTime;
		return lifeLeft <= 0;
	}
	
	private void explodeBy(Vessel trigger) {
		for (Vessel other : world.vessels) {
			if (other != trigger)
				other.isHitBy(bounds, DAMAGE);
		}
		
		for (Updatable entity : world.updatables) {
			if (entity instanceof Hittable) {
				((Hittable)entity).isHitBy(bounds, DAMAGE);
			}
		}
	}

	@Override
	public Renderable getRenderable() {
		return MineRenderable.create(bounds.x, bounds.y, id);
	}


}
