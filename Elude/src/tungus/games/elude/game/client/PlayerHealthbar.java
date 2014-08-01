package tungus.games.elude.game.client;

import tungus.games.elude.Assets;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class PlayerHealthbar {
	
	private static final float SPEED = 0.35f; // Part of whole per second
	private final Rectangle r;
	private float display;
	
	public PlayerHealthbar(Rectangle r) {
		this.r = r;
		display = 1;
	}
	
	public void draw(SpriteBatch batch, float hp, float delta, float gameAlpha) {
		if (hp < display) {
			display = Math.max(display - SPEED * delta, hp);
		}
		if (display > 0) {
			batch.setColor(1-display, display, 0, 0.8f*gameAlpha);
			batch.draw(Assets.whiteRectangle, r.x, r.y, display * r.width, r.height);
			batch.setColor(1,1,1,gameAlpha);
		}
	}
}
