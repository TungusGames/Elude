package tungus.games.dodge.game.level.arcade;

import tungus.games.dodge.game.World;
import tungus.games.dodge.game.enemies.Enemy;
import tungus.games.dodge.game.enemies.Enemy.EnemyType;
import tungus.games.dodge.game.level.EnemyLoader;

public class OneDeadTwoCome extends EnemyLoader {

	public OneDeadTwoCome(World w) {
		super(w, 0.1f, 0.1f, 0.1f);
		world.enemies.add(Enemy.newEnemy(world, EnemyType.STANDING));
	}
	
	@Override
	public void onEnemyDead(Enemy e) {
		super.onEnemyDead(e);
		world.enemies.add(Enemy.newEnemy(world, EnemyType.STANDING));
		world.enemies.add(Enemy.newEnemy(world, EnemyType.MOVING));
	}

}
