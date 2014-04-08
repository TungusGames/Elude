package tungus.games.elude.menu.ingame;

import tungus.games.elude.Assets;
import tungus.games.elude.game.GameScreen;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

public class PauseMenu extends AbstractIngameMenu {
	
	private static final float BUTTON_SIZE = 150;
	private static final float BUTTON_SPACING = 60;
	private static final float BUTTON_Y = 240-BUTTON_SIZE/2;
	
	public PauseMenu() {
		super(new Sprite[]{new Sprite(Assets.toMenu), new Sprite(Assets.restart), new Sprite(Assets.resume)},
		   new Rectangle[]{new Rectangle(cam.viewportWidth/2-1.5f*BUTTON_SIZE-BUTTON_SPACING, BUTTON_Y, BUTTON_SIZE, BUTTON_SIZE),
						   new Rectangle(cam.viewportWidth/2-0.5f*BUTTON_SIZE,				  BUTTON_Y, BUTTON_SIZE, BUTTON_SIZE),
						   new Rectangle(cam.viewportWidth/2+0.5f*BUTTON_SIZE+BUTTON_SPACING, BUTTON_Y, BUTTON_SIZE, BUTTON_SIZE)});
	}
	
	@Override
	protected void onButtonTouch(int id) {
		state = STATE_DISAPPEAR;
		stateTime = 0;
		returnOnDisappear = (id == 0 ? GameScreen.MENU_QUIT : id == 1 ? GameScreen.MENU_RESTART : GameScreen.STATE_PLAYING);
	}

}
