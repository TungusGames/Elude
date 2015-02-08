package tungus.games.elude.game.server.enemies.boss;

import com.badlogic.gdx.math.MathUtils;

import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.enemies.Enemy;
import tungus.games.elude.game.server.enemies.Enemy.EnemyType;

public class Spawner {

	private final EnemyType[][] toSpawn;

	public int maxEnemies = 4;
	public float spawnReload = 3f;

	private final World world;
	private float timeSinceSpawn = 0;
	
	public Spawner(World world, EnemyType[][] whatToSpawn) {
		this.world = world;
		this.toSpawn = whatToSpawn;
	}

	public void update(float deltaTime, float progress) {
		if (world.enemyCount >= maxEnemies) {
			return;
		}
		
		timeSinceSpawn += deltaTime;
		if (timeSinceSpawn >= spawnReload) {
			timeSinceSpawn -= spawnReload;
			int progressIndex = Math.min(toSpawn.length-1, (int)(progress * toSpawn.length));
			int secondIndex = MathUtils.random(toSpawn[progressIndex].length - 1);
			world.addNextFrame.add(Enemy.fromType(world, toSpawn[progressIndex][secondIndex]));
		}
	}
}
