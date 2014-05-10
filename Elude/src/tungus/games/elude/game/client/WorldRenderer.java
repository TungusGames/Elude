package tungus.games.elude.game.client;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import tungus.games.elude.Assets;
import tungus.games.elude.game.multiplayer.transfer.RenderInfo;
import tungus.games.elude.game.multiplayer.transfer.RenderInfo.DebrisEffect;
import tungus.games.elude.game.multiplayer.transfer.RenderInfo.Effect;
import tungus.games.elude.game.multiplayer.transfer.RenderInfo.Effect.EffectType;
import tungus.games.elude.game.multiplayer.transfer.RenderInfo.ReducedEnemy;
import tungus.games.elude.game.multiplayer.transfer.RenderInfo.ReducedPickup;
import tungus.games.elude.game.multiplayer.transfer.RenderInfo.ReducedRocket;
import tungus.games.elude.game.multiplayer.transfer.RenderInfo.ReducedVessel;
import tungus.games.elude.game.server.Vessel;
import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.enemies.Enemy.EnemyType;
import tungus.games.elude.game.server.pickups.Pickup;
import tungus.games.elude.game.server.pickups.Pickup.PickupType;
import tungus.games.elude.game.server.rockets.Rocket.RocketType;
import tungus.games.elude.util.CamShaker;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntMap;

public class WorldRenderer {

	private static final EnemyType[] et = EnemyType.values();
	private static final RocketType[] rt = RocketType.values();
	private static final PickupType[] pt = PickupType.values();
	private static final EffectType[] eft = EffectType.values();
	
	private static final Vector2 tmp = new Vector2();

	private SpriteBatch batch;
	private List<PooledEffect> particles = new LinkedList<PooledEffect>();
	private IntMap<PooledEffect> rockets = new IntMap<PooledEffect>(80);
	private IntMap<ReducedRocket> rocketsInFrame = new IntMap<ReducedRocket>(80);
	
	private Vector2[] vesselPositions = null;
	private PooledEffect[] vesselTrails = null;
	public OrthographicCamera camera;
	private int vesselID;

	public WorldRenderer(int myVesselID) {
		batch = new SpriteBatch(5460);
		camera = new OrthographicCamera(World.WIDTH, World.HEIGHT);
		camera.position.set(World.WIDTH/2, World.HEIGHT/2, 0);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		CamShaker.INSTANCE = new CamShaker(batch);
		this.vesselID = myVesselID;
	}

	public void render(float deltaTime, float alpha, RenderInfo r, boolean updateParticles) {
		batch.setColor(1, 1, 1, alpha);
		batch.begin();
		int size = r.enemies.size();
		for(int i = 0; i < size; i++) {
			drawEnemy(r.enemies.get(i));
		}

		size = r.pickups.size();
		for(int i = 0; i < size; i++) {
			drawPickup(r.pickups.get(i));
		}

		size = r.vessels.size();
		if (vesselPositions == null && size != 0) {
			vesselPositions = new Vector2[size];
			vesselTrails = new PooledEffect[size];
			for (int i = 0; i < size; i++) {
				vesselPositions[i] = new Vector2(r.vessels.get(i).x, r.vessels.get(i).y);
				vesselTrails[i] = Assets.vesselTrails.obtain();
				vesselTrails[i].getEmitters().get(0).getEmission().setHigh(0);
			}
		}
		for(int i = 0; i < size; i++) {
			drawVessel(r.vessels.get(i), i, updateParticles);
		}		

		drawRockets(r);
		
		size = r.effects.size();
		for (int i = 0; i < size; i++) {
			drawEffect(r.effects.get(i));
		}
		r.effects.clear(); // Don't repeat them in the next frames if no new data is received

		size = particles.size();		
		for (Iterator<PooledEffect> it = particles.iterator(); it.hasNext(); ) {
			PooledEffect p = it.next();
			if (p.isComplete()) {
				p.free();
				it.remove();
			} else {
				batch.setColor(1, 1, 1, alpha);
				if (updateParticles) {
					p.draw(batch, deltaTime);
				} else {
					p.draw(batch);
				}
				
			}
		}
		batch.end();
		batch.setColor(Color.WHITE);
	}

	private void drawEffect(Effect effect) {
		switch (eft[effect.typeOrdinal]) {
		case EXPLOSION:
			PooledEffect exp = Assets.explosion.obtain();
			exp.setPosition(effect.x, effect.y);
			particles.add(exp);
			Assets.explosionSound.play();
			break;
		case CAMSHAKE:
			Gdx.input.vibrate(100);
			CamShaker.INSTANCE.shake(0.65f, 10f);
			break;
		case DEBRIS:
			DebrisEffect e = (DebrisEffect)effect;
			PooledEffect d = Assets.debris(et[e.enemy].debrisColor, e.direction);
			d.setPosition(e.x, e.y);
			particles.add(d);
			break;
		}
	}

