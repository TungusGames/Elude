package tungus.games.elude.menu.ingame;

import tungus.games.elude.Assets;
import tungus.games.elude.game.GameScreen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public abstract class AbstractIngameMenu {
	
	protected static final int STATE_APPEAR = 0;
	protected static final int STATE_IDLE = 1;
	protected static final int STATE_DISAPPEAR = 2;
	
	private static final float APPEAR_TIME = 0.5f;
	private static final float DISAPPEAR_TIME = 0.5f;
	
	private static final Vector2 APPEAR_ORIGIN = new Vector2(400f, 360f);
	private static final Interpolation SIZE = Interpolation.fade;
	private static final Interpolation POSITION = Interpolation.fade;
	private static final Interpolation OPACITY = Interpolation.fade;
	private static final Interpolation SHADOW = Interpolation.fade;
	
	protected static final OrthographicCamera cam = new OrthographicCamera(800, 480);
	protected static final SpriteBatch batch = new SpriteBatch();
	{
		cam.position.set(cam.viewportWidth/2, cam.viewportHeight/2, 0);
		cam.update();
		batch.setProjectionMatrix(cam.combined);
	}
	
	protected Sprite[] buttonSprites;
	protected Rectangle[] goalPositions;
	protected int state = STATE_APPEAR;
	protected float stateTime = 0;
	
	protected int returnOnDisappear = GameScreen.STATE_PLAYING;
	
	protected AbstractIngameMenu(Sprite[] buttonSprites, Rectangle[] goalPositions) {
		this.buttonSprites = buttonSprites;
		this.goalPositions = goalPositions;
	}
	
	public int update(float deltaTime, Vector3 touch) {
		stateTime += deltaTime;
		if (state == STATE_APPEAR) {
			if (stateTime > APPEAR_TIME) {
				stateTime = 0;
				state = STATE_IDLE;
				int s = buttonSprites.length;
				for (int i = 0; i < s; i++) {
					buttonSprites[i].setBounds(goalPositions[i].x, goalPositions[i].y, goalPositions[i].width, goalPositions[i].height);
				}
			} else {
				int s = buttonSprites.length;
				float a = stateTime / APPEAR_TIME;
				for (int i = 0; i < s; i++) {
					buttonSprites[i].setBounds(POSITION.apply(APPEAR_ORIGIN.x, goalPositions[i].x, a), 
											   POSITION.apply(APPEAR_ORIGIN.y, goalPositions[i].y, a), 
											   SIZE.apply(0, goalPositions[i].width, a), SIZE.apply(0, goalPositions[i].height, a));
					Color c = buttonSprites[i].getColor();
					c.a = OPACITY.apply(a);
					buttonSprites[i].setColor(c);
				}
			}
		} else if (state == STATE_DISAPPEAR) {
			if (stateTime > DISAPPEAR_TIME) {
				stateTime = 0;
				state = STATE_APPEAR;
				return returnOnDisappear;
			}
			return GameScreen.MENU_NOCHANGE;
		} else if (touch != null) {
			cam.unproject(touch);
			int s = buttonSprites.length;
			for (int i = 0; i < s; i++) {
				if (buttonSprites[i].getBoundingRectangle().contains(touch.x, touch.y)) {
					onButtonTouch(i);
					break;
				}
			}
		}
		return GameScreen.MENU_NOCHANGE;
	}
	
	public void render() {
		batch.begin();
		float shadow = (state == STATE_APPEAR) ? SHADOW.apply(stateTime / APPEAR_TIME) :
					   (state == STATE_DISAPPEAR) ? SHADOW.apply(1-stateTime/DISAPPEAR_TIME) : 1;
		batch.setColor(1, 1, 1, shadow*0.8f);
		batch.draw(Assets.shadower, 0, 0, cam.viewportWidth, cam.viewportHeight);
		batch.setColor(1, 1, 1, state == STATE_DISAPPEAR ? SHADOW.apply(1-stateTime/DISAPPEAR_TIME) : 1);
		int s = buttonSprites.length;
		for (int i = 0; i < s; i++) {
			buttonSprites[i].draw(batch, shadow);
		}
		batch.end();
	}
	
	protected abstract void onButtonTouch(int id);

}
