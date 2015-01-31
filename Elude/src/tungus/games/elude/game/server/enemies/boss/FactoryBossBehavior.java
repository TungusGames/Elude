package tungus.games.elude.game.server.enemies.boss;

public interface FactoryBossBehavior {
	
	public void startPeriod(FactoryBoss boss);
	public void update(FactoryBoss boss, float deltaTime);
	
}
