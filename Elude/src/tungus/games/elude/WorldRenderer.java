package tungus.games.elude;


import tungus.games.elude.game.World;
import tungus.games.elude.util.CamShaker;

import com.badlogic.gdx.graphics.OrthographicCamera;
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
	
	public void render(float deltaTime) {
		
		batch.begin();
		int size = world.vessels.size();
		for(int i = 0; i < size; i++) {
			world.vessels.get(i).draw(batch);
		}		
		
		size = world.enemies.size();
		for(int i = 0; i < size; i++) {
			world.enemies.get(i).draw(batch);
		}
		
		/*size = world.rockets.size();
		for (int i = 0; i < size; i++) {
			world.rockets.get(i).draw(batch);
		}*/
		size = world.pickups.size();
		for(int i = 0; i < size; i++) {
			world.pickups.get(i).draw(batch);
		}
		
		size = world.particles.size();
		for (int i = 0; i < size; i++) {
			if (world.particles.get(i).isComplete()) {
				world.particles.get(i).free();
				world.particles.remove(i);
				i--;
				size--;
			} else {
				world.particles.get(i).draw(batch);
			}
		}
		batch.end();
	}
	

}
