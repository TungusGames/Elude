package tungus.games.elude.game.client;

import tungus.games.elude.Assets;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class LevelProgressbar {
	private static final float SPEED = 0.1f; // Part of whole per second
	private final Rectangle r;
	private float display;
	
	private final Vector2 textCoord;
	private final float textScale;
	
	public LevelProgressbar(Rectangle r, float frustumWidth, float frustumHeight) {
		this.r = r;
		display = 0;
		textScale = r.height;
		float camScale = 800 / frustumWidth;
		textCoord = new Vector2(r.x * camScale + 8*textScale, (r.y+r.height) * camScale - 5*textScale);
	}
	
	public void drawBar(SpriteBatch batch, float pr, float delta, float gameAlpha) {
		if (pr > display) {
			display = Math.min(display + SPEED * delta, pr);
		}
		if (display > 0) {
			batch.setColor(0.1f, 0.6f, 1f, 0.8f*gameAlpha);
			batch.draw(Assets.whiteRectangle, r.x, r.y, display * r.width, r.height);
		}
		if (display < 1) {
			batch.setColor(0.1f, 0.1f, 0.5f, 0.8f*gameAlpha);
			batch.draw(Assets.whiteRectangle, r.x + display * r.width, r.y, (1-display) * r.width, r.height);
		}
		batch.setColor(1,1,1,gameAlpha);
	}
	
	public void drawText(SpriteBatch fontBatch, float alpha) {
		Assets.font.setScale(textScale);
		Assets.font.setColor(1, 1, 1, alpha);
		Assets.font.draw(fontBatch, "PROGRESS: " + (int)(display*100) + "%", textCoord.x, textCoord.y);
		Assets.font.setScale(1);
	}
}
