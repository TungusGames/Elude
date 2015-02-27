package tungus.games.elude.game.client.worldrender.renderable.effect;

import tungus.games.elude.Assets;
import tungus.games.elude.Assets.Particles;
import tungus.games.elude.game.client.worldrender.WorldRenderer;
import tungus.games.elude.game.client.worldrender.lastingeffects.ParticleEffectPool.PooledEffect;
import tungus.games.elude.game.client.worldrender.renderable.Renderable;
import tungus.games.elude.util.LinkedPool;

public class DebrisAdder extends ParticleAdder {
	private static LinkedPool<ParticleAdder> pool = new LinkedPool<ParticleAdder>(DebrisAdder.class, 15);
	public static Effect create(float[] color, int a, float x, float y, float angle, Particles type) {
		return create(color, a, x, y, angle, type.ordinal());
	}
	public static Effect create(float[] color, int a, float x, float y, float angle, int type) {
		DebrisAdder p = (DebrisAdder)pool.obtain();
		p.adderID = a; p.angle = angle; p.color = color; p.x = x; p.y = y; p.typeID = type;
		return p;
	}
	public static Effect create(float[] color, int a, float x, float y, float angle) {
		return create(color, a, x, y, angle, Particles.DEBRIS.ordinal());
	}
	
	private float angle;
	private float[] color;
	
	public DebrisAdder(LinkedPool<ParticleAdder> p) {
		super(p);
	}
	
	@Override
	public void render(WorldRenderer wr) {
		PooledEffect debris = Assets.Particles.debris(color, angle, typeID);
		debris.setPosition(x, y);
		wr.lastingEffects.put(adderID, debris);
	}
	
	@Override
	public Renderable clone() {
		return create(color, adderID, x, y, angle, typeID);
	}
}
