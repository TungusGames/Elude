package tungus.games.elude.levels.levelselect;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class DetailsPanel {
	
	private final boolean finite;
	private ScoreDetails activeLevel = null;
	private ScoreDetails prevLevel = null;
	private final Sprite playButton;
	
	public DetailsPanel(boolean finiteLevels) {
		finite = finiteLevels;
		playButton = new Sprite();
	}
	
	public void render (SpriteBatch batch, boolean text) {
		
	}
	
	public void switchTo(int levelNum) {
		
	}

}
