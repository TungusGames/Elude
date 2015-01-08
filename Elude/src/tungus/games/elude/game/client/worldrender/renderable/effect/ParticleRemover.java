package tungus.games.elude.game.client.worldrender.renderable.effect;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

import tungus.games.elude.game.client.worldrender.WorldRenderer;
import tungus.games.elude.game.client.worldrender.lastingeffects.LastingEffect;
import tungus.games.elude.game.client.worldrender.renderable.Renderable;
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
		LastingEffect boundEffect = wr.lastingEffects.getFirst(adderID);
		if (boundEffect != null) {
			boundEffect.allowCompletion();
		} else {
			Gdx.app.setLogLevel(Application.LOG_ERROR);
			Gdx.app.log("ERROR", "No lastingeffect for ID " + adderID + " found to remove");
			Gdx.app.setLogLevel(Application.LOG_DEBUG);
		}
		
	}
	@Override
	public Renderable clone() {
		return create(adderID);
	}
}
