package tungus.games.elude;

import tungus.games.elude.Assets.EludeMusic;
import tungus.games.elude.levels.scoredata.ScoreData;
import tungus.games.elude.menu.mainmenu.MainMenu;
import tungus.games.elude.util.log.FPSLogger;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.TimeUtils;

public class Elude extends Game {
	
	public static final String VERSION = "1.0";
	public static Class<? extends Screen> mpScreen = null;
	public static final float VIEW_RATIO = 800f/480f;
	private FPSLogger fps;
		
	@Override
	public void create () {
		long oldTime = TimeUtils.millis();
		EludeMusic.currentPlaying = null;
		Assets.load();
		ScoreData.load();
		setScreen(new MainMenu(this));
		long newTime = TimeUtils.millis();
		float deltaTime = (newTime-oldTime) / 1000f;
		Gdx.app.log("Elude", "Loading time: " + deltaTime);
		//setScreen(GameScreen.newSinglePlayer(this, 49, true)); // for quick debugging		
		fps = new FPSLogger("FPSLogger", "Render thread FPS: ");
	}
	
	@Override
	public void render() {
		super.render();
		fps.log();
	}
}
