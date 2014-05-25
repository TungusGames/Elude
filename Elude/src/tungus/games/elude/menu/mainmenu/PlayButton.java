package tungus.games.elude.menu.mainmenu;

import tungus.games.elude.Assets;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PlayButton extends Sprite {
	
	public static final int RETURN_FINITE_LEVELS = 1;
	public static final int RETURN_ARCADE_LEVELS = 2;
	
	private final float SPLIT_TIME = 0.3f;
	private float stateTime = 0;
	private boolean split = false;
	private boolean switching = false;
	
	private final Sprite upper = new Sprite(Assets.halfPlayPanel);
	private final Sprite lower = new Sprite(Assets.halfPlayPanel);
	
	public PlayButton() {
		super(Assets.playSingleButton);
		//		playButton.setBounds(175, 35, 220, 220);
		upper.setBounds(175, 150, 220, 105);
		lower.setBounds(175, 35, 220, 105);
	}
	
	public void render(SpriteBatch batch, float alpha) {
		float nonSplitAlpha = switching ? 
				(split ? stateTime / SPLIT_TIME : 1-stateTime / SPLIT_TIME) : 
				(split ? 0 : 1);
		float splitAlpha = 1-nonSplitAlpha;
		nonSplitAlpha *= alpha;
		splitAlpha *= alpha;
		super.draw(batch, nonSplitAlpha);
		upper.draw(batch, splitAlpha);
		lower.draw(batch, splitAlpha);
		Assets.font.setColor(0.5f,1f,0.5f,splitAlpha);
		Assets.font.setScale(1);
		Assets.font.draw(batch, "STAGES", 215, 215);
		Assets.font.draw(batch, Assets.Strings.endless, 204, 100);
	}
	
	public void update(float deltaTime) {
		stateTime += deltaTime;
		if (switching && stateTime > SPLIT_TIME) {
			split = !split;
			stateTime = 0;
			switching = false;
		}
	}
	
	public int touchAt(float x, float y) {
		if (!split) {
			switching = true;
			stateTime = 0;
		} else {
			if (upper.getBoundingRectangle().contains(x, y))
				return RETURN_FINITE_LEVELS;
			else if (lower.getBoundingRectangle().contains(x, y))
				return RETURN_ARCADE_LEVELS;
		}
		return -1;
	}
	
	public void unsplit() {
		if (split) {
			switching = true;
			stateTime = 0;
		}
	}
}
