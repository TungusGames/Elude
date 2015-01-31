package tungus.games.elude.game.server.enemies.boss;

public class ShootBehavior implements FactoryBossBehavior {
	
	private static final int ROCKETS_AT_START = 8;
	private static final int ROCKETS_AT_END = 25;
	
	private static final float PERIOD_EDGE = 0.5f;
	private static final float SHOOT_TIME = FactoryBoss.TIME_PER_BEHAVIOR - PERIOD_EDGE * 2;
	
	private int rocketsInVolley = ROCKETS_AT_START;
	private int rocketsShot = 0;
	private float reload = SHOOT_TIME / (rocketsInVolley - 1);
	private float timeSinceShot = 0;
	private float timeSinceStart = 0;
	
	@Override
	public void update(FactoryBoss boss, float deltaTime) {
		timeSinceStart += deltaTime;
		if (timeSinceStart < PERIOD_EDGE || rocketsShot >= rocketsInVolley) {
			return;
		}
		timeSinceShot += deltaTime;
		if (timeSinceShot > reload) {
			timeSinceShot -= reload;
			boss.shootRocket();
		}
	}
	
	@Override
	public void startPeriod(FactoryBoss boss) {
		rocketsInVolley = (int)((1 - boss.hp / boss.maxHp) * (ROCKETS_AT_END + 1 - ROCKETS_AT_START)) + ROCKETS_AT_START;
		reload = SHOOT_TIME / (rocketsInVolley - 1);
		timeSinceStart = 0;
		timeSinceShot = reload; // Can shoot
	}
}
