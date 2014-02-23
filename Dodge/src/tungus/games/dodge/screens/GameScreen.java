package tungus.games.dodge.screens;

import tungus.games.dodge.WorldRenderer;
import tungus.games.dodge.game.World;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;

public class GameScreen extends BaseScreen {
	
	private World world;
	private WorldRenderer renderer;

	public GameScreen(Game game) {
		super(game);
		world = new World();
		renderer = new WorldRenderer(world);
	}
	

	@Override
	public void render(float deltaTime) {
		deltaTime = Math.min(deltaTime, 0.05f);
		world.update(deltaTime);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		renderer.render();
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

}
