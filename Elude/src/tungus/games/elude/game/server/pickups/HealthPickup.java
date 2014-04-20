package tungus.games.elude.game.server.pickups;

import tungus.games.elude.Assets;
import tungus.games.elude.game.server.Vessel;
import tungus.games.elude.game.server.World;

import com.badlogic.gdx.math.Vector2;

public class HealthPickup extends Pickup {
	
	private static final float HEALING = 8f;
	
	public HealthPickup(World world, Vector2 pos, float lifeTime) {
		super(world, pos, Assets.hpBonus, lifeTime);
	}
	
	public HealthPickup(World world, Vector2 pos) {
		super(world, pos, Assets.hpBonus, DEFAULT_LIFETIME); 
	}
	
	@Override
	protected void produceEffect(Vessel vessel) {
		float newhp = vessel.hp + HEALING;
		vessel.hp = newhp < Vessel.MAX_HP ? newhp : Vessel.MAX_HP;
	}

}
