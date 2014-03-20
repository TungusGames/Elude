package tungus.games.elude;

import tungus.games.elude.levels.levelselect.LevelSelectScreen;
import tungus.games.elude.levels.scoredata.ScoreData;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.FPSLogger;

public class Elude extends Game {
	
	FPSLogger fps;
		
	@Override
	public void create () {
		Assets.load();
		ScoreData.load();
		ScoreData.playerFiniteScore.get(0).completed = true;
		ScoreData.playerFiniteScore.get(0).timeTaken = 90f;
		ScoreData.playerFiniteScore.get(0).hpLost = 110;
		setScreen(new LevelSelectScreen(this,true));
		fps = new FPSLogger();
	}
	
	@Override
	public void render() {
		super.render();
		fps.log();
	}
}
