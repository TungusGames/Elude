package tungus.games.elude.levels.levelselect;

import tungus.games.elude.Assets;
import tungus.games.elude.levels.scoredata.ScoreData;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class LevelButton extends Sprite {
	
	private static final float LOCK_HEIGHT = 1f;
	private static final float LOCK_WIDTH = 0.7f;
	
	private final int id;
	private final Sprite[] stars;
	private final Sprite lock;
	
	public final boolean open;
	public boolean freezeCompositeScale = false;
	
	float numYPos;
	float numYScale;

	public LevelButton(int n, boolean finite, boolean open) {
		super(Assets.frame);
		id = n;
		this.open = open;
		
		if (open) {
			stars = new Sprite[finite ? 3 : 2];
			for (int i = 0; i < stars.length; i++) {
				if (finite && i == 0)
					stars[i] = new Sprite(ScoreData.playerFiniteScore.get(n).completed ? Assets.stars[3] : Assets.stars[0]);
				else
					stars[i] = new Sprite(ScoreData.hasMedal(finite, ((finite ? i-1 : i) % 2 == 0), n) ? Assets.stars[3] : Assets.stars[0]);
				stars[i].setSize(0.36f, 0.342f);
				stars[i].setOrigin(stars[i].getWidth()/2, stars[i].getHeight()/2+0.43f);
			}
			lock = null;
		} else {
			stars = null;
			lock = new Sprite(Assets.lock);
			lock.setSize(LOCK_WIDTH, LOCK_HEIGHT);
			lock.setOrigin(LOCK_WIDTH/2, LOCK_HEIGHT/2);
		}
		
		
	}

	public void draw(SpriteBatch batch, boolean text) {
		if (!text) {
			super.draw(batch);
			if (open) {
				for (int i = 0; i < stars.length; i++) {
					stars[i].setPosition(getX()+0.25f+(i+(stars.length == 2 ? 0.5f : 0))*((getWidth()-2*0.25f)/3), getY()+0.175f*getHeight());
					if (!freezeCompositeScale) stars[i].setScale(1, getScaleY());
					stars[i].draw(batch);
				}
			} else {
				lock.setColor(getColor());
				lock.setScale(1, getScaleY());
				float y = getY()+(getHeight()-LOCK_HEIGHT)/2;
				y -= (1-getScaleY())*0.03f;
				lock.setPosition(getX()+(getWidth()-LOCK_WIDTH)/2, y);
				
				lock.draw(batch);
				
			}
		} else if (open) {
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
			if (!freezeCompositeScale) {
				numYScale = getScaleY();
				numYPos = getY()+getHeight()/5*4;
				numYPos -= (1-getScaleY())*getHeight()*0.3f;
			}
			Assets.font.setScale(1, numYScale);
			Assets.font.setColor(getColor());
			Assets.font.drawMultiLine(batch, ""+(id+1), xPos*40, numYPos*40);
		}
	}
	
	public void setStarAlpha(float a) {
		if (stars == null)
			return;
		for (int i = 0; i < stars.length; i++) {
			stars[i].setColor(1, 1, 1, a);
		}
	}
}
