package tungus.games.elude;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public abstract class BaseScreen implements Screen {
	
	protected final Game game;
	
	public BaseScreen(Game game) {
		this.game = game;
	}

	@Override
	public void render(float delta) {

	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void show() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {

	}

}
