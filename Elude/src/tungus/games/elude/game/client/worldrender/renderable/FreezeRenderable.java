package tungus.games.elude.game.client.worldrender.renderable;

import tungus.games.elude.game.client.worldrender.WorldRenderer;
import tungus.games.elude.game.client.worldrender.phases.RenderPhase;
import tungus.games.elude.util.LinkedPool;

public class FreezeRenderable extends Renderable {
	private static LinkedPool<FreezeRenderable> pool = new LinkedPool<FreezeRenderable>(FreezeRenderable.class, 300);
	public static Renderable create(float timeLeft, float x, float y) {
		FreezeRenderable freeze = pool.obtain();
		freeze.x = x; freeze.y = y; freeze.timeLeft = timeLeft;
		freeze.phase = RenderPhase.FREEZE;
		return freeze;
	}
	
	public float x, y;
	public float timeLeft;
	
	public FreezeRenderable(LinkedPool<FreezeRenderable> p) {
		super(p);
	}

	@Override
	public void render(WorldRenderer wr) {
		// Currently, all rendering done by FreezeRenderer
	}
	
	@Override
	public Renderable clone() {
		return create(timeLeft, x, y);
	}
}
