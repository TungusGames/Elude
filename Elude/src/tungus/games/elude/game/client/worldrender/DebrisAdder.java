package tungus.games.elude.game.client.worldrender;

import tungus.games.elude.Assets;
import tungus.games.elude.game.server.enemies.Enemy.EnemyType;
import tungus.games.elude.util.LinkedPool;

public class DebrisAdder extends ParticleAdder {
	private static LinkedPool<ParticleAdder> pool = new LinkedPool<ParticleAdder>(DebrisAdder.class, 15);
	public static Effect create(int a, int t, float angle, EnemyType e) {
		DebrisAdder p = (DebrisAdder)pool.obtain();
		p.adderID = a; p.typeID = t; p.angle = angle; p.enemyType = e.ordinal();
		return p;
	}
	
	private float angle;
	private int enemyType;
	
	public DebrisAdder(LinkedPool<ParticleAdder> p) {
		super(p);
	}
	
	@Override
	public void render(WorldRenderer wr) {
		wr.lastingEffects.put(adderID, Assets.Particles.debris(EnemyType.values()[enemyType].debrisColor, angle));
	}
}
