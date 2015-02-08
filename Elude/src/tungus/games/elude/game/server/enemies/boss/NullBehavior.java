package tungus.games.elude.game.server.enemies.boss;

import tungus.games.elude.game.server.World;

public class NullBehavior implements FactoryBossBehavior {
	
	@Override
	public void startPeriod(World world, FactoryBoss boss) { }
	
	@Override
	public void update(World world, FactoryBoss boss, float deltaTime) { }

}
