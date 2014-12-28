package tungus.games.elude.game.client.worldrender.renderable;

import tungus.games.elude.game.client.worldrender.WorldRenderer;
import tungus.games.elude.game.client.worldrender.phases.RenderPhase;
import tungus.games.elude.util.LinkedPool;

public class MineRenderable extends Renderable {
	
	private static LinkedPool<MineRenderable> pool = new LinkedPool<MineRenderable>(MineRenderable.class, 300);
	public static Renderable create(float x, float y, int adder) {
		MineRenderable mine = pool.obtain();
		mine.x = x; mine.y = y; mine.adderID = adder;
		mine.phase = RenderPhase.MINE;
		return mine;
	}
	
	public float x, y;
	public int adderID;
	
	public MineRenderable(LinkedPool<MineRenderable> p) {
		super(p);
	}

	@Override
	public void render(WorldRenderer wr) {
		// Currently, all rendering done by MineRenderer
	}
	
	@Override
	public Renderable clone() {
		return create(x, y, adderID);
	}
}
