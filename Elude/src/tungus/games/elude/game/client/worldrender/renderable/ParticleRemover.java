package tungus.games.elude.game.client.worldrender.renderable;

import tungus.games.elude.game.client.worldrender.WorldRenderer;
import tungus.games.elude.game.client.worldrender.renderable.Renderable.Effect;
import tungus.games.elude.util.LinkedPool;

public class ParticleRemover extends Effect {
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
