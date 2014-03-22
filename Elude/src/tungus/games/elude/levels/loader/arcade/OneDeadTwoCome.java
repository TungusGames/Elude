package tungus.games.elude.levels.loader.arcade;

import com.badlogic.gdx.math.MathUtils;

import tungus.games.elude.game.World;
import tungus.games.elude.game.enemies.Enemy;
import tungus.games.elude.game.enemies.Enemy.EnemyType;
import tungus.games.elude.levels.loader.EnemyLoader;

public class OneDeadTwoCome extends ArcadeLoaderBase {

	public OneDeadTwoCome(World w, int levelNum) {
		super(w, 0.05f, 0.05f, 0.05f, levelNum);
		world.enemies.add(Enemy.newEnemy(world, EnemyType.STANDING));
	}
	
	@Override
	public void onEnemyDead(Enemy e) {
		super.onEnemyDead(e);
		world.enemies.add(Enemy.newEnemy(world, EnemyType.MOVING));
		world.enemies.add(Enemy.newEnemy(world, MathUtils.randomBoolean() ? EnemyType.KAMIKAZE : EnemyType.STANDING));
	}

}
