package tungus.games.elude;

import tungus.games.elude.game.client.GameScreen;
import tungus.games.elude.levels.scoredata.ScoreData;
import tungus.games.elude.menu.mainmenu.MainMenu;
import tungus.games.elude.util.log.FPSLogger;

import com.badlogic.gdx.Game;

public class Elude extends Game {
	
	public static final String VERSION = "V0.1";
	private FPSLogger fps;
		
	@Override
	public void create () {
		Assets.load();
		ScoreData.load();
		setScreen(new MainMenu(this));
		//setScreen(GameScreen.newSinglePlayer(this, 7, true)); // for quick debugging
		fps = new FPSLogger("FPSLogger", "Render thread FPS: ");
	}
	
	@Override
	public void render() {
		super.render();
		fps.log();
	}
}
