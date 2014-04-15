package tungus.games.elude.menu.ingame;

import tungus.games.elude.Assets;
import tungus.games.elude.game.GameScreen;
import tungus.games.elude.levels.levelselect.ScoreDetails;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class LevelCompleteMenu extends AbstractIngameMenu {
	private static final float BUTTON_SIZE = 90;
	private static final float BUTTON_SPACING = 60;
	private static final float BUTTON_Y = 50;
	private static final Interpolation interp = Interpolation.pow2In;
	
	private final ScoreDetails scoreDisplay;
	
	public LevelCompleteMenu(int levelNum, boolean isFinite) {
		super(new Sprite[]{new Sprite(Assets.toMenu), new Sprite(Assets.restart), new Sprite(Assets.resume)},
		   new Rectangle[]{new Rectangle(cam.viewportWidth/2-1.5f*BUTTON_SIZE-BUTTON_SPACING, BUTTON_Y, BUTTON_SIZE, BUTTON_SIZE),
						   new Rectangle(cam.viewportWidth/2-0.5f*BUTTON_SIZE,				  BUTTON_Y, BUTTON_SIZE, BUTTON_SIZE),
						   new Rectangle(cam.viewportWidth/2+0.5f*BUTTON_SIZE+BUTTON_SPACING, BUTTON_Y, BUTTON_SIZE, BUTTON_SIZE)});
		scoreDisplay = new ScoreDetails(levelNum, isFinite, 310f, 40f, true, 7.5f);
	}
	
	/*@Override
	public int update(float deltaTime, Vector3 touch) {
		
		return super.update(deltaTime, touch);
	}*/
	
	@Override
	public void render() {
		super.render();
		float a = 1;
		float move = 1;
		if (state == STATE_APPEAR) {
			move = a = stateTime/APPEAR_TIME;
		}
		else  {
			if (state == STATE_DISAPPEAR) {
				move = a = 1-stateTime/DISAPPEAR_TIME;
			}
		}
		a = interp.apply(a);
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
}
