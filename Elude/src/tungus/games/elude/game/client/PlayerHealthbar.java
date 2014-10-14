package tungus.games.elude.game.client;

import tungus.games.elude.Assets;
import tungus.games.elude.game.server.Vessel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class PlayerHealthbar {
	
	private static final float SPEED = 0.35f; // Part of whole per second
	private final Rectangle r;
	private float display;
	
	private final Vector2 textCoord;
	private final float textScale;
	
	public PlayerHealthbar(Rectangle r, float frustumWidth, float frustumHeight) {
		this.r = r;
		Gdx.app.log("HB", ""+r.height);
		display = 1;
		textScale = r.height;
		float camScale = 800 / frustumWidth;
		textCoord = new Vector2(r.x * camScale + 8*textScale, (r.y+r.height) * camScale - 5*textScale);
	}
	
	public void drawBar(SpriteBatch batch, float hp, float delta, float gameAlpha) {
		if (hp < display) {
			display = Math.max(display - SPEED * delta, hp);
		}
		if (display > 0) {
			batch.setColor(1-display, display, 0, 0.8f*gameAlpha);
			batch.draw(Assets.whiteRectangle, r.x, r.y, display * r.width, r.height);
			batch.setColor(1,1,1,gameAlpha);
		}
	}
	
	public void drawText(SpriteBatch fontBatch, float alpha) {
		Assets.font.setScale(textScale);
		Assets.font.setColor(1, 1, 1, alpha);
		Assets.font.draw(fontBatch, "HP: " + (int)(display*Vessel.MAX_HP), textCoord.x, textCoord.y);
		Assets.font.setScale(1);
	}
}
