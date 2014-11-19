package tungus.games.elude.game.client.worldrender;

import tungus.games.elude.Assets.Particles;
import tungus.games.elude.util.LinkedPool;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;

public class StraightRocketRenderable extends RocketRenderable {
	
	private static LinkedPool<RocketRenderable> pool = new LinkedPool<RocketRenderable>(StraightRocketRenderable.class, 300);
	public static Renderable create(float x, float y, float angle, int id) {	
		StraightRocketRenderable r = (StraightRocketRenderable)pool.obtain();
		r.x = x; r.y = y; r.angle = angle; r.rocketID = id; r.particleTypeID = Particles.STRAIGHT_ROCKET.ordinal();
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
	
	@Override
	public Renderable clone() {
		return create(x, y, angle, rocketID, Particles.values()[particleTypeID]);
	}
}
