package tungus.games.dodge.game.enemies;

import tungus.games.dodge.Assets;
import tungus.games.dodge.game.Vessel;
import tungus.games.dodge.game.World;
import tungus.games.dodge.game.pickups.Pickup;
import tungus.games.dodge.game.rockets.Rocket;

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