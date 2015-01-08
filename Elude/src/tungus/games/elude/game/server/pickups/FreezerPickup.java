package tungus.games.elude.game.server.pickups;

import tungus.games.elude.game.client.worldrender.renderable.effect.FreezeEffect;
import tungus.games.elude.game.server.Vessel;
import tungus.games.elude.game.server.World;

import com.badlogic.gdx.math.Vector2;

public class FreezerPickup extends Pickup {

	public static final float FREEZE_TIME = 4f;
	
	public FreezerPickup(World world, Vector2 pos) {
		super(world, pos, PickupType.FREEZER); 
	}
	
	@Override
	protected void produceEffect(Vessel vessel) {
		world.freezeTimer.freeze(FREEZE_TIME);
		world.effects.add(FreezeEffect.create(collisionBounds.x, collisionBounds.y, FREEZE_TIME));
	}

}
