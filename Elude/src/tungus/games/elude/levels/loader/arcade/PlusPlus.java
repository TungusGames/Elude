package tungus.games.elude.levels.loader.arcade;

import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.enemies.Enemy;
import tungus.games.elude.game.server.enemies.Enemy.EnemyType;

import com.badlogic.gdx.math.MathUtils;

public class PlusPlus extends ArcadeLoaderBase {
	private final EnemyType[] types;
	private int wave = 1;
	public PlusPlus(World w, int levelNum, EnemyType... t) {
		this(w, levelNum, 0, 0, 0, 0, t);
	}
	
	public PlusPlus(World w, int levelNum, float a, float b, float c, float d, EnemyType... t) {
		super(w, a, b, c, d, levelNum);
		types = t;
		addEnemy();
		wave++;
	}
	
	@Override
	public void onEnemyDead(Enemy e) {
		super.onEnemyDead(e);
		if (world.enemyCount == 0) {
			for (int i = 0; i < wave; i++) {
				addEnemy();
			}
			wave++;
		}
	}
	
	private void addEnemy() {
		world.addEnemy(Enemy.fromType(world, types[MathUtils.random(types.length-1)]));
	}
}
