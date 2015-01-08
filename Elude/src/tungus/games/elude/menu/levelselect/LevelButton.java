package tungus.games.elude.menu.levelselect;

import tungus.games.elude.Assets;
import tungus.games.elude.levels.scoredata.ScoreData;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class LevelButton extends Sprite {
	
	private static final float LOCK_HEIGHT = 1f;
	private static final float LOCK_WIDTH = 0.7f;
	
	private final int id;
	private final Sprite[] stars;
	private final Sprite lock;
	private Color col;
	
	private static TextureRegion getFrame(int n, boolean isFinite, boolean open) {
		if (!open)
			return Assets.Tex.FRAME_RED.t;
		if (!(isFinite ? ScoreData.playerFiniteScore.get(n).completed : ScoreData.playerArcadeScore.get(n).tried))
			return Assets.Tex.FRAME_BLUE.t;
		//if (ScoreData.hasMedal(isFinite, false, n) && ScoreData.hasMedal(isFinite, true, n))
		//	return Assets.frameYellow;
		return Assets.Tex.FRAME_GREEN.t;
	}
	
	void setupColor(TextureRegion tex) {
		if (tex == Assets.Tex.FRAME_RED.t)
			col = new Color(1f, 0.3f, 0.1f, 1);
		else if (tex == Assets.Tex.FRAME_BLUE.t)
			col = new Color(0.2f, 0.7f, 1f, 1);
		else if (tex == Assets.Tex.FRAME_GREEN.t)
			col = new Color(0.3f, 1f, 0.1f, 1);
		else
			col = new Color(1f, 1f, 0.1f, 1);
	}
	
	public final boolean open;
	public boolean freezeCompositeScale = false;
	
	float numYPos;
	float numYScale;

	public LevelButton(int n, boolean finite, boolean open) {
		super(getFrame(n, finite, open));
		setupColor(getFrame(n, finite, open));
		id = n;
		this.open = open;
		
		if (open) {
			stars = new Sprite[finite ? 3 : 2];
			for (int i = 0; i < stars.length; i++) {
				if (finite && i == 0)
					stars[i] = new Sprite(ScoreData.playerFiniteScore.get(n).completed ? Assets.Tex.STAR_ON.t : Assets.Tex.STAR_OFF.t);
				else
					stars[i] = new Sprite(ScoreData.hasMedal(finite, ((finite ? i-1 : i) % 2 == 0), n) ? Assets.Tex.STAR_ON.t : Assets.Tex.STAR_OFF.t);
				stars[i].setSize(0.36f, 0.342f);
				stars[i].setOrigin(stars[i].getWidth()/2, stars[i].getHeight()/2+0.43f);
			}
			lock = null;
		} else {
			stars = null;
			lock = new Sprite(Assets.Tex.LOCK.t);
			lock.setSize(LOCK_WIDTH, LOCK_HEIGHT);
			lock.setOrigin(LOCK_WIDTH/2, LOCK_HEIGHT/2);
			lock.setColor(col);
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
			Assets.font.setColor(col);
			Assets.font.drawMultiLine(batch, ""+(id+1), xPos*40, numYPos*40);
		}
	}
	
	public void setAlpha(float a) {
		if (stars != null) {
			for (int i = 0; i < stars.length; i++) {
				stars[i].setColor(1, 1, 1, a);
			}
		}
		col.a = a;
		if (lock != null) {
			lock.setColor(col);
		}
		setColor(1, 1, 1, a);
	}
}
