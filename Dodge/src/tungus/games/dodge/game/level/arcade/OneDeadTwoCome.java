package tungus.games.dodge.game.level.arcade;

import com.badlogic.gdx.math.MathUtils;

import tungus.games.dodge.game.World;
import tungus.games.dodge.game.enemies.Enemy;
import tungus.games.dodge.game.enemies.Enemy.EnemyType;
import tungus.games.dodge.game.level.EnemyLoader;

public class OneDeadTwoCome extends EnemyLoader {

	public OneDeadTwoCome(World w) {
		super(w, 0.05f, 0.05f, 0.05f);
		world.enemies.add(Enemy.newEnemy(world, EnemyType.STANDING));
	}
	
	@Override
	public void onEnemyDead(Enemy e) {
		super.onEnemyDead(e);
		world.enemies.add(Enemy.newEnemy(world, EnemyType.MOVING));
		world.enemies.add(Enemy.newEnemy(world, MathUtils.randomBoolean() ? EnemyType.KAMIKAZE : EnemyType.STANDING));
	}

}
