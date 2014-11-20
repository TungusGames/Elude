package tungus.games.elude.game.client.worldrender;


import java.util.List;

import tungus.games.elude.game.multiplayer.transfer.RenderInfo;
import tungus.games.elude.game.server.World;
import tungus.games.elude.util.CamShaker;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.IntMap;

public class WorldRenderer {
	
	private RenderPhase[] phases = RenderPhase.values();	
	
	public OrthographicCamera camera;
	
	int vesselID;
	boolean updateParticles;
	SpriteBatch batch;
	CamShaker camShaker;
	IntMap<PooledEffect> lastingEffects;
	
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
		batch.end();
	}
	
	public void resetContext() {
		for (RenderPhase p : phases) {
			p.renderer.resetContext();
		}
	}
}
