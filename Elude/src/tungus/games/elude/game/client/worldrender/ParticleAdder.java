package tungus.games.elude.game.client.worldrender;

import tungus.games.elude.Assets;
import tungus.games.elude.Assets.Particles;
import tungus.games.elude.game.client.worldrender.Renderable.Effect;
import tungus.games.elude.util.LinkedPool;
import tungus.games.elude.util.LinkedPool.Poolable;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;

public class ParticleAdder extends Poolable implements Effect {
	private static LinkedPool<ParticleAdder> pool = new LinkedPool<ParticleAdder>(ParticleAdder.class, 15);
	public static Effect create(Particles t, float x, float y, int a) {
		ParticleAdder p = pool.obtain();
		p.adderID = a; p.typeID = t.ordinal(); p.x = x; p.y = y;
		return p;
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
