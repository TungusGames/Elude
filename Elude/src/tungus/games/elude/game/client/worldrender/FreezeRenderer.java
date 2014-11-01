package tungus.games.elude.game.client.worldrender;

import tungus.games.elude.Assets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;

public class FreezeRenderer extends PhaseRenderer {
	
	private static final float MAX_ALPHA = 0.75f;
	private static final float MAX_SIZE = 40f; // Radius
	private static final float FADE_TIME = 0.25f; 
	private static final float SIZE_SPEED = MAX_SIZE / FADE_TIME; // Enlarge in the same time as fadein
	private static final Color color = new Color(0, 1f, 1, 1);
	
	private float timeLeft = 0;
	private float alpha = 0;
	private float size = 0;
	private Vector2 center = new Vector2();
	public boolean active = false;
	public ShaderProgram enemyShader = Assets.Shaders.FREEZE_ENEMY.s;
	
	public void turnOn(float x, float y, float time) {
		if (timeLeft <= 0) {
			center.set(x, y);
			active = true;
		}
		timeLeft = time;
	}
	
	@Override
	public void begin(RenderPhase p, WorldRenderer r, float delta) {
		super.begin(p, r, delta);
		timeLeft -= delta;
		if (timeLeft < 0) {
			alpha = size = 0;
			active = false;
			return;
		}
		
		size = Math.min(MAX_SIZE, size + SIZE_SPEED * delta);
		if (timeLeft < FADE_TIME) {
			alpha = timeLeft / FADE_TIME * MAX_ALPHA;
		} else {
			alpha = Math.min(MAX_ALPHA, alpha + delta / FADE_TIME);
		}
		r.batch.setColor(color.r, color.g, color.b, alpha);
		r.batch.draw(Assets.Tex.LINEAR_GRADIENT_SPOT.t, center.x - size/2, center.y - size/2, size, size);
		
		r.batch.end();
		enemyShader.begin();
		enemyShader.setUniformf("a", alpha/MAX_ALPHA);
		enemyShader.end();
		r.batch.begin();
	}
	
	@Override
	public void resetContext() {
		if (alpha > 0) {
			enemyShader.begin();
			enemyShader.setUniformf("a", alpha/MAX_ALPHA);
			enemyShader.end();
		}
	}
	
	
}
