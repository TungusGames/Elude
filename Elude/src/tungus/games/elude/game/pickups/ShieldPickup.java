package tungus.games.elude.game.pickups;

import tungus.games.elude.Assets;
import tungus.games.elude.game.Vessel;
import tungus.games.elude.game.World;

import com.badlogic.gdx.math.Vector2;

public class ShieldPickup extends Pickup {

	public ShieldPickup(World world, Vector2 pos, float lifeTime) {
		super(world, pos, Assets.whiteRectangle, lifeTime);
	}

	public ShieldPickup(World world, Vector2 pos) {
		super(world, pos, Assets.whiteRectangle);
	}

	@Override
	protected void produceEffect(Vessel vessel) {
		vessel.addShield(3f);
	}

}
