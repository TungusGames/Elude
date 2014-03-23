package tungus.games.elude;

import tungus.games.elude.levels.scoredata.ScoreData;
import tungus.games.elude.menu.PlayMenu;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.FPSLogger;

public class Elude extends Game {
	
	FPSLogger fps;
		
	@Override
	public void create () {
		Assets.load();
		ScoreData.load();
		setScreen(new PlayMenu(this));
		fps = new FPSLogger();
	}
	
	@Override
	public void render() {
		super.render();
		fps.log();
	}
}
