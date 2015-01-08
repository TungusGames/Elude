package tungus.games.elude.game.client.worldrender.renderable.effect;

import tungus.games.elude.Assets;
import tungus.games.elude.Assets.Particles;
import tungus.games.elude.game.client.worldrender.WorldRenderer;
import tungus.games.elude.game.client.worldrender.lastingeffects.ParticleEffectPool.PooledEffect;
import tungus.games.elude.game.client.worldrender.renderable.Renderable;
import tungus.games.elude.game.client.worldrender.renderable.Renderable.Effect;
import tungus.games.elude.game.server.enemies.Enemy.EnemyType;
import tungus.games.elude.util.LinkedPool;


public class DebrisAdder extends ParticleAdder {
	private static LinkedPool<ParticleAdder> pool = new LinkedPool<ParticleAdder>(DebrisAdder.class, 15);
	public static Effect create(EnemyType e, int a, float x, float y, float angle) {
		DebrisAdder p = (DebrisAdder)pool.obtain();
		p.adderID = a; p.typeID = Particles.DEBRIS.ordinal(); p.angle = angle; p.enemyType = e.ordinal(); p.x = x; p.y = y;
		return p;
	}
	
	private float angle;
	private int enemyType;
	
	public DebrisAdder(LinkedPool<ParticleAdder> p) {
		super(p);
	}
	
	@Override
	public void render(WorldRenderer wr) {
		PooledEffect debris = Assets.Particles.debris(EnemyType.values()[enemyType].debrisColor, angle);
		debris.setPosition(x, y);
		wr.lastingEffects.put(adderID, debris);
	}
	
	@Override
	public Renderable clone() {
		return create(EnemyType.values()[enemyType], adderID, x, y, angle);
	}
}
