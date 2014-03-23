package tungus.games.elude.menu;

import tungus.games.elude.BaseScreen;
import tungus.games.elude.levels.levelselect.LevelSelectScreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;


public class PlayMenu extends BaseScreen {

	public PlayMenu(Game game) {
		super(game);
		Gdx.input.setCatchBackKey(false);
	}
	
	@Override
	public void render(float deltaTime) {
		if (Gdx.input.isTouched()) {
			Screen next = new LevelSelectScreen(game, Gdx.input.getX() < Gdx.graphics.getWidth()/2);
			game.setScreen(next);
		}
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	}

}
