package tungus.games.elude.game.client.worldrender;

import tungus.games.elude.game.client.worldrender.Renderable.Effect;
import tungus.games.elude.util.LinkedPool;
import tungus.games.elude.util.LinkedPool.Poolable;

public class ParticleRemover extends Poolable implements Effect {
	private static LinkedPool<ParticleRemover> pool = new LinkedPool<ParticleRemover>(ParticleRemover.class, 15);
	public static Effect create(int a) {
		ParticleRemover p = pool.obtain();
		p.adderID = a; 
		return p;
	}
	
	private int adderID;
	
	public ParticleRemover(LinkedPool<ParticleRemover> p) {
		super(p);
	}

	@Override
	public void render(WorldRenderer wr) {
		wr.lastingEffects.get(adderID).allowCompletion();
	}
	
	@Override
	public Renderable clone() {
		return create(adderID);
	}
}
