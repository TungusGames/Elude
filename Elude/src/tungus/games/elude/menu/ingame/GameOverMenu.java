package tungus.games.elude.menu.ingame;

import tungus.games.elude.Assets;
import tungus.games.elude.game.GameScreen;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

public class GameOverMenu extends AbstractIngameMenu {

	private static final float BUTTON_Y = 140;
	private static final float BUTTON_SIZE = 100;
	private static final float BUTTON_SPACING = 60;
	
	public GameOverMenu() {
		super(new Sprite[]{new Sprite(Assets.toMenu), new Sprite(Assets.restart)},
		   new Rectangle[]{new Rectangle(cam.viewportWidth/2-0.5f*BUTTON_SPACING-BUTTON_SIZE, BUTTON_Y, BUTTON_SIZE, BUTTON_SIZE),
						   new Rectangle(cam.viewportWidth/2+0.5f*BUTTON_SPACING,				  BUTTON_Y, BUTTON_SIZE, BUTTON_SIZE)});
	}
	
	@Override
	protected void onButtonTouch(int id) {
		state = STATE_DISAPPEAR;
		stateTime = 0;
		returnOnDisappear = id == 0 ? GameScreen.MENU_QUIT : GameScreen.MENU_RESTART;
	}

}
