package tungus.games.elude.game.client.worldrender.lastingeffects;


import tungus.games.elude.game.client.worldrender.lastingeffects.ParticleEffectPool.PooledEffect;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Pool;

public class ParticleEffectPool extends Pool<PooledEffect> {
	private final ParticleEffect effect;

	public ParticleEffectPool (ParticleEffect effect, int initialCapacity, int max) {
		super(initialCapacity, max);
		this.effect = effect;
	}

	protected PooledEffect newObject () {
		return new PooledEffect(effect);
	}

	public PooledEffect obtain () {
		PooledEffect effect = super.obtain();
		effect.reset();
		return effect;
	}

	public class PooledEffect extends ParticleEffect implements LastingEffect {
		PooledEffect (ParticleEffect effect) {
			super(effect);
		}
		public void free() {
			ParticleEffectPool.this.free(this);
		}
		@Override
		public void render(SpriteBatch batch, float delta) {
			super.draw(batch, delta);
		}
	}
}
