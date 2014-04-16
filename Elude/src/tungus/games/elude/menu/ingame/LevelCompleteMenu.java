package tungus.games.elude.menu.ingame;

import tungus.games.elude.Assets;
import tungus.games.elude.game.GameScreen;
import tungus.games.elude.levels.levelselect.ScoreDetails;
import tungus.games.elude.levels.scoredata.ScoreData;
import tungus.games.elude.levels.scoredata.ScoreData.ArcadeLevelScore;
import tungus.games.elude.levels.scoredata.ScoreData.FiniteLevelScore;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

public class LevelCompleteMenu extends AbstractIngameMenu {
	private static final float BUTTON_SIZE = 90;
	private static final float BUTTON_SPACING = 50;
	private static final float BUTTON_Y = 30;
	
	private ScoreDetails scoreDisplay;
	private final int levelNum;
	
	public LevelCompleteMenu(int levelNum, boolean isFinite) {
		super(new Sprite[]{new Sprite(Assets.toMenu), new Sprite(Assets.restart), new Sprite(Assets.resume)},
		   new Rectangle[]{new Rectangle(cam.viewportWidth/2-1.5f*BUTTON_SIZE-BUTTON_SPACING, BUTTON_Y, BUTTON_SIZE, BUTTON_SIZE),
						   new Rectangle(cam.viewportWidth/2-0.5f*BUTTON_SIZE,				  BUTTON_Y, BUTTON_SIZE, BUTTON_SIZE),
						   new Rectangle(cam.viewportWidth/2+0.5f*BUTTON_SIZE+BUTTON_SPACING, BUTTON_Y, BUTTON_SIZE, BUTTON_SIZE)});
		scoreDisplay = isFinite ?
				new ScoreDetails("LEVEL COMPLETE", levelNum, 310f, 40f, true, 7.5f, ScoreData.playerFiniteScore.get(levelNum)) :
				new ScoreDetails("GAME OVER", levelNum, 310f, 40f, true, 7.5f, ScoreData.playerArcadeScore.get(levelNum));
		this.levelNum = levelNum;
	}
	
	@Override
	public void render() {
		super.render();
		float move = 1;
		if (state == STATE_APPEAR) {
			move = stateTime/APPEAR_TIME;
			Gdx.app.log("Interp", ""+move);
		}
		else if (state == STATE_DISAPPEAR) {
			move = 1-stateTime/DISAPPEAR_TIME;
		}
		batch.begin();
		scoreDisplay.render(batch, false, move, 1);	// Two batches implicitly - SpriteBatch will switch when it encounters a new texture
		scoreDisplay.render(batch, true, move, 1);
		batch.end();
	}
	
	@Override
	protected void onButtonTouch(int id) {
		state = STATE_DISAPPEAR;
		stateTime = 0;
		returnOnDisappear = (id == 0 ? GameScreen.MENU_QUIT : id == 1 ? GameScreen.MENU_RESTART : GameScreen.STATE_PLAYING);
	}
	
	public void setScore(FiniteLevelScore s) {
		scoreDisplay = new ScoreDetails("LEVEL COMPLETE", levelNum, 310f, 40f, true, 7.5f, s);
	}
	public void setScore(ArcadeLevelScore s) {
		scoreDisplay = new ScoreDetails("GAME OVER", levelNum, 310f, 40f, true, 7.5f, s);
	}
}
