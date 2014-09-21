package tungus.games.elude.game.server.pickups;

import tungus.games.elude.game.multiplayer.transfer.RenderInfo.Effect.EffectType;
import tungus.games.elude.game.multiplayer.transfer.RenderInfoPool;
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
		world.freezeTime = FREEZE_TIME;
		world.effects.add(RenderInfoPool.newEffect(0, 0, EffectType.FREEZE.ordinal()));
	}

}
