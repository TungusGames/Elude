package tungus.games.dodge;

import tungus.games.dodge.game.World;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class WorldRenderer {

	private World world;
	private SpriteBatch batch;
	private OrthographicCamera camera;
	
	public WorldRenderer(World world) {
		this.world = world;
		batch = new SpriteBatch();
		camera = new OrthographicCamera(World.WIDTH, World.HEIGHT);
		camera.position.set(World.WIDTH/2, World.HEIGHT/2, 0);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
	}
	
	public void render() {
		batch.begin();
		int size = world.vessels.size();
		for(int i = 0; i < size; i++) {
			world.vessels.get(i).draw(batch);
		}		
		batch.end();
		
		batch.begin();
		size = world.rockets.size();
		for (int i = 0; i < size; i++) {
			world.rockets.get(i).draw(batch);
		}
		batch.end();
		
	}

}