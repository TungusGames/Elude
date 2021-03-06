package tungus.games.elude.menu.ingame;

import tungus.games.elude.Assets;
import tungus.games.elude.Assets.Strings;
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
	
	private ScoreDetails scoreDisplay;
	private final int levelNum;
	//private final boolean highScoreTime;
	//private final boolean highScoreHit;
	
	public LevelCompleteMenu(int levelNum, boolean isFinite) {
		super((levelNum+1 < (isFinite ? ScoreData.finiteMedals.size() : Math.min(MathUtils.floor(ScoreData.starsEarned/10f), ScoreData.arcadeMedals.size()))) ?
				   new Sprite[]{new Sprite(Assets.Tex.TO_MENU.t), new Sprite(Assets.Tex.RESTART.t), new Sprite(Assets.Tex.NEXT_LEVEL.t)} :
				   new Sprite[]{new Sprite(Assets.Tex.TO_MENU.t), new Sprite(Assets.Tex.RESTART.t)},
			  (levelNum+1 < (isFinite ? ScoreData.finiteMedals.size() : Math.min(MathUtils.floor(ScoreData.starsEarned/10f), ScoreData.arcadeMedals.size()))) ?
				   new Rectangle[]{new Rectangle(FRUSTUM_WIDTH/2-1.5f*BUTTON_SIZE-BUTTON_SPACING, BUTTON_Y, BUTTON_SIZE, BUTTON_SIZE),
								   new Rectangle(FRUSTUM_WIDTH/2-0.5f*BUTTON_SIZE,				  BUTTON_Y, BUTTON_SIZE, BUTTON_SIZE),
								   new Rectangle(FRUSTUM_WIDTH/2+0.5f*BUTTON_SIZE+BUTTON_SPACING, BUTTON_Y, BUTTON_SIZE, BUTTON_SIZE)} :
				   new Rectangle[]{new Rectangle(FRUSTUM_WIDTH/2-BUTTON_SIZE-0.5f*BUTTON_SPACING, BUTTON_Y, BUTTON_SIZE, BUTTON_SIZE),
					   			   new Rectangle(FRUSTUM_WIDTH/2            +0.5f*BUTTON_SPACING, BUTTON_Y, BUTTON_SIZE, BUTTON_SIZE)});
		scoreDisplay = isFinite ?
				new ScoreDetails(Strings.finiteNames[levelNum], levelNum, 310f, 11.5f, 40f, true, 7.5f, ScoreData.playerFiniteScore.get(levelNum), false, true, 800) :
				new ScoreDetails(Strings.arcadeNames[levelNum], levelNum, 310f, 11.5f, 40f, true, 7.5f, ScoreData.playerArcadeScore.get(levelNum), false, true, 800);
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
		scoreDisplay = new ScoreDetails(Strings.finiteNames[levelNum], levelNum, 310f, 11.5f, 40f, true, 7.5f, s, true, true, 800);
	}
	public void setScore(ArcadeLevelScore s) {
		scoreDisplay = new ScoreDetails(Strings.arcadeNames[levelNum], levelNum, 310f, 11.5f, 40f, true, 7.5f, s, true, true, 800);
	}
	
	//Exits the game on back key
	@Override
	public void onBackKey() {
		if (state == STATE_IDLE)
			onButtonTouch(0);
	}
}
