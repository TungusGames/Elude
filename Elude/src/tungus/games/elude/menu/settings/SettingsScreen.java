package tungus.games.elude.menu.settings;

import tungus.games.elude.BaseScreen;
import tungus.games.elude.menu.mainmenu.MainMenu;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;

public class SettingsScreen extends BaseScreen {
	
	private static final int STATE_ACTIVE = 1;
	private static final int STATE_FADEOUT = 2;
	private int state = STATE_ACTIVE;
	
	public SettingsScreen(Game game) {
		super(game);
		Gdx.input.setInputProcessor(new InputAdapter() {
			@Override
			public boolean keyDown(int keycode) {
				if (keycode == Keys.BACK || keycode == Keys.ESCAPE) {					
					state = STATE_FADEOUT;
					return true;
				}
				return false;
			}
		});
	}
	
	@Override
	public void render(float deltaTime) {
		if (state == STATE_FADEOUT) {
			game.setScreen(new MainMenu(game));
		}
	}
}
