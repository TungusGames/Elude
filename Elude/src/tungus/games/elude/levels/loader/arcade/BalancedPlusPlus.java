package tungus.games.elude.levels.loader.arcade;

import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.enemies.Enemy.EnemyType;

/**
 * Each wave's composition is as close to the given array's as possible.
 * The first types in the array have priority.
 */
public class BalancedPlusPlus extends PlusPlus {
	
	public BalancedPlusPlus(World w, int levelNum, float maxWait, EnemyType... t) {
		super(w, levelNum, maxWait, t);
	}

	@Override
	protected void addEnemies(int count) {
		int typeIndex = 0;
		for (int i = 0; i < count; i++) {
			addEnemy(types[typeIndex++]);
			if (typeIndex == types.length) {
				typeIndex = 0;
			}
		}
	}
}
