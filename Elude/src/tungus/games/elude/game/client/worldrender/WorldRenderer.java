package tungus.games.elude.game.client.worldrender;


import java.util.Iterator;
import java.util.List;

import tungus.games.elude.game.client.worldrender.lastingeffects.LastingEffect;
import tungus.games.elude.game.client.worldrender.phases.RenderPhase;
import tungus.games.elude.game.client.worldrender.renderable.Renderable;
import tungus.games.elude.game.multiplayer.transfer.RenderInfo;
import tungus.games.elude.game.server.World;
import tungus.games.elude.util.CamShaker;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class WorldRenderer {

	private RenderPhase[] phases = RenderPhase.values();	

	public OrthographicCamera camera;

	public int vesselID;
	public boolean updateParticles;
	public SpriteBatch batch;
	public CamShaker camShaker;
	public LastingEffectCollection lastingEffects;

	public WorldRenderer(int myVesselID) {
		batch = new SpriteBatch(5460);
		camera = new OrthographicCamera(World.WIDTH, World.HEIGHT);
		camera.position.set(World.WIDTH/2, World.HEIGHT/2, 0);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		camShaker = new CamShaker(batch);
		lastingEffects = new LastingEffectCollection();
		this.vesselID = myVesselID;
	}

	public void render(float deltaTime, float alpha, RenderInfo renderInfo, boolean updateEffects) {
		this.updateParticles = updateEffects;
		camShaker.update(deltaTime);
		batch.setColor(1, 1, 1, alpha);
		batch.begin();
		for (int i = 0; i < phases.length; i++) {
			RenderPhase p = phases[i];
			List<Renderable> phaseList = renderInfo.phases.get(i);
			p.renderer.begin(p, this, deltaTime);
			for (Renderable r : phaseList) {
				p.renderer.render(r);
			}
			p.renderer.end();
		}
		Iterator<LastingEffect> it = lastingEffects.iterator();
		while (it.hasNext()) {
			LastingEffect effect = it.next();
			if (updateEffects) {
				effect.render(batch, deltaTime);					
			} else {
				effect.render(batch, 0);
			}

			if (effect.isComplete()) {
				it.remove();
			}
		}
		batch.end();
	}

	public void resetContext() {
		for (RenderPhase p : phases) {
			p.renderer.resetContext();
		}
	}
}
