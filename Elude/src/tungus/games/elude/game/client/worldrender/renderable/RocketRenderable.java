package tungus.games.elude.game.client.worldrender.renderable;

import tungus.games.elude.Assets;
import tungus.games.elude.Assets.Particles;
import tungus.games.elude.game.client.worldrender.WorldRenderer;
import tungus.games.elude.game.client.worldrender.phases.RenderPhase;
import tungus.games.elude.util.LinkedPool;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;

public class RocketRenderable extends Renderable {
	
	/*private static class BasicRocketRenderable extends RocketRenderable<BasicRocketRenderable> {
		private static LinkedPool<BasicRocketRenderable> pool = new LinkedPool<BasicRocketRenderable>(BasicRocketRenderable.class, 300, 600);
		public static Renderable create(float X, float Y, float a, int i, int p) {
			BasicRocketRenderable r = pool.obtain();
			r.x = X; r.y = Y; r.angle = a; r.rocketID = i; r.particleTypeID = p;
			return r;
		}
		public BasicRocketRenderable(LinkedPool<BasicRocketRenderable> p) {
			super(p);
		}
	};*/
	private static LinkedPool<RocketRenderable> pool = new LinkedPool<RocketRenderable>(RocketRenderable.class, 300);
	public static Renderable create(float x, float y, float a, int i, Particles p) {
		RocketRenderable r = pool.obtain();
		r.x = x; r.y = y; r.angle = a; r.rocketID = i; r.particleTypeID = p.ordinal();
		return r;
	}
	
	public float x, y, angle;
	public int rocketID, particleTypeID;
	
	public RocketRenderable(LinkedPool<RocketRenderable> p) {
		super(p);
		phase = RenderPhase.ROCKET;
	}
	
	@Override
	public void render(WorldRenderer wr) {
		PooledEffect particles = wr.lastingEffects.get(rocketID);
		if (particles == null) {
			particles = Assets.Particles.values()[particleTypeID].p.obtain();
			wr.lastingEffects.put(rocketID, particles);
		}
		setRocketEffect(particles);
	}
	
	protected void setRocketEffect(PooledEffect effect) {
		effect.getEmitters().get(0).getAngle().setLow(angle-180);
		effect.setPosition(x, y);
	}
	
	@Override
	public Renderable clone() {
		return create(x, y, angle, rocketID, Particles.values()[particleTypeID]);
	}
}
