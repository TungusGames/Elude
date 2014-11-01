package tungus.games.elude.game.client.worldrender;

import tungus.games.elude.util.LinkedPool;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;

public class StraightRocketRenderable extends RocketRenderable {
	
	private static LinkedPool<RocketRenderable> pool = new LinkedPool<RocketRenderable>(StraightRocketRenderable.class, 300);
	public static Renderable create(float X, float Y, float a, int i, int p) {	
		StraightRocketRenderable r = (StraightRocketRenderable)pool.obtain();
		r.x = X; r.y = Y; r.angle = a; r.rocketID = i; r.particleTypeID = p;
		return r;
	}
	
	public StraightRocketRenderable(LinkedPool<RocketRenderable> p) {
		super(p);
	}
	
	@Override
	protected void setRocketEffect(PooledEffect effect) {
		super.setRocketEffect(effect);
		effect.getEmitters().get(0).getRotation().setLow(angle-90);
	}
}
