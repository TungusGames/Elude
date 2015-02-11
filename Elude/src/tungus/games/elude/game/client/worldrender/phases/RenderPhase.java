package tungus.games.elude.game.client.worldrender.phases;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public enum RenderPhase {
	FREEZE(new FreezeRenderer()),
	MINE(new MineRenderer()),
	PICKUP,
	LASER,
	ENEMY,
	ROCKET,
	VESSEL,
	EFFECT;
	
	private RenderPhase(PhaseRenderer r) {
		renderer = r;
	}
	
	private RenderPhase() {
		this(new PhaseRenderer());
	}
	
	public ShaderProgram shader = null;
	public final PhaseRenderer renderer;
}
