package tungus.games.elude.game.server.pickups;

import tungus.games.elude.game.server.Vessel;
import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.rockets.Rocket;

import com.badlogic.gdx.math.Vector2;

public class RocketWiperPickup extends Pickup {

	public RocketWiperPickup(World world, Vector2 pos, 
			float lifeTime) {
		super(world, pos, PickupType.ROCKETWIPER, lifeTime);
	}

	public RocketWiperPickup(World world, Vector2 pos) {
		super(world, pos, PickupType.ROCKETWIPER);
	}

	@Override
	protected void produceEffect(Vessel vessel) {
		int size = world.rockets.size();
		for (int i = 0; i < size; i++) {
			Rocket r = world.rockets.get(i);
			if (r.target == vessel) {
				r.kill();
				i--;
				size--;
			}
		}
	}
}