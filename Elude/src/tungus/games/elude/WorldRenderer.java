package tungus.games.elude;


import tungus.games.elude.game.World;
import tungus.games.elude.util.CamShaker;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class WorldRenderer {

	private World world;
	private SpriteBatch batch;
	public OrthographicCamera camera;
	
	public WorldRenderer(World world) {
		this.world = world;
		batch = new SpriteBatch(5460);
		camera = new OrthographicCamera(World.WIDTH, World.HEIGHT);
		camera.position.set(World.WIDTH/2, World.HEIGHT/2, 0);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		CamShaker.INSTANCE = new CamShaker(batch);
	}
	
	public void render(float deltaTime, float alpha) {
		
		batch.begin();
		int size = world.enemies.size();
		for(int i = 0; i < size; i++) {
			world.enemies.get(i).draw(batch, alpha);
		}
		
		size = world.pickups.size();
		for(int i = 0; i < size; i++) {
			world.pickups.get(i).draw(batch, alpha);
		}
		
		size = world.vessels.size();
		for(int i = 0; i < size; i++) {
			world.vessels.get(i).draw(batch, alpha);
		}		
		
		size = world.particles.size();
		for (int i = 0; i < size; i++) {
			PooledEffect p = world.particles.get(i);
			if (p.isComplete()) {
				p.free();
				world.particles.remove(i);
				i--;
				size--;
			} else {
				batch.setColor(1, 1, 1, alpha);
				p.draw(batch);
			}
		}
		batch.end();
	}
	

}
