package tungus.games.elude.game.pickups;

import tungus.games.elude.Assets;
import tungus.games.elude.game.Vessel;
import tungus.games.elude.game.World;
import tungus.games.elude.game.pickups.Pickup;
import tungus.games.elude.game.rockets.Rocket;

import com.badlogic.gdx.math.Vector2;

public class RocketKillerPickup extends Pickup {

	public RocketKillerPickup(World world, Vector2 pos, 
			float lifeTime) {
		super(world, pos, Assets.smallCircle, lifeTime);
	}

	public RocketKillerPickup(World world, Vector2 pos) {
		super(world, pos, Assets.smallCircle);
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