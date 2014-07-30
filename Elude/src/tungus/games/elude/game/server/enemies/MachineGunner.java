package tungus.games.elude.game.server.enemies;

import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.rockets.Rocket.RocketType;

import com.badlogic.gdx.math.Vector2;

public class MachineGunner extends StandingBase {
	private static final int SHOTS_AT_ONCE = 5;
	private static final float SHORT_RELOAD = 0.2f;
	private static final float LONG_RELOAD = 3f;
	
	private int shots = 0;
	
	public MachineGunner(Vector2 pos, World w) {
		super(pos, EnemyType.MACHINEGUNNER, RocketType.SLOW_TURNING, w);
	}

	@Override
	protected boolean standingUpdate(float deltaTime) {
		if (timeSinceShot > (shots == 0 ? LONG_RELOAD : SHORT_RELOAD)) {
			shootRocket(world.vessels.get(0).pos.cpy().sub(pos));
			if (++shots == SHOTS_AT_ONCE) {
				shots = 0;
			}
		}
		return false;
	}
}
