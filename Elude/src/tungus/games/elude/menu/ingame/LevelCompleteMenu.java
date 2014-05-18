package tungus.games.elude.menu.ingame;

import tungus.games.elude.Assets;
import tungus.games.elude.game.client.GameScreen;
import tungus.games.elude.levels.scoredata.ScoreData;
import tungus.games.elude.levels.scoredata.ScoreData.ArcadeLevelScore;
import tungus.games.elude.levels.scoredata.ScoreData.FiniteLevelScore;
import tungus.games.elude.menu.levelselect.ScoreDetails;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class LevelCompleteMenu extends AbstractIngameMenu {
	private static final float BUTTON_SIZE = 90;
	private static final float BUTTON_SPACING = 50;
	private static final float BUTTON_Y = 30;
	private final String FINITE_TITLE;
	private final String ARCADE_TITLE = "GAME OVER";
	
	private ScoreDetails scoreDisplay;
	private final int levelNum;
	//private final boolean highScoreTime;
	//private final boolean highScoreHit;
	
	public LevelCompleteMenu(int levelNum, boolean isFinite) {
		super((levelNum+1 < (isFinite ? ScoreData.finiteMedals.size() : MathUtils.floor(ScoreData.totalStars/10f))) ?
				   new Sprite[]{new Sprite(Assets.toMenu), new Sprite(Assets.restart), new Sprite(Assets.nextLevel)} :
				   new Sprite[]{new Sprite(Assets.toMenu), new Sprite(Assets.restart)},
			  (levelNum+1 < (isFinite ? ScoreData.finiteMedals.size() : MathUtils.ceil(ScoreData.totalStars/10f)+1)) ?
				   new Rectangle[]{new Rectangle(cam.viewportWidth/2-1.5f*BUTTON_SIZE-BUTTON_SPACING, BUTTON_Y, BUTTON_SIZE, BUTTON_SIZE),
								   new Rectangle(cam.viewportWidth/2-0.5f*BUTTON_SIZE,				  BUTTON_Y, BUTTON_SIZE, BUTTON_SIZE),
								   new Rectangle(cam.viewportWidth/2+0.5f*BUTTON_SIZE+BUTTON_SPACING, BUTTON_Y, BUTTON_SIZE, BUTTON_SIZE)} :
				   new Rectangle[]{new Rectangle(cam.viewportWidth/2-BUTTON_SIZE-0.5f*BUTTON_SPACING, BUTTON_Y, BUTTON_SIZE, BUTTON_SIZE),
					   			   new Rectangle(cam.viewportWidth/2            +0.5f*BUTTON_SPACING, BUTTON_Y, BUTTON_SIZE, BUTTON_SIZE)});
		FINITE_TITLE = (isFinite ? "STAGE " : "SURVIVAL ") + (levelNum+1) + " COMPLETED";
		scoreDisplay = isFinite ?
				new ScoreDetails(FINITE_TITLE, levelNum, 310f, 11.5f, 40f, true, 7.5f, ScoreData.playerFiniteScore.get(levelNum), false, true) :
				new ScoreDetails(ARCADE_TITLE, levelNum, 310f, 11.5f, 40f, true, 7.5f, ScoreData.playerArcadeScore.get(levelNum), false, true);
		this.levelNum = levelNum;
	}
	
	@Override
	public int render() {
		int r =	super.render(); 
		float move = 1;
		if (state == STATE_APPEAR) {
			move = stateTime/APPEAR_TIME;
		}
		else if (state == STATE_DISAPPEAR) {
			move = 1-stateTime/DISAPPEAR_TIME;
		}
		batch.begin();
		scoreDisplay.render(batch, false, move, 1);	// Two batches implicitly - SpriteBatch will switch when it encounters a new texture
		scoreDisplay.render(batch, true, move, 1);
		Assets.font.setColor(1, 1, 0.6f, 1);
		
		batch.end();
		return r;
	}
	
	@Override
	protected void onButtonTouch(int id) {
		state = STATE_DISAPPEAR;
		stateTime = 0;
		returnOnDisappear = (id == 0 ? GameScreen.MENU_QUIT : id == 1 ? GameScreen.MENU_RESTART : GameScreen.MENU_NEXTLEVEL);
		fadeGameOut = true;
	}
	
	public void setScore(FiniteLevelScore s) {
		scoreDisplay = new ScoreDetails(FINITE_TITLE, levelNum, 310f, 11.5f, 40f, true, 7.5f, s, true, true);
	}
	public void setScore(ArcadeLevelScore s) {
		scoreDisplay = new ScoreDetails(ARCADE_TITLE, levelNum, 310f, 11.5f, 40f, true, 7.5f, s, true, true);
	}
}
