package tungus.games.elude.game.client.worldrender.phases;

import tungus.games.elude.Assets;
import tungus.games.elude.game.client.worldrender.WorldRenderer;
import tungus.games.elude.game.client.worldrender.renderable.Renderable;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class PhaseRenderer {
	
	protected WorldRenderer wr;
	private final int srcBlend, dstBlend;
	
	public PhaseRenderer() {
		this(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	public PhaseRenderer(int srcBlend, int dstBlend) {
		this.srcBlend = srcBlend;
		this.dstBlend = dstBlend;
	}
	
	public void begin(RenderPhase phase, WorldRenderer wr, float delta) {
		this.wr = wr;
		ShaderProgram prev = (phase.ordinal() == 0) ? Assets.Shaders.DEFAULT.s : RenderPhase.values()[phase.ordinal()-1].shader;
		if (phase.shader != prev) {
			wr.batch.setShader(phase.shader);
		}
		wr.batch.setBlendFunction(srcBlend, dstBlend);
	}
	
	public void render(Renderable r) {
		r.render(wr);
	}
	
	public void end() {};
	
	public void resetContext() {};
	
}
