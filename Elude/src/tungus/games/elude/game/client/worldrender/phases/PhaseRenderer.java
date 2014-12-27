package tungus.games.elude.game.client.worldrender.phases;

import tungus.games.elude.Assets;
import tungus.games.elude.game.client.worldrender.WorldRenderer;
import tungus.games.elude.game.client.worldrender.renderable.Renderable;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class PhaseRenderer {
	
	protected WorldRenderer wr;
	
	public void begin(RenderPhase phase, WorldRenderer wr, float delta) {
		this.wr = wr;
		ShaderProgram prev = (phase.ordinal() == 0) ? Assets.Shaders.DEFAULT.s : RenderPhase.values()[phase.ordinal()-1].shader;
		if (phase.shader != prev) {
			wr.batch.setShader(phase.shader);
		}
	}
	
	public void render(Renderable r) {
		r.render(wr);
	}
	
	public void end() {};
	
	public void resetContext() {};
	
}
