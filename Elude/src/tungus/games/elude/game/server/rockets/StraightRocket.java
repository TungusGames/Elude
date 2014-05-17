package tungus.games.elude.game.server.rockets;

import tungus.games.elude.game.server.Vessel;
import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.enemies.Enemy;

import com.badlogic.gdx.math.Vector2;

public class StraightRocket extends Rocket {
	
	public static final float SPEED = 25f;
	private boolean bounced = false;
	
	public StraightRocket(Enemy origin, Vector2 pos, Vector2 dir, World world, Vessel target) {
		super(origin, RocketType.STRAIGHT, pos, dir, world, target);
		vel.nor().scl(SPEED);
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
			bounced = true;
			return false;
		}
		kill();
		return true;
	}

}
