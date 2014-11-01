package tungus.games.elude.game.client.worldrender;

import tungus.games.elude.game.client.worldrender.Renderable.Effect;
import tungus.games.elude.util.LinkedPool;
import tungus.games.elude.util.LinkedPool.Poolable;

public class FreezeEffect extends Poolable implements Effect {

	private static LinkedPool<FreezeEffect> pool = new LinkedPool<FreezeEffect>(FreezeEffect.class, 2);
	public static Effect create(float x, float y, float t) {
		FreezeEffect p = pool.obtain();
		p.x = x; p.y = y; p.time = t;
		return p;
	}
	
	private float x, y, time;
	
	public FreezeEffect(LinkedPool<FreezeEffect> p) {
		super(p);
	}

	@Override
	public void render(WorldRenderer wr) {
		((FreezeRenderer)RenderPhase.FREEZE.renderer).turnOn(x, y, time);
	}
	
	@Override
	public Renderable clone() {
		return create(x, y, time);
	}
}
