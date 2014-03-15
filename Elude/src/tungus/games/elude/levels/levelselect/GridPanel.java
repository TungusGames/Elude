package tungus.games.elude.levels.levelselect;

import tungus.games.elude.Assets;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class GridPanel {
	
	public static final int STATE_IDLE = 0;
	public static final int STATE_SELECTIONSWITCH = 1;
	
	private static final int ROW_LEN = 5;
	private static final int COL_LEN = 3;
	private static final Vector2 TOP_LEFT = new Vector2(2,7);
	private static final float BUTTON_TOUCH_SIZE = 2;
	private static final float BUTTON_DRAW_SIZE = 1.7f;
	private static final float SELECTED_DRAW_SIZE = 2f;
	private static final float SELECTED_MAX_ROT = 10;
	
	private static final float SELECTIONSWITCH_TIME = 1.5f;
	
	private final Rectangle[] buttons;
	private final Sprite[] buttonSprites;
	
	private int selected = -1;
	private int prevSelected = -1;
	
	private float rowOffset = 0;
	
	private final float[] rgba = new float[4];
	
	private float time = 0;
	private float stateTime = 0;
	
	public int state = STATE_IDLE;
	
	public GridPanel() {
		buttons = new Rectangle[ROW_LEN * COL_LEN];
		for (int i = 0; i < buttons.length; i++) {
			buttons[i] = new Rectangle(i%5*2 + TOP_LEFT.x, -i/5*2+TOP_LEFT.y, BUTTON_TOUCH_SIZE, BUTTON_TOUCH_SIZE);
		}
		buttonSprites = new Sprite[ROW_LEN * COL_LEN];
		for (int i = 0; i < buttonSprites.length; i++) {
			buttonSprites[i] = new Sprite(Assets.frame);
			buttonSprites[i].setBounds(buttons[i].x, buttons[i].y, BUTTON_DRAW_SIZE, BUTTON_DRAW_SIZE);
			buttonSprites[i].setOrigin(BUTTON_DRAW_SIZE/2, BUTTON_DRAW_SIZE/2);
		}
	}
	
	public void tapped(float x, float y) {
		int s = buttons.length;
		for (int i = 0; i < s; i++) {
			if (buttons[i].contains(x, y)) {
				prevSelected = selected;
				selected = i;
				state = STATE_SELECTIONSWITCH;
				stateTime = 0;
				break;
			}
		}
	}
	
	public void render(SpriteBatch batcher, float deltaTime) {
		time += deltaTime;
		stateTime += deltaTime;
		if (state == STATE_SELECTIONSWITCH && stateTime > SELECTIONSWITCH_TIME) {
			state = STATE_IDLE;
			stateTime = 0;
			prevSelected = -1;
		}
		
		for (int i = 0; i < ROW_LEN; i++) {
			for (int j = 0; j < COL_LEN; j++) {
				setColor(i+j, 1);
				int n = j*ROW_LEN + i;
				Sprite sprite = buttonSprites[n];
				sprite.setColor(rgba[0], rgba[1], rgba[2], rgba[3]);
				if (n == selected) {
					if (state != STATE_SELECTIONSWITCH) {
						sprite.setScale(SELECTED_DRAW_SIZE/BUTTON_DRAW_SIZE);
						sprite.setRotation(MathUtils.sin(time)*SELECTED_MAX_ROT);
					} else {
						float complete = stateTime / SELECTIONSWITCH_TIME;
						float size = BUTTON_DRAW_SIZE + complete*(SELECTED_DRAW_SIZE-BUTTON_DRAW_SIZE);
						sprite.setScale(size/BUTTON_DRAW_SIZE);
						sprite.setRotation(MathUtils.sin(time)*SELECTED_MAX_ROT * complete);
					}
				} else if (n == prevSelected) {
					float complete = 1 - stateTime / SELECTIONSWITCH_TIME;
					float size = BUTTON_DRAW_SIZE + complete*(SELECTED_DRAW_SIZE-BUTTON_DRAW_SIZE);
					sprite.setScale(size/BUTTON_DRAW_SIZE);
					sprite.setRotation(MathUtils.sin(time)*SELECTED_MAX_ROT * complete);
				}
				sprite.draw(batcher);
			}
		}
	}
	
	private static final float COLOR_CYCLE_TIME = 8f;	
	private float[] setColor(float i, float a) {
		float f = time + i/2;
		rgba[0] = rgbComponent(f);
		rgba[1] = rgbComponent(f + COLOR_CYCLE_TIME/3);
		rgba[2] = rgbComponent(f - COLOR_CYCLE_TIME/3);
		rgba[3] = a;
		
		return rgba;
	}
	
	private float rgbComponent(float f) {
		f = f % COLOR_CYCLE_TIME / COLOR_CYCLE_TIME * 6;
		f = Math.abs(f-3);
		f = MathUtils.clamp(f-1, 0, 1);
		return f;
	}

}
