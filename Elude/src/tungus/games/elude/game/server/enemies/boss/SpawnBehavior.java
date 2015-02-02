package tungus.games.elude.game.server.enemies.boss;

import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.enemies.Enemy;
import tungus.games.elude.game.server.enemies.Enemy.EnemyType;

public class SpawnBehavior implements FactoryBossBehavior {
	
	private static final float PERIOD_EDGE = 3f;
	private static final int DEFAULT_COUNT = 3;
		
	private final EnemyType type;
	private final int count;
	private final float reload;
	
	private int spawnedThisPeriod = 0;
	private float timeSinceSpawn;
	private float timeSinceStart = 0;
	
	public SpawnBehavior(EnemyType type) {
		this(type, DEFAULT_COUNT);
	}
	
	public SpawnBehavior(EnemyType type, int count) {
		this.type = type;
		this.count = count;
		reload = (FactoryBoss.TIME_PER_BEHAVIOR - 2 * PERIOD_EDGE) / (count - 1);
		timeSinceSpawn = reload; // Spawn immediately once inside
	}
	
	@Override
	public void startPeriod(World world, FactoryBoss boss) {
		spawnedThisPeriod = 0;
		timeSinceStart = 0;
	}

	@Override
	public void update(World world, FactoryBoss boss, float deltaTime) {
		timeSinceStart += deltaTime;
		if (timeSinceStart <= PERIOD_EDGE || spawnedThisPeriod >= count) {
			return;
		}
		timeSinceSpawn += deltaTime;
		if (timeSinceSpawn >= reload) {
			timeSinceSpawn -= reload;
			Enemy toAdd = Enemy.fromType(world, type, boss.pos.cpy());
			toAdd.countsForProgress = false;
			world.addNextFrame.add(toAdd);
		}
	}

}
