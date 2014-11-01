package tungus.games.elude.game.client.worldrender;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public enum RenderPhase {
	FREEZE(new FreezeRenderer()),
	MINE(new MineRenderer()),
	PICKUP(new PhaseRenderer()),
	ENEMY(PICKUP.renderer),
	ROCKET(PICKUP.renderer),
	VESSEL(PICKUP.renderer),
	EFFECT(PICKUP.renderer);
	
	private RenderPhase(PhaseRenderer r) {
		renderer = r;
	}
	
	public ShaderProgram shader = null;
	final PhaseRenderer renderer;
}
