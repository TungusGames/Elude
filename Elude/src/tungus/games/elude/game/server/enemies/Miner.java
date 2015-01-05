package tungus.games.elude.game.server.enemies;

import tungus.games.elude.game.server.Updatable;
import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.rockets.Mine;

import com.badlogic.gdx.math.Vector2;

public class Miner extends MovingEnemy {
	
	private static final float SPEED = 2f;
	private static final float RELOAD = 5f;
	private static final float TURN = 60f;
	
	public Miner(Vector2 pos, World w) {
		super(pos, EnemyType.MINER, w, null, SPEED, RELOAD, TURN);
	}
	
	@Override
	protected boolean canShoot() {
		return super.canShoot() && !mineIsNearby();
	}
	
	@Override
	protected void shootRocket() {
		timeSinceShot = 0;
		Mine mine = new Mine(world, pos.cpy());
		world.addNextFrame.add(mine);
	}

	private boolean mineIsNearby() {
		for (Updatable u : world.updatables) {
			if (u instanceof Mine && pos.dst2(((Mine)u).bounds.x, ((Mine)u).bounds.y) < 4*Mine.RADIUS*Mine.RADIUS) {
				return true;
			}
		}
		return false;
	}

}
