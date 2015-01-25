package tungus.games.elude.game.client.worldrender.renderable.effect;

import tungus.games.elude.Assets;
import tungus.games.elude.Assets.Particles;
import tungus.games.elude.game.client.worldrender.WorldRenderer;
import tungus.games.elude.game.client.worldrender.lastingeffects.ParticleEffectPool.PooledEffect;
import tungus.games.elude.game.client.worldrender.renderable.Renderable;
import tungus.games.elude.util.LinkedPool;

public class DebrisAdder extends ParticleAdder {
	private static LinkedPool<ParticleAdder> pool = new LinkedPool<ParticleAdder>(DebrisAdder.class, 15);
	public static Effect create(float[] color, int a, float x, float y, float angle, boolean big) {
		DebrisAdder p = (DebrisAdder)pool.obtain();
		p.adderID = a; p.typeID = Particles.DEBRIS.ordinal(); p.angle = angle; p.color = color; p.x = x; p.y = y; p.big = big;
		return p;
	}
	public static Effect create(float[] color, int a, float x, float y, float angle) {
		return create(color, a, x, y, angle, false);
	}
	
	private float angle;
	private float[] color;
	private boolean big;
	
	public DebrisAdder(LinkedPool<ParticleAdder> p) {
		super(p);
	}
	
	@Override
	public void render(WorldRenderer wr) {
		PooledEffect debris = Assets.Particles.debris(color, angle, big);
		debris.setPosition(x, y);
		wr.lastingEffects.put(adderID, debris);
	}
	
	@Override
	public Renderable clone() {
		return create(color, adderID, x, y, angle, big);
	}
}
