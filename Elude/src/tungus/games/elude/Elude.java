package tungus.games.elude;

import tungus.games.elude.game.GameScreen;
import tungus.games.elude.levels.scoredata.ScoreData;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.FPSLogger;

public class Elude extends Game {
	
	FPSLogger fps;
		
	@Override
	public void create () {
		Assets.load();
		ScoreData.load();
		//setScreen(new MainMenu(this));
		setScreen(new GameScreen(this, 0, true)); // for quick debugging
		fps = new FPSLogger();
	}
	
	@Override
	public void render() {
		super.render();
		fps.log();
	}
}
