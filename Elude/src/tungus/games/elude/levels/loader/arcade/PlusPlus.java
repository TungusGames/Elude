package tungus.games.elude.levels.loader.arcade;

import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.enemies.Enemy;
import tungus.games.elude.game.server.enemies.Enemy.EnemyType;

import com.badlogic.gdx.math.MathUtils;

/**
 * Spawns waves of enemies, 1 more enemy in each wave. 
 * Types randomly selected from given array.
 */
public class PlusPlus extends ArcadeLoaderBase {
	
	protected final EnemyType[] types;
	private int wave = 0;
	private float maxWait;
	private float timeSinceWave = 0;
	public PlusPlus(World w, int levelNum, float timeout, EnemyType... t) {
		this(w, levelNum, timeout, 0, 0, 0, 0, t);
	}
	
	public PlusPlus(World w, int levelNum, float timeout, float a, float b, float c, float d, EnemyType... t) {
		super(w, a, b, c, d, levelNum);
		this.maxWait = timeout;
		types = t;
		addEnemies(++wave);
	}
	
	@Override
	public boolean update(float delta) {
		timeSinceWave += delta;
		return super.update(delta);
	}
	
	@Override
	public void onEnemyDead(Enemy e) {
		super.onEnemyDead(e);
		if (world.enemyCount == 0 || timeSinceWave > maxWait) {
			addEnemies(++wave);
			timeSinceWave = 0;
		}
	}
	
	protected void addEnemy(EnemyType type) {
		world.addEnemy(Enemy.fromType(world, type));
	}
	
	protected void addEnemies(int count) {
		for (int i = 0; i < count; i++) {
			addEnemy(types[MathUtils.random(types.length - 1)]);
		}
	}
}
