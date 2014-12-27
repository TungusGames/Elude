package tungus.games.elude.game.client.worldrender;

import tungus.games.elude.util.LinkedPool;
import tungus.games.elude.util.LinkedPool.Poolable;


public abstract class Renderable extends Poolable {
	
	public RenderPhase phase;
	
	public Renderable(LinkedPool<?> p) {
		super(p);
	}
	public abstract void render(WorldRenderer wr);
	public abstract Renderable clone();
	
	/**
	 * Class for renderables which last one frame, i.e. renderables that shouldn't be cleared from an unhandled previous frame.
	 * Provides no extra functionality, just serves for separating these.
	 */
	public static abstract class Effect extends Renderable {
		public Effect(LinkedPool<?> p) {
			super(p);
			phase = RenderPhase.EFFECT;
		}
	}
}
