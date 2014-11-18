package tungus.games.elude.game.server.rockets;

import tungus.games.elude.Assets;
import tungus.games.elude.game.client.worldrender.SoundEffect;
import tungus.games.elude.game.server.Vessel;
import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.enemies.Enemy;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class StraightRocket extends Rocket {
	
	public static final float SPEED = 17.5f;
	private boolean bounced = false;
	
	public StraightRocket(Enemy origin, Vector2 pos, Vector2 dir, World world, Vessel target) {
		super(origin, RocketType.STRAIGHT, pos, dir, world, target);
		vel.nor().scl(SPEED);
		world.effects.add(SoundEffect.create(Assets.Sounds.LASERSHOT));
	}

	@Override
	protected void aiUpdate(float deltaTime) {} // No need to do *anything*!
	
	@Override
	protected boolean hitWall(boolean vert) {
		if (!bounced) {
			if (vert)
				vel.x = -vel.x;
			else
				vel.y = -vel.y;
			pos.x = MathUtils.clamp(pos.x, boundsForVessel.x, World.WIDTH-boundsForVessel.x);
			pos.y = MathUtils.clamp(pos.y, boundsForVessel.y, World.HEIGHT-boundsForVessel.y);
			bounced = true;
			return false;
		}
		kill();
		return true;
	}

}