	private void drawRockets(RenderInfo r) {
		// Make a map of active rockets
		rocketsInFrame.clear();
		int size = r.rockets.size();
		for (int i = 0; i < size; i++) {
			rocketsInFrame.put(r.rockets.get(i).id, r.rockets.get(i));
		}
		// Update rocket effects from previous frame, remove any that are missing from the RenderData
		IntMap.Entries<PooledEffect> effectEntries = rockets.entries();
		while (effectEntries.hasNext) {
			IntMap.Entry<PooledEffect> effectEntry = effectEntries.next();
			if (rocketsInFrame.containsKey(effectEntry.key)) {
				// If it was in the new frame, update the effect and remove from the new map
				setRocketEffect(effectEntry.value, rocketsInFrame.remove(effectEntry.key));
			} else {
				// If not, remove it
				effectEntry.value.allowCompletion();
				effectEntries.remove();
			}
		}
		// Only the all-new rockets remain in the new frame's map, add new effects for them
		IntMap.Entries<ReducedRocket> rocketEntries = rocketsInFrame.entries();
		while (rocketEntries.hasNext) {
			ReducedRocket roc = rocketEntries.next().value;
			PooledEffect effect = rt[roc.typeOrdinal].effect.obtain();
			rockets.put(roc.id, setRocketEffect(effect, roc));
			if (rt[roc.typeOrdinal] == RocketType.STRAIGHT) {
				effect.getEmitters().get(0).getRotation().setLow(roc.angle-90);
			}
			particles.add(effect);
		}
		//Gdx.app.log("Rocket effects", ""+r.rockets.size() + " rockets received, " + rockets.size + " effects binded, " + particles.size() + " effects drawn");
	}

	private PooledEffect setRocketEffect(PooledEffect effect, ReducedRocket rocket) {
		effect.getEmitters().get(0).getAngle().setLow(rocket.angle-180);
		effect.setPosition(rocket.x, rocket.y);
		return effect;
	}

	private void drawEnemy(ReducedEnemy e) {
		int o = e.typeOrdinal;
		batch.draw(et[o].tex, e.x-et[o].halfWidth, e.y-et[o].halfHeight, et[o].halfWidth, et[o].halfHeight, et[o].width, et[o].height, 1, 1, e.rot);
	}

	private void drawVessel(ReducedVessel v, int i, boolean updateParticles) {
		TextureRegion t = (v.id == vesselID) ? Assets.vessel : Assets.vesselRed;
		batch.draw(t, v.x-Vessel.HALF_WIDTH, v.y-Vessel.HALF_HEIGHT, 
				Vessel.HALF_WIDTH, Vessel.HALF_HEIGHT, Vessel.DRAW_WIDTH, Vessel.DRAW_HEIGHT, 1, 1, v.angle);
		if (v.shieldAlpha > 0) {
			batch.draw(Assets.shield, v.x-Vessel.SHIELD_HALF_SIZE, v.y-Vessel.SHIELD_HALF_SIZE, Vessel.SHIELD_SIZE, Vessel.SHIELD_SIZE);
		}
		if (updateParticles) {
			modVesselTrails(tmp.set(v.x-vesselPositions[i].x, v.y-vesselPositions[i].y), i, v);
			vesselPositions[i].set(v.x, v.y);
		}
	}
	
	private void modVesselTrails(Vector2 vel, int i, ReducedVessel v) {
		PooledEffect trails = vesselTrails[i];
		ParticleEmitter particleEmitter = trails.getEmitters().get(0);
		if (vel.equals(Vector2.Zero)) {
			particleEmitter.getEmission().setHigh(0);
		} else {
			if (particleEmitter.getEmission().getHighMax() == 0) {
				vesselTrails[i] = trails = Assets.vesselTrails.obtain();
				particleEmitter = trails.getEmitters().get(0);
				particleEmitter.getEmission().setHigh(150);
				particles.add(trails);
			}
			particleEmitter.getAngle().setLow(v.angle-90);
			particleEmitter.getRotation().setLow(v.angle);
		}
		trails.setPosition(v.x, v.y);
	}

	private void drawPickup(ReducedPickup p) {
		int o = p.typeOrdinal;
		batch.setColor(1, 1, 1, batch.getColor().a*p.alpha);
		batch.draw(pt[o].tex, p.x-Pickup.HALF_SIZE, p.y-Pickup.HALF_SIZE, Pickup.DRAW_SIZE, Pickup.DRAW_SIZE);
		batch.setColor(1, 1, 1, batch.getColor().a/p.alpha);
	}


}
