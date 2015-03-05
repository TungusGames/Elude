package tungus.games.elude.menu.levelselect;

import tungus.games.elude.Assets;
import tungus.games.elude.levels.scoredata.ScoreData;

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
	private float playFloatTime = 0;
	private boolean playLoaded = false;
	private boolean prevOpen = false;
	private boolean open = true;
	
	static Interpolation interp = Interpolation.exp5In;

	public DetailsPanel(boolean finiteLevels) {
		finite = finiteLevels;
		playButton = new Sprite(Assets.Tex.PLAY_LEVEL.t);
		playButton.setBounds(PLAY_X+10, 0.5f, 3f, 1.8f);
	}
	
	public void render(float deltaTime, SpriteBatch batch, boolean text, float alpha) {
		stateTime += deltaTime;
		float playAlpha = open ? 1 : 0;
		if (state == STATE_SWITCH) {
			if (!playLoaded) {
				playButton.setX(PLAY_X+interp.apply(1-playFloatTime/SWITCH_TIME)*10);
				playFloatTime += deltaTime;
			} else {
				float x = MathUtils.clamp(stateTime / SWITCH_TIME, 0, 1);
				playAlpha = prevOpen && open ? 1 :
							!prevOpen && !open ? 0 :
							prevOpen && !open ? 1-x :
												x;
			}
			if (playFloatTime > SWITCH_TIME && open) {
				playLoaded = true;
			}
			if (stateTime > SWITCH_TIME) {
				state = STATE_IDLE;
				stateTime = 0;
				prevLevel = null;
				playLoaded = true;
				playButton.setX(PLAY_X);
			}
		}
		if (!text) {
			playButton.setColor(1, 1, 1, alpha*playAlpha);
			playButton.draw(batch);
		}

		if (activeLevel != null) {
			activeLevel.render(batch, text, state == STATE_SWITCH ? stateTime/SWITCH_TIME : 1, alpha);
		}
		if (prevLevel != null) {
			prevLevel.render(batch, text, 1, alpha*Math.max(0, 1-stateTime*2.5f));
		}
	}

	public void switchTo(int levelNum, boolean open) {
		prevLevel = activeLevel;
		activeLevel = finite ?
				new ScoreDetails("STRAIGHTFORWARD KILLERS", levelNum, 12.5f, 420, 1, false, 10f, ScoreData.playerFiniteScore.get(levelNum), false, open) :
				new ScoreDetails(Assets.Strings.endless + " " + (levelNum+1), levelNum, 12.5f, 420, 1, false, 10f, 
								ScoreData.playerArcadeScore.get(levelNum), false, open);
		state = STATE_SWITCH;
		prevOpen = this.open;
		this.open = open;
		stateTime = 0;
	}
	
	public boolean tapped(float x, float y) {
		return open && playLoaded && playButton.getBoundingRectangle().contains(x, y);
	}
}
