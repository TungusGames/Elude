package tungus.games.elude;

import tungus.games.elude.levels.scoredata.ScoreData;
import tungus.games.elude.menu.MainMenu;
import tungus.games.elude.util.log.FPSLogger;

import com.badlogic.gdx.Game;

public class Elude extends Game {
	
	FPSLogger fps;
		
	@Override
	public void create () {
		Assets.load();
		ScoreData.load();
		setScreen(new MainMenu(this));
		//setScreen(new GameScreen(this, 0, true)); // for quick debugging
		fps = new FPSLogger("FPSLogger", "Render thread FPS: ");
	}
	
	@Override
	public void render() {
		super.render();
		fps.log();
	}
}
