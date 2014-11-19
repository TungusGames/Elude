package tungus.games.elude.game.server.enemies;

import tungus.games.elude.game.server.Updatable;
import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.rockets.Mine;
import tungus.games.elude.game.server.rockets.Rocket.RocketType;

import com.badlogic.gdx.math.Vector2;

public class Miner extends MovingEnemy {
	
	private static final float SPEED = 2f;
	private static final float RELOAD = 3f;
	private static final float TURN = 60f;
	
	public Miner(Vector2 pos, World w) {
		super(pos, EnemyType.MINER, w, RocketType.MINE, SPEED, RELOAD, TURN);
	}
	
	@Override
	protected boolean canShoot() {
		return super.canShoot() && !mineIsNearby();
	}

	private boolean mineIsNearby() {
		for (Updatable u : world.updatables) {
			if (u instanceof Mine && pos.dst2(((Mine)u).pos) < Mine.SIZE*Mine.SIZE) {
				return true;
			}
		}
		return false;
	}

}
