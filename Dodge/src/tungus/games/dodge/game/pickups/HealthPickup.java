package tungus.games.dodge.game.pickups;

import tungus.games.dodge.Assets;
import tungus.games.dodge.game.Vessel;
import tungus.games.dodge.game.World;

import com.badlogic.gdx.math.Vector2;

public class HealthPickup extends Pickup {

	public HealthPickup(World world, Vector2 pos, float lifeTime) {
		super(world, pos, Assets.smallCircle, lifeTime); //Texture needed
	}
	
	public HealthPickup(World world, Vector2 pos) {
		super(world, pos, Assets.smallCircle, DEFAULT_LIFETIME); 
	}
	
	@Override
	protected void produceEffect(Vessel vessel) {
		float newhp = vessel.hp + 20f;
		vessel.hp = newhp < Vessel.MAX_HP ? newhp : Vessel.MAX_HP;
	}

}
