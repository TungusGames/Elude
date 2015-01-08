package tungus.games.elude.game.client.worldrender.renderable;

import tungus.games.elude.Assets;
import tungus.games.elude.game.client.worldrender.WorldRenderer;
import tungus.games.elude.game.client.worldrender.lastingeffects.ParticleEffectPool.PooledEffect;
import tungus.games.elude.game.client.worldrender.phases.RenderPhase;
import tungus.games.elude.game.server.Vessel;
import tungus.games.elude.util.LinkedPool;

import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.math.Vector2;

public class VesselRenderable extends Sprite {
	private static LinkedPool<Sprite> pool = new LinkedPool<Sprite>(VesselRenderable.class, 2);
	public static Renderable create(float x, float y, float vx, float vy, float rot, float a, int updatableID, int vesselID) {
		VesselRenderable v = (VesselRenderable)pool.obtain();
		v.x = x; v.y = y; v.rot = rot; v.shieldAlpha = a; v.updatableID = updatableID; v.vesselID = vesselID; v.vx = vx; v.vy = vy;
		v.height = Vessel.DRAW_HEIGHT; v.width = Vessel.DRAW_WIDTH; v.alpha = 1;
		return v;
	}
	
	private static Vector2 tmp = new Vector2();
	
	private float shieldAlpha;
	private float vx, vy;
	private int updatableID, vesselID;
	
	public VesselRenderable(LinkedPool<Sprite> p) {
		super(p);
		phase = RenderPhase.VESSEL;
	}
	
	@Override
	public void render(WorldRenderer wr) {
		texID = (vesselID == wr.vesselID) ? Assets.Tex.VESSEL.ordinal() : Assets.Tex.VESSELRED.ordinal();
		super.render(wr);
		if (shieldAlpha > 0) {
			texID = Assets.Tex.SHIELD.ordinal();
			width = height = Vessel.SHIELD_SIZE;
			alpha = shieldAlpha;
			super.render(wr);
		}
		if (wr.updateParticles) {
			modVesselTrails(wr, tmp.set(vx, vy));
		}
	}

	private void modVesselTrails(WorldRenderer wr, Vector2 vel) {
		PooledEffect trails = (PooledEffect)(wr.lastingEffects.getFirst(updatableID));
		if (trails == null) {
			if (vel.equals(Vector2.Zero)) {
				return;
			} else {
				trails = (vesselID == wr.vesselID) ? Assets.Particles.VESSEL_TRAILS.p.obtain() : Assets.Particles.VESSEL_TRAILS_RED.p.obtain();
				ParticleEmitter particleEmitter = trails.getEmitters().get(0);
				particleEmitter = trails.getEmitters().get(0);
				particleEmitter.getEmission().setHigh(150);
				wr.lastingEffects.put(updatableID, trails);
			}
		}
		ParticleEmitter particleEmitter = trails.getEmitters().get(0);
		if (vel.equals(Vector2.Zero)) {
			particleEmitter.getEmission().setHigh(0);
		} else {
			particleEmitter.getAngle().setLow(rot-90);
			particleEmitter.getRotation().setLow(rot);
		}
		trails.setPosition(x, y);
	}
	
	@Override
	public Renderable clone() {
		return create(x, y, vx, vy, rot, shieldAlpha, updatableID, vesselID);
	}

	
}
