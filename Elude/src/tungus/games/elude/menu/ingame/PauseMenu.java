package tungus.games.elude.menu.ingame;

import tungus.games.elude.Assets;
import tungus.games.elude.game.client.GameScreen;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

public class PauseMenu extends AbstractIngameMenu {
	
	private static final float BUTTON_SIZE = 120;
	private static final float BUTTON_SPACING = 60;
	private static final float BUTTON_Y = 240-BUTTON_SIZE/2;
	private static final String TEXT = "PAUSED";
	private static final float TEXT_X = 400-Assets.font.getBounds(TEXT).width/2;
	private static final float TEXT_Y = BUTTON_Y + BUTTON_SIZE + BUTTON_SPACING;
	
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
		returnOnDisappear = id == 0 ? GameScreen.MENU_QUIT : id == 1 ? GameScreen.MENU_RESTART : GameScreen.STATE_PLAYING;
		fadeGameOut = !(returnOnDisappear == GameScreen.STATE_PLAYING);
	}
	
	//To be called when unpaused from keyboard
	public void unPause() {
		if (state == STATE_IDLE)
			onButtonTouch(2);
	}
	
	@Override
	public int render() {
		int r = super.render();
		batch.begin();
		float a = state == STATE_APPEAR ? stateTime / APPEAR_TIME :
				  state == STATE_DISAPPEAR ? 1-stateTime / DISAPPEAR_TIME :
				  1;
		Assets.font.setColor(1, 1, 1, OPACITY.apply(a));
		Assets.font.draw(batch, TEXT, TEXT_X, TEXT_Y);
		batch.end();
		return r;
	}
}
