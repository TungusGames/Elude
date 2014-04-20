package tungus.games.elude.game.server.pickups;

import tungus.games.elude.Assets;
import tungus.games.elude.game.server.Vessel;
import tungus.games.elude.game.server.World;

import com.badlogic.gdx.math.Vector2;

public class SpeedPickup extends Pickup {

	private static final float SPEED_BONUS = 1.5f;
	private static final float SPEED_BONUS_TIME = 3f;
	
	public SpeedPickup(World world, Vector2 pos) {
		super(world, pos, Assets.speedBonus, DEFAULT_LIFETIME);
	}
	
	@Override
	protected void produceEffect(Vessel vessel) {
		vessel.speedBonus = SPEED_BONUS;
		vessel.speedBonusTime = SPEED_BONUS_TIME;
	}

}
