package tungus.games.dodge;

import tungus.games.dodge.screens.GameScreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.FPSLogger;

public class Dodge extends Game {
	
	FPSLogger fps;
		
	@Override
	public void create () {
		Assets.load();
		setScreen(new GameScreen(this));
		fps = new FPSLogger();
	}
	
	@Override
	public void render() {
		super.render();
		fps.log();
	}
}
