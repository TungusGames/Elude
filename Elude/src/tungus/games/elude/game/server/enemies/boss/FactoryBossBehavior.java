package tungus.games.elude.game.server.enemies.boss;

import tungus.games.elude.game.server.World;

public interface FactoryBossBehavior {
	
	public void startPeriod(World world, FactoryBoss boss);
	public void update(World world, FactoryBoss boss, float deltaTime);
	
}
