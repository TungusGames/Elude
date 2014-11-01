package tungus.games.elude.game.client.worldrender;

import tungus.games.elude.Assets;
import tungus.games.elude.game.client.worldrender.Renderable.Effect;
import tungus.games.elude.util.LinkedPool;
import tungus.games.elude.util.LinkedPool.Poolable;

public class ParticleAdder extends Poolable implements Effect {
	private static LinkedPool<ParticleAdder> pool = new LinkedPool<ParticleAdder>(ParticleAdder.class, 15);
	public static Effect create(int a, int t) {
		ParticleAdder p = pool.obtain();
		p.adderID = a; p.typeID = t;
		return p;
	}
	protected int adderID;
	protected int typeID;
	public ParticleAdder(LinkedPool<ParticleAdder> p) {
		super(p);
	}
	
	@Override
	public void render(WorldRenderer wr) {
		wr.lastingEffects.put(adderID, Assets.Particles.values()[typeID].p.obtain());
	}
	
	@Override
	public Renderable clone() {
		return create(adderID, typeID);
	}
}
