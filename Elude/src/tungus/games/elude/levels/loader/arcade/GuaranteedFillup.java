package tungus.games.elude.levels.loader.arcade;

import tungus.games.elude.game.server.Updatable;
import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.enemies.Enemy;
import tungus.games.elude.game.server.enemies.Enemy.EnemyType;

/**
 * Always puts at least 1 of the first enemy type it gets in the level
 */
public class GuaranteedFillup extends FillUp {

	public GuaranteedFillup(World w, int levelNum, int fillTo, float timeToMax,
			EnemyType... types) {
		super(w, levelNum, fillTo, timeToMax, types);
	}
	
	@Override
	protected void addEnemy() {
		boolean hasFirstType = false;
		for (Updatable u : world.updatables) {
			if (u instanceof Enemy && ((Enemy)u).type == types[0]) {
				hasFirstType = true;
				break;
			}
		}
		if (!hasFirstType) {
			world.addEnemy(Enemy.fromType(world, types[0]));
		} else {
			super.addEnemy();
		}
	}

}
