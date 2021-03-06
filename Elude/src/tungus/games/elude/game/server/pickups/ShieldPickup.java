package tungus.games.elude.game.server.pickups;

import tungus.games.elude.game.server.Vessel;
import tungus.games.elude.game.server.World;

import com.badlogic.gdx.math.Vector2;

public class ShieldPickup extends Pickup {

	private static final float SHIELD_DURATION = 6f;
	
	public ShieldPickup(World world, Vector2 pos, float lifeTime) {
		super(world, pos, PickupType.SHIELD, lifeTime);
	}

	public ShieldPickup(World world, Vector2 pos) {
		super(world, pos, PickupType.SHIELD);
	}

	@Override
	protected void produceEffect(Vessel vessel) {
		vessel.addShield(SHIELD_DURATION);
	}

}
