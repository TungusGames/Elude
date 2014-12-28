package tungus.games.elude;

import tungus.games.elude.levels.scoredata.ScoreData;
import tungus.games.elude.menu.mainmenu.MainMenu;
import tungus.games.elude.util.log.FPSLogger;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

public class Elude extends Game {
	
	public static final String VERSION = "V0.1.3";
	public static Class<? extends Screen> mpScreen = null;
	public static final float VIEW_RATIO = 800f/480f;
	private FPSLogger fps;
		
	@Override
	public void create () {
		Gdx.app.log("Nullvector angle", "" + Vector2.Zero.angle());
		long oldTime = TimeUtils.millis();
		Assets.load();
		ScoreData.load();
		setScreen(new MainMenu(this));
		long newTime = TimeUtils.millis();
		float deltaTime = (newTime-oldTime) / 1000f;
		Gdx.app.log("Elude", "Loading time: " + deltaTime);
		//setScreen(GameScreen.newSinglePlayer(this, 7, true)); // for quick debugging
		fps = new FPSLogger("FPSLogger", "Render thread FPS: ");
	}
	
	@Override
	public void render() {
		super.render();
		fps.log();
	}
}
