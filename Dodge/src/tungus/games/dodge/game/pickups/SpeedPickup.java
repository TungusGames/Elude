package tungus.games.dodge.game.pickups;

import tungus.games.dodge.Assets;
import tungus.games.dodge.game.Vessel;
import tungus.games.dodge.game.World;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class SpeedPickup extends Pickup {

	private static final float SPEED_BONUS = 1.5f;
	private static final float SPEED_BONUS_TIME = 3f;
	private static final TextureRegion texture = Assets.whiteRectangle;
	
	public SpeedPickup(World world, Vector2 pos) {
		super(world, pos, texture, DEFAULT_LIFETIME);
	}
	
	@Override
	protected void produceEffect(Vessel vessel) {
		vessel.speedBonus = SPEED_BONUS;
		vessel.speedBonusTime = SPEED_BONUS_TIME;
	}

}
