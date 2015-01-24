package tungus.games.elude.game.client.worldrender.phases;

import tungus.games.elude.Assets;
import tungus.games.elude.Assets.Shaders;
import tungus.games.elude.game.client.worldrender.WorldRenderer;
import tungus.games.elude.game.client.worldrender.renderable.FreezeRenderable;
import tungus.games.elude.game.client.worldrender.renderable.Renderable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class FreezeRenderer extends PhaseRenderer {
	
	private static final float MAX_ALPHA = 0.75f;
	private static final float MAX_SIZE = 40f; // Radius
	private static final float FADE_TIME = 0.25f; 
	private static final float SIZE_SPEED = MAX_SIZE / FADE_TIME; // Enlarge in the same time as fadein
	private static final Color color = new Color(0, 1f, 1, 1);
	
	private float delta;
	private float alpha = 0;
	private float size = 0;
	public ShaderProgram frozenEnemyShader = Assets.Shaders.FREEZE_ENEMY.s;
	
	@Override
	public void begin(RenderPhase p, WorldRenderer r, float delta) {
		super.begin(p, r, delta);
		this.delta = delta;
	}
	
	@Override
	public void render(Renderable r) {
		FreezeRenderable freeze = (FreezeRenderable)r;
		
		if (freeze.timeLeft <= 0) {			
			alpha = size = 0;
			RenderPhase.ENEMY.shader = Shaders.DEFAULT.s;
		} else {
			size = Math.min(MAX_SIZE, size + SIZE_SPEED * delta);
			if (freeze.timeLeft < FADE_TIME) {
				alpha = freeze.timeLeft / FADE_TIME * MAX_ALPHA;
			} else {
				alpha = Math.min(MAX_ALPHA, alpha + delta / FADE_TIME);
			}
			
			wr.batch.setColor(color.r, color.g, color.b, alpha);
			wr.batch.draw(Assets.Tex.LINEAR_GRADIENT_SPOT.t, freeze.x - size/2, freeze.y - size/2, size, size);
			
			RenderPhase.ENEMY.shader = frozenEnemyShader;
			wr.batch.end();			
			frozenEnemyShader.begin();
			frozenEnemyShader.setUniformf("a", alpha/MAX_ALPHA);
			frozenEnemyShader.end();
			wr.batch.begin();
		}
	}
	
	@Override
	public void resetContext() {
		if (alpha > 0) {
			frozenEnemyShader.begin();
			frozenEnemyShader.setUniformf("a", alpha/MAX_ALPHA);
			frozenEnemyShader.end();
		}
	}
	
	
}
