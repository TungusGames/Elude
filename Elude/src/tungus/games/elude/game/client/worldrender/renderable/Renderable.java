package tungus.games.elude.game.client.worldrender.renderable;

import java.util.List;

import tungus.games.elude.Assets.Particles;
import tungus.games.elude.Assets.Sounds;
import tungus.games.elude.game.client.worldrender.WorldRenderer;
import tungus.games.elude.game.client.worldrender.phases.RenderPhase;
import tungus.games.elude.game.client.worldrender.renderable.effect.ParticleAdder;
import tungus.games.elude.game.client.worldrender.renderable.effect.SoundEffect;
import tungus.games.elude.util.LinkedPool;
import tungus.games.elude.util.LinkedPool.Poolable;

import com.badlogic.gdx.math.Vector2;


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
		public static void addExplosion(List<Renderable> effects, Vector2 pos) {
			effects.add(SoundEffect.create(Sounds.EXPLOSION));
			effects.add(ParticleAdder.create(Particles.EXPLOSION, pos.x, pos.y));
		}
	}
}
