package tungus.games.elude.game.client.worldrender;

import tungus.games.elude.Assets;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class EnemyHealthbar {
	
	private static final float BAR_LENGTH = 1f;
	private static final float BAR_WIDTH = 0.15f;
	private static final float DECREASE_SPEED = 1.5f; // Part of whole per sec
	private static final float APPEAR_TIME = 0.2f;
	private static final float DIST_FROM_ENEMY = 0.5f;
	private static final float MAX_ALPHA = 0.8f;
	
	private final Vector2 pos = new Vector2();
	private float display = 1;
	private float alpha = 0;
	private boolean fadeOut = false;
	private float fadeOutTime = 0;
	
	public void draw(SpriteBatch batch, float hp, float deltaTime, float x, float y) {
		pos.set(x, y);
		if (!fadeOut && alpha < MAX_ALPHA && display < 1) {
			alpha = Math.min(MAX_ALPHA, alpha + deltaTime / APPEAR_TIME * MAX_ALPHA);
		} else if (fadeOut) {
			alpha -= deltaTime / fadeOutTime * MAX_ALPHA;
		}
		if (hp < display) {
			display = Math.max(hp, display - deltaTime * DECREASE_SPEED);
		}
		float green = display * BAR_LENGTH;
		float red = BAR_LENGTH - green;
		float originalAlpha = batch.getColor().a;
		batch.setColor(0, 1, 0, alpha*originalAlpha);
		if (green > 0) {
			batch.draw(Assets.Tex.WHITE_RECTANGLE.t, pos.x - BAR_LENGTH / 2, pos.y + DIST_FROM_ENEMY, green, BAR_WIDTH);
		}
		batch.setColor(1, 0, 0, batch.getColor().a);
		if (red > 0) {
			batch.draw(Assets.Tex.WHITE_RECTANGLE.t, pos.x - BAR_LENGTH / 2 + green, pos.y + DIST_FROM_ENEMY, red, BAR_WIDTH);
		}
		batch.setColor(1, 1, 1, originalAlpha);
	}
	
	public boolean drawDead(SpriteBatch batch, float deltaTime) {
		if (!fadeOut) {
			fadeOut = true;
			fadeOutTime = display / DECREASE_SPEED;
		}
		draw(batch, 0, deltaTime, pos.x, pos.y);
		return display == 0;
	}
}
