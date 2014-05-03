package tungus.games.elude.levels.loader.arcade;

import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.enemies.Enemy;
import tungus.games.elude.game.server.enemies.Enemy.EnemyType;

import com.badlogic.gdx.math.MathUtils;

public class OneDeadTwoCome extends ArcadeLoaderBase {

	public OneDeadTwoCome(World w, int levelNum) {
		super(w, 0.05f, 0.05f, 0.05f, 0.05f, levelNum);
		world.enemies.add(Enemy.fromType(world, EnemyType.STANDING));
	}
	
	@Override
	public void onEnemyDead(Enemy e) {
		super.onEnemyDead(e);
		world.enemies.add(Enemy.fromType(world, EnemyType.MOVING));
		world.enemies.add(Enemy.fromType(world, MathUtils.randomBoolean() ? EnemyType.KAMIKAZE : EnemyType.STANDING));
	}

}
