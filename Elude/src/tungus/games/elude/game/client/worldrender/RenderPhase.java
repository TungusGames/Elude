package tungus.games.elude.game.client.worldrender;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public enum RenderPhase {
	FREEZE(new FreezeRenderer()),
	MINE(new MineRenderer()),
	PICKUP(new PhaseRenderer()),
	ENEMY(new PhaseRenderer()),
	ROCKET(new PhaseRenderer()),
	VESSEL(new PhaseRenderer()),
	EFFECT(new PhaseRenderer());
	
	private RenderPhase(PhaseRenderer r) {
		renderer = r;
	}
	
	public ShaderProgram shader = null;
	final PhaseRenderer renderer;
}
