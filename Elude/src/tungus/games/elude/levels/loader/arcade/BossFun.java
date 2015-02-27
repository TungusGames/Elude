package tungus.games.elude.levels.loader.arcade;

import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.enemies.Enemy;
import tungus.games.elude.game.server.enemies.Enemy.EnemyType;
import tungus.games.elude.game.server.enemies.boss.ClosingBoss;
import tungus.games.elude.game.server.enemies.boss.TeleportingBoss;

public class BossFun extends ArcadeLoaderBase {

	public BossFun(World w, int levelNum) {
		super(w, 0.07f, 0.015f, 0.015f, 0.055f, levelNum);
		world.addEnemy(Enemy.fromType(world, EnemyType.CLOSING_BOSS));
		world.addEnemy(Enemy.fromType(world, EnemyType.CLOSING_BOSS));
		world.addEnemy(Enemy.fromType(world, EnemyType.BOSS_TELEPORT));
		world.addEnemy(Enemy.fromType(world, EnemyType.BOSS_TELEPORT));
	}
	
	@Override
	public void onEnemyDead(Enemy e) {
		super.onEnemyDead(e);
		if (e instanceof ClosingBoss) {
			world.addEnemy(Enemy.fromType(world, EnemyType.CLOSING_BOSS));
		} else if (e instanceof TeleportingBoss) {
			world.addEnemy(Enemy.fromType(world, EnemyType.BOSS_TELEPORT));
		}
	}
}
