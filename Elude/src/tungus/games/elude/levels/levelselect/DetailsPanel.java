package tungus.games.elude.levels.levelselect;

import tungus.games.elude.Assets;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;

public class DetailsPanel {

	private static final int STATE_IDLE = 0;
	private static final int STATE_SWITCH = 1;

	static final float SWITCH_TIME = 1.2f;

	private static final float PLAY_X = 13.75f;

	private final boolean finite;
	private ScoreDetails activeLevel = null;
	private ScoreDetails prevLevel = null;
	private final Sprite playButton;

	private int state = STATE_IDLE;
	private float stateTime = 0;
	private float time = 3;
	private float playFloatTime = 0;
	private boolean playLoaded = false;
	
	static Interpolation interp = Interpolation.exp5In;

	public DetailsPanel(boolean finiteLevels) {
		finite = finiteLevels;
		playButton = new Sprite(Assets.play);
		playButton.setBounds(PLAY_X+10, 1.5f, 3f, 1.8f);
	}
	
	public void render(float deltaTime, SpriteBatch batch, boolean text, float alpha) {
		stateTime += deltaTime;
		time += deltaTime;
		if (state == STATE_SWITCH) {
			if (!playLoaded) {
				playButton.setX(PLAY_X+interp.apply(1-playFloatTime/SWITCH_TIME)*10);
				playFloatTime += deltaTime;
			}
			if (playFloatTime > SWITCH_TIME) {
				playLoaded = true;
			}
			if (stateTime > SWITCH_TIME) {
				state = STATE_IDLE;
				stateTime = 0;
				prevLevel = null;
				playLoaded = true;
			}
		}
		if (!text) {
			setPlayColor(alpha);
			playButton.draw(batch);
		}

		if (activeLevel != null) {
			activeLevel.render(batch, text, state == STATE_SWITCH ? stateTime : SWITCH_TIME, alpha);
		}
		if (prevLevel != null) {
			prevLevel.render(batch, text, SWITCH_TIME, alpha*Math.max(0, 1-stateTime*2.5f));
		}
	}

	public void switchTo(int levelNum) {
		prevLevel = activeLevel;
		activeLevel = new ScoreDetails(levelNum, finite);
		state = STATE_SWITCH;
		stateTime = 0;
	}
	
	public boolean tapped(float x, float y) {
		return playLoaded && playButton.getBoundingRectangle().contains(x, y);
	}
	
	private float COLOR_CYCLE_TIME = 8f;
	private void setPlayColor(float alpha) {
		playButton.setColor(rgbComponent(time), rgbComponent(time+COLOR_CYCLE_TIME/3), rgbComponent(time-COLOR_CYCLE_TIME/3), alpha);
	}
	
	private float rgbComponent(float h) { // HSV to RGB for one component (the three components have to be offset by 120 degrees of hue)
		float f = h % COLOR_CYCLE_TIME / COLOR_CYCLE_TIME * 6;
		f = Math.abs(f-3);
		f = MathUtils.clamp(f-1, 0, 1);
		return f;
	}

}
