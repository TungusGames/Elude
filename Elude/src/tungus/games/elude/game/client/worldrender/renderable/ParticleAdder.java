package tungus.games.elude.game.client.worldrender.renderable;

import tungus.games.elude.Assets;
import tungus.games.elude.Assets.Particles;
import tungus.games.elude.game.client.worldrender.WorldRenderer;
import tungus.games.elude.game.client.worldrender.renderable.Renderable.Effect;
import tungus.games.elude.util.LinkedPool;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;

public class ParticleAdder extends Effect {
	private static LinkedPool<ParticleAdder> pool = new LinkedPool<ParticleAdder>(ParticleAdder.class, 15);
	public static Effect create(Particles t, float x, float y, int a) {
		ParticleAdder p = pool.obtain();
		p.adderID = a; p.typeID = t.ordinal(); p.x = x; p.y = y;
		return p;
	}
	public static Effect create(Particles type, float x, float y) {
		return create(type, x, y, -1);
	}
	protected int adderID;
	protected int typeID;
	
	protected float x, y;
	
	public ParticleAdder(LinkedPool<ParticleAdder> p) {
		super(p);
	}
	
	@Override
	public void render(WorldRenderer wr) {
		PooledEffect e = Assets.Particles.values()[typeID].p.obtain();
		setPos(e);
		wr.lastingEffects.put(adderID, e);
	}
	
	protected void setPos(PooledEffect p) {
		p.setPosition(x, y);
	}
	
	@Override
	public Renderable clone() {
		return create(Particles.values()[typeID], x, y, adderID);
	}
}
