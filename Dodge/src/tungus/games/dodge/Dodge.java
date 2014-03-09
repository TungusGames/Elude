package tungus.games.dodge;

import tungus.games.dodge.screens.LevelSelectScreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.FPSLogger;

public class Dodge extends Game {
	
	FPSLogger fps;
		
	@Override
	public void create () {
		Assets.load();
		setScreen(new LevelSelectScreen(this));
		fps = new FPSLogger();
	}
	
	@Override
	public void render() {
		super.render();
		fps.log();
	}
}
