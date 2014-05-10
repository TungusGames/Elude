package tungus.games.elude.menu.levelselect;

import tungus.games.elude.levels.scoredata.ScoreData;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class GridPanel {
	
	public static final int STATE_IDLE = 0;
	public static final int STATE_SELECTIONSWITCH = 1;
	
	private static final int ROW_LEN = 5;
	private static final int ACTIVE_COL_LEN = 3;
	private static final Vector2 TOP_LEFT = new Vector2(2f,8);
	private static final float BUTTON_TOUCH_SIZE = 2;
	private static final float BUTTON_DIST = 2;
	private static final float BUTTON_DRAW_SIZE = 1.7f;
	private static final float SELECTED_DRAW_SIZE = 2f;
	private static final float SELECTED_MAX_ROT = 10;
	private static final float SELECTED_FADEOUT_SIZE = 15f;
	private static final float SELECTED_FADEOUT_ROT = 100f;
	private static final float SELECTIONSWITCH_TIME = 0.6f;
	private static final float ROW_SNAP_SPEED = 1.3f;
	private static final float FLING_DECEL = 10;
	
	private static final float ROT_CENTER_DIST = MathUtils.cosDeg(15) / MathUtils.sinDeg(15) / 2 * BUTTON_DIST; // Don't try to understand..	
	private static final float INACTIVE_SAT = 0.75f;
	private static final float INACTIVE_V = 0.25f;
	
	private static final Interpolation interp = Interpolation.pow3Out;
	
	private final int totalLevels;
	private final int totalRows;
	
	private final Rectangle[] buttonTouchAreas;
	private final Rectangle allButtons;
	private final LevelButton[] buttons;
	
	public int selected = -1;
	private int prevSelected = -1;
	private int lastOpenLevel;
	
	public float middleRow = 1f;
	private float touchedRow = -1;
	
	private final float[] rgba = new float[4];
	
	private float time = 0;
	private float stateTime = 0;
	
	public int state = STATE_IDLE;
	private boolean panning = false;
	private boolean flinging = false;
	private float flingSpeed = 0;
	
	private float selectedSize = SELECTED_DRAW_SIZE;
	private float selectedRot = SELECTED_MAX_ROT;
	
	
	public GridPanel(int levels, boolean finite) {
		rgba[3] = 1;
		totalLevels = levels;
		totalRows = levels / ROW_LEN;
		if (levels % ROW_LEN != 0)
			Gdx.app.log("LevelSelect", "Bad level count - not divisible by " + ROW_LEN);
		int visibleButtons = ROW_LEN * ACTIVE_COL_LEN;
		buttonTouchAreas = new Rectangle[visibleButtons];
		for (int i = 0; i < visibleButtons; i++) {
			buttonTouchAreas[i] = new Rectangle(i%ROW_LEN*BUTTON_DIST + TOP_LEFT.x - BUTTON_TOUCH_SIZE/2, 
												-i/ROW_LEN*BUTTON_DIST+TOP_LEFT.y  - BUTTON_TOUCH_SIZE/2, 
												BUTTON_TOUCH_SIZE, BUTTON_TOUCH_SIZE);
		}
		buttons = new LevelButton[totalLevels];
		allButtons = new Rectangle(TOP_LEFT.x-BUTTON_DIST/2, 0, ROW_LEN*BUTTON_DIST, 12);
		int openLeft = 3;
		for (int i = 0; i < buttons.length; i++) {
			boolean open = finite ? ScoreData.playerFiniteScore.get(i).completed : true;
			if (!open && openLeft > 0) {
				openLeft--;
				open = true;
				if (openLeft == 0)
					lastOpenLevel = i;
			}
			buttons[i] = new LevelButton(i, finite, open);
			buttons[i].setBounds(buttonTouchAreas[i%visibleButtons].x, buttonTouchAreas[i%visibleButtons].y, BUTTON_DRAW_SIZE, BUTTON_DRAW_SIZE);
			buttons[i].setOrigin(BUTTON_DRAW_SIZE/2, BUTTON_DRAW_SIZE/2);
		}
		if (openLeft > 0)
			lastOpenLevel = levels-1;
	}
	
	public boolean tapped(float x, float y) {
		int s = buttonTouchAreas.length;
		for (int i = 0; i < s; i++) {
			if (buttonTouchAreas[i].contains(x, y)) {
				int newSelected = i + (Math.round(middleRow)-1)*ROW_LEN;
				if (!buttons[newSelected].open || newSelected == selected) {
					return false;
				}
				prevSelected = selected;
				selected = newSelected;
				state = STATE_SELECTIONSWITCH;
				stateTime = 0;
				return true;
			}
		}
		return false;
	}
	
	public void calcTouchedRow(float x, float y) {
		if (allButtons.contains(x, y)) {
			float middleRowY = TOP_LEFT.y - BUTTON_DIST;
			float diff = Math.abs(middleRowY-y);
			if (diff <= BUTTON_DIST) {
				touchedRow = middleRow + (middleRowY-y)/BUTTON_DIST;
			} else {
				diff -= BUTTON_DIST;
				if (diff >= ROT_CENTER_DIST) {
					touchedRow = -1;
					return;
				}
				diff /= ROT_CENTER_DIST;
				float rot = (float)Math.asin(diff)*MathUtils.radiansToDegrees;
				float offset = rot / 30f;
				if (y > middleRowY)
					touchedRow = middleRow-1-offset;
				else
					touchedRow = middleRow+1+offset;
			}
		} else
			touchedRow = -1;
	}
	
	public void pan(float x, float y) {
		if (!panning) {
			panning = true;
			calcTouchedRow(x, y);
		}
		if (touchedRow == -1)
			return;
		float middleRowY = TOP_LEFT.y - BUTTON_DIST;
		float diff = Math.abs(middleRowY-y);
		if (diff <= BUTTON_DIST) {
			middleRow = touchedRow + (y-middleRowY)/BUTTON_DIST;
		} else {
			diff -= BUTTON_DIST;
			if (diff >= ROT_CENTER_DIST)
				return;
			diff /= ROT_CENTER_DIST;
			float rot = (float)Math.asin(diff)*MathUtils.radiansToDegrees;
			float offset = rot / 30f;
			if (y > middleRowY)
				middleRow = touchedRow+offset+1;
			else
				middleRow = touchedRow-offset-1;
		}
		middleRow = MathUtils.clamp(middleRow, 1, totalRows-2);
	}
	
	public void panStop(float x, float y) {
		panning = false;
		if (allButtons.contains(x, y))
			flinging = true;
	}
	
	public void scroll(int amount) {
		middleRow += amount*0.3f;
		middleRow = MathUtils.clamp(middleRow, 1, totalRows-2);
	}
	
	public void fling(float velY) {
		if (flinging) {
			flingSpeed = velY/4;
			Gdx.app.log("fling", ""+velY);
		}
	}
	
	public void stopFling() {
		flinging = false;
		flingSpeed = 0;
	}
	
	public void render(SpriteBatch uiBatch, float deltaTime, boolean text) {
		time += deltaTime;
		stateTime += deltaTime;
		if (state == STATE_SELECTIONSWITCH && stateTime > SELECTIONSWITCH_TIME) {
			state = STATE_IDLE;
			stateTime = 0;
			prevSelected = -1;
		}
		
		if (middleRow % 1 != 0 && !panning && !flinging) {
			int goal = Math.round(middleRow);
			if (Math.abs(goal-middleRow) < deltaTime*ROW_SNAP_SPEED) {
				middleRow = goal;
			} else {
				middleRow += deltaTime*ROW_SNAP_SPEED* (goal > middleRow ? 1 : -1);
			}
		}
		
		if (flinging) {
			if (deltaTime*FLING_DECEL > Math.abs(flingSpeed)) {
				flingSpeed = 0;
				flinging = false;
			} else {
				flingSpeed -= deltaTime*FLING_DECEL*Math.signum(flingSpeed);
			}
			middleRow += flingSpeed*deltaTime;
			middleRow = MathUtils.clamp(middleRow, 1, totalRows-2);
		}
		
		for (int i = 0; i < totalRows; i++) {
			float distanceFromMiddle = Math.abs(middleRow-i); 
			if (distanceFromMiddle > (ACTIVE_COL_LEN-1)/2+3) 		// Row out of view
				continue;
			else if (distanceFromMiddle <= (ACTIVE_COL_LEN-1)/2) 	// Row in the active area, not rotated
				drawRow(TOP_LEFT.y-(i-middleRow+1)*BUTTON_DIST, 1, i*ROW_LEN, 1, 1, uiBatch, text);
			else {				 				// Row rotating/rotated on the edge
				float degrees = (distanceFromMiddle-1)*30; // Degrees rotated: between 0 (facing us) and 90 (out of view)
				float scaleY = MathUtils.cosDeg(degrees);
				float offset = MathUtils.sinDeg(degrees)*ROT_CENTER_DIST;
				float posY = (i < middleRow) ? 
						TOP_LEFT.y+offset :										// Above the top row 
						TOP_LEFT.y - (ACTIVE_COL_LEN-1)*BUTTON_DIST - offset;	// Below the bottom row
				float s = Math.max(INACTIVE_SAT, 1-degrees/30*(1-INACTIVE_SAT));
				float v = Math.max(INACTIVE_V, 1-degrees/30*(1-INACTIVE_V));
				drawRow(posY, scaleY, i*ROW_LEN, s, v, uiBatch, text);
			}
		}
	}
	
	public void renderLoading(SpriteBatch uiBatch, float deltaTime, boolean text, float ready) {
		float goalRow = Math.max(lastOpenLevel/5-1, 1);
		renderLoading(uiBatch, deltaTime, text, ready, goalRow);
	}
	
	public void renderLoading(SpriteBatch uiBatch, float deltaTime, boolean text, float ready, float goalRow) {
		middleRow = interp.apply(totalRows+3, goalRow, ready);
		render(uiBatch, deltaTime, text);
	}
	
	public void renderEnding(SpriteBatch uiBatch, float deltaTime, boolean text, float ready) {
		rgba[3] = 1-ready;
		selectedSize = SELECTED_DRAW_SIZE + ready * (SELECTED_FADEOUT_SIZE-SELECTED_DRAW_SIZE);
		selectedRot  = SELECTED_MAX_ROT   + ready * (SELECTED_FADEOUT_ROT -SELECTED_DRAW_SIZE);
		buttons[selected].freezeCompositeScale = true;
		render(uiBatch, deltaTime, text);
	}
	
	private void drawRow(float yPos, float scaleY, int level, float s, float v, SpriteBatch batcher, boolean text) {
		for (int i = 0; i < ROW_LEN; i++) {
			LevelButton button = buttons[level];
			button.setPosition(button.getX(), yPos-BUTTON_DRAW_SIZE/2);
			button.setScale(1, scaleY);
			button.setAlpha(v*rgba[3]);
			if (level == selected) {
				if (state != STATE_SELECTIONSWITCH) {
					button.setScale(button.getScaleX()*selectedSize/BUTTON_DRAW_SIZE, button.getScaleY()*selectedSize/BUTTON_DRAW_SIZE);
					button.setRotation(MathUtils.sin(time)*selectedRot);
				} else {
					float complete = stateTime / SELECTIONSWITCH_TIME;
					float size = BUTTON_DRAW_SIZE + complete*(selectedSize-BUTTON_DRAW_SIZE);
					button.setScale(button.getScaleX()*size/BUTTON_DRAW_SIZE, button.getScaleY()*size/BUTTON_DRAW_SIZE);
					button.setRotation(MathUtils.sin(time)*selectedRot * complete);
				}
			} else if (level == prevSelected) {
				float complete = 1 - stateTime / SELECTIONSWITCH_TIME;
				float size = BUTTON_DRAW_SIZE + complete*(selectedSize-BUTTON_DRAW_SIZE);
				button.setScale(button.getScaleX()*size/BUTTON_DRAW_SIZE, button.getScaleY()*size/BUTTON_DRAW_SIZE);
				button.setRotation(MathUtils.sin(time)*selectedRot * complete);
			} else {
				button.setRotation(0);
			}
			button.draw(batcher, text);
			level++;
		}
		
	}
	
	/*private static final float COLOR_CYCLE_TIME = 8f;	
	private float[] setColor(float i, float s, float v) {
		float f = time + i/2;
		rgba[0] = rgbComponent(f, s, v);
		rgba[1] = rgbComponent(f + COLOR_CYCLE_TIME/3, s, v);
		rgba[2] = rgbComponent(f - COLOR_CYCLE_TIME/3, s, v);
		
		return rgba;
	}
	
	private float rgbComponent(float h, float s, float v) { // HSV to RGB for one component (the three components have to be offset by 120 degrees of hue)
		float f = h % COLOR_CYCLE_TIME / COLOR_CYCLE_TIME * 6;
		f = Math.abs(f-3);
		f = MathUtils.clamp(f-1, 0, 1);
		f *= v;
		f += v*(1-s);
		return f;
	}*/

}
