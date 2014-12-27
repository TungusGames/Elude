package tungus.games.elude.game.client.worldrender;


import java.util.Iterator;
import java.util.List;

import tungus.games.elude.game.client.worldrender.phases.RenderPhase;
import tungus.games.elude.game.client.worldrender.renderable.Renderable;
import tungus.games.elude.game.multiplayer.transfer.RenderInfo;
import tungus.games.elude.game.server.World;
import tungus.games.elude.util.CamShaker;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Entry;

public class WorldRenderer {
	
	private RenderPhase[] phases = RenderPhase.values();	
	
	public OrthographicCamera camera;
	
	public int vesselID;
	public boolean updateParticles;
	public SpriteBatch batch;
	public CamShaker camShaker;
	public IntMap<PooledEffect> lastingEffects;
	
	public WorldRenderer(int myVesselID) {
		batch = new SpriteBatch(5460);
		camera = new OrthographicCamera(World.WIDTH, World.HEIGHT);
		camera.position.set(World.WIDTH/2, World.HEIGHT/2, 0);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		camShaker = new CamShaker(batch);
		lastingEffects = new IntMap<PooledEffect>(100);
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
		//while (effects.hasNext) {
			for (Iterator<Entry<PooledEffect>> it = lastingEffects.iterator(); it.hasNext();) {
				PooledEffect effect = it.next().value;
				
				if (updateEffects) {
					effect.draw(batch, deltaTime);					
				} else {
					effect.draw(batch);
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
