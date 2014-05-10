package tungus.games.elude.menu.levelselect;

import tungus.games.elude.Assets;
import tungus.games.elude.levels.scoredata.ScoreData;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;

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
	private float playFloatTime = 0;
	private boolean playLoaded = false;
	
	static Interpolation interp = Interpolation.exp5In;

	public DetailsPanel(boolean finiteLevels) {
		finite = finiteLevels;
		playButton = new Sprite(Assets.play);
		playButton.setBounds(PLAY_X+10, 0.5f, 3f, 1.8f);
	}
	
	public void render(float deltaTime, SpriteBatch batch, boolean text, float alpha) {
		stateTime += deltaTime;
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
			playButton.draw(batch);
		}

		if (activeLevel != null) {
			activeLevel.render(batch, text, state == STATE_SWITCH ? stateTime/SWITCH_TIME : 1, alpha);
		}
		if (prevLevel != null) {
			prevLevel.render(batch, text, 1, alpha*Math.max(0, 1-stateTime*2.5f));
		}
	}

	public void switchTo(int levelNum) {
		prevLevel = activeLevel;
		activeLevel = finite ?
				new ScoreDetails("LEVEL " + (levelNum+1), levelNum, 12.5f, 1, false, 10f, ScoreData.playerFiniteScore.get(levelNum)) :
				new ScoreDetails("LEVEL " + (levelNum+1), levelNum, 12.5f, 1, false, 10f, ScoreData.playerArcadeScore.get(levelNum));
		state = STATE_SWITCH;
		stateTime = 0;
	}
	
	public boolean tapped(float x, float y) {
		return playLoaded && playButton.getBoundingRectangle().contains(x, y);
	}
}
