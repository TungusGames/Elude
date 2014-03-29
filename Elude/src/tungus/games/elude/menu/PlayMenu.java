package tungus.games.elude.menu;

import tungus.games.elude.BaseScreen;
import tungus.games.elude.levels.levelselect.LevelSelectScreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;


public class PlayMenu extends BaseScreen {

	private boolean initialTouch = false;
	
	private InputAdapter listener = new InputAdapter() {
		@Override
		public boolean keyDown(int keycode) {
			if (keycode == Keys.BACK || keycode == Keys.ESCAPE) {
				game.setScreen(new MainMenu(game));
				return true;
			}
			return false;
		}
	};
	
	public PlayMenu(Game game) {
		super(game);
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setInputProcessor(listener);
		if (Gdx.input.isTouched())
			initialTouch = true;
	}
	
	@Override
	public void render(float deltaTime) {
		if (Gdx.input.isTouched()) {
			if (!initialTouch) {
				Screen next = new LevelSelectScreen(game, Gdx.input.getX() < Gdx.graphics.getWidth()/2);
				game.setScreen(next);
			}
		} else initialTouch = false;
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	}

}
