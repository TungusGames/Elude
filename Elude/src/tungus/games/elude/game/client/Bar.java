package tungus.games.elude.game.client;

import tungus.games.elude.Assets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Bar {
	
	private static final float SPEED = 0.35f; // Part of whole per second
	private final Rectangle r;
	private float displayedValue;
	
	private final Vector2 textCoord;
	private final float textScaleY;
	private final float textScaleX;
	
	private final Color empty;
	private final Color full;
	private final Color bg;
	
	private final Color temp = new Color();
	
	private final String prefix;
	private final String postfix;
	private final float maxValue;
	
	public Bar(Rectangle r, float frustumWidth, float frustumHeight, float start, 
						   Color empty, Color full, Color bg, String prefix, String postfix, float max) {
		this.r = r;
		displayedValue = start;
		textScaleY = 0.417f / 0.033f * r.height / frustumHeight;
		textScaleX = textScaleY * (800f/480) / (frustumWidth/frustumHeight);
		float camScaleY = 480 / frustumHeight;
		float camScaleX = 800 / frustumWidth;
		textCoord = new Vector2((r.x+r.height*0.15f) * camScaleX, (r.y+r.height*0.85f) * camScaleY);
		this.empty = empty;
		this.full = full;
		this.bg = bg;
		this.prefix = prefix;
		this.postfix = postfix;
		this.maxValue = max;
	}
	
	public void drawBar(SpriteBatch batch, float value, float delta, float gameAlpha) {
		if (value < displayedValue) {
			displayedValue = Math.max(displayedValue - SPEED * delta, value);
		} else if (value > displayedValue) {
			displayedValue = Math.min(displayedValue + SPEED * delta, value);
		}
		
		if (displayedValue > 0) {
			batch.setColor(lerp(empty, full, displayedValue, 0.8f*gameAlpha));
			batch.draw(Assets.Tex.WHITE_RECTANGLE.t, r.x, r.y, displayedValue * r.width, r.height);
		}
		if (displayedValue < 1 && bg != null) {
			bg.a = 0.8f * gameAlpha;
			batch.setColor(bg);
			batch.draw(Assets.Tex.WHITE_RECTANGLE.t, r.x + displayedValue * r.width, r.y, (1-displayedValue) * r.width, r.height);
		}
		batch.setColor(1, 1, 1, gameAlpha);
	}
	
	public void drawText(SpriteBatch fontBatch, float alpha) {
		Assets.font.setScale(textScaleX, textScaleY);
		Assets.font.setColor(1, 1, 1, alpha);
		Assets.font.draw(fontBatch, prefix + (int)(displayedValue*maxValue) + postfix, textCoord.x, textCoord.y);
		Assets.font.setScale(1);
	}
	
	private Color lerp(Color begin, Color end, float a, float alpha) {
		temp.set(Interpolation.linear.apply(begin.r, end.r, a),
				Interpolation.linear.apply(begin.g, end.g, a),
				Interpolation.linear.apply(begin.b, end.b, a),
				alpha);
		return temp;
	}
}
