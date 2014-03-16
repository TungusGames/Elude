package tungus.games.elude.levels.levelselect;

import tungus.games.elude.Assets;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class LevelButton extends Sprite {

	private final int id;
	
	public LevelButton(int n, boolean finite) {
		super(Assets.frame);
		id = n;
	}
	
	public void draw(SpriteBatch uiBatch, boolean text) {
		if (!text)
			super.draw(uiBatch);
		else {
			float xPos = getX()+getWidth()/3;	// All magic numbers derived from experimentation
			if (id == 0)
				xPos += getWidth()*0.08f;
			else if (id == 10)
				xPos -= getWidth()*0.04f;
			else if ((id+1)%10==1 || (id+1)/10 == 1)
				xPos -= getWidth()*0.1f;
			else if (id >= 19) {
				xPos -= getWidth()*0.18f;
			}
			Assets.font.setScale(1, getScaleY());
			float yPos = getY()+getHeight()/5*4;
			yPos -= (1-getScaleY())*getHeight()*0.3f;
			Assets.font.setColor(getColor());
			Assets.font.drawMultiLine(uiBatch, ""+(id+1), xPos*40, yPos*40);
		}
			
	}
}
