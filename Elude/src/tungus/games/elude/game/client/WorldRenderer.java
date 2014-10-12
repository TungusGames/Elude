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
import tungus.games.elude.game.server.pickups.FreezerPickup;
import tungus.games.elude.game.server.pickups.Pickup;
import tungus.games.elude.game.server.pickups.Pickup.PickupType;
import tungus.games.elude.game.server.rockets.Rocket.RocketType;
import tungus.games.elude.menu.settings.Settings;
import tungus.games.elude.util.CamShaker;
import tungus.games.elude.util.CustomInterpolations.FadeInOut;

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
	private IntMap<EnemyHealthbar> enemyHps = new IntMap<EnemyHealthbar>(50);
	private IntMap<ReducedEnemy> enemiesInFrame = new IntMap<ReducedEnemy>(50);
	private MineEffects mines = new MineEffects(100);

	private Vector2[] vesselPositions = null;
	private PooledEffect[] vesselTrails = null;
	public OrthographicCamera camera;
	private int vesselID;

	private float freezeTime = 0f;
	private final FadeInOut freezeFade= new FadeInOut(0.5f, FreezerPickup.FREEZE_TIME);

	public WorldRenderer(int myVesselID) {
		batch = new SpriteBatch(5460);
		camera = new OrthographicCamera(World.WIDTH, World.HEIGHT);
		camera.position.set(World.WIDTH/2, World.HEIGHT/2, 0);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		CamShaker.INSTANCE = new CamShaker(batch);
		this.vesselID = myVesselID;
	}

	public void render(float deltaTime, float alpha, RenderInfo r, boolean updateEffects) {
		/*if (freezeTime > 0f) {
			freezeTime -= deltaTime;
			batch.setColor(1 - freezeFade.apply(FreezerPickup.FREEZE_TIME % freezeTime), 1, 1, 1);
		} else*/ 
		prepRockets(r);
		mines.render(deltaTime, alpha);
		
		batch.setColor(1, 1, 1, alpha);
		batch.begin();
		int size = r.enemies.size();
		for(int i = 0; i < size; i++) {
			drawEnemy(r.enemies.get(i));
		}
		drawEnemyHPs(r.enemies, deltaTime);
		batch.setColor(1, 1, 1, alpha);
		size = r.pickups.size();
		for(int i = 0; i < size; i++) {
			drawPickup(r.pickups.get(i));
		}
		if (freezeTime > 0f) {
			if (updateEffects) {
				freezeTime -= deltaTime;
			}
			batch.setColor(0f, 1f, 1f, freezeFade.apply(freezeTime) * 0.75f);
			batch.draw(Assets.whiteRectangle, 0, 0, World.WIDTH, World.HEIGHT);
			batch.setColor(1, 1, 1, alpha);
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
			drawVessel(r.vessels.get(i), i, updateEffects);
		}		

		drawRockets(r);
		if (updateEffects) {
			size = r.effects.size();
			for (int i = 0; i < size; i++) {
				drawEffect(r.effects.get(i));
			}
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
				if (updateEffects) {
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
			if (Settings.INSTANCE.soundOn) {
				Assets.explosionSound.play();
			}
			break;
		case CAMSHAKE:
			if (Settings.INSTANCE.vibrateOn) {
				Gdx.input.vibrate(100);
			}
			CamShaker.INSTANCE.shake(0.65f, 10f);
			break;
		case DEBRIS:
			DebrisEffect e = (DebrisEffect)effect;
			PooledEffect d = Assets.debris(et[e.enemy].debrisColor, e.direction);
			d.setPosition(e.x, e.y);
			particles.add(d);
			break;
		case LASERSHOT:
			if (Settings.INSTANCE.soundOn) {
				Assets.laserShot.play();
			}
			break;
		case FREEZE:
			freezeTime = FreezerPickup.FREEZE_TIME;
			break;
		}
	}

	private void drawEnemyHPs(List<ReducedEnemy> l, float deltaTime) {
		// Make a map of active enemies
		enemiesInFrame.clear();
		int size = l.size();
		for (int i = 0; i < size; i++) {
			ReducedEnemy e = l.get(i);
			enemiesInFrame.put(e.id, e);
		}
		// Update and draw HP bars from previous frame, remove any that finish
		IntMap.Entries<EnemyHealthbar> h = enemyHps.entries();
		while (h.hasNext) {
			IntMap.Entry<EnemyHealthbar> hpEntry = h.next();
			if (enemiesInFrame.containsKey(hpEntry.key)) {
				// If it was in the new frame, draw and remove from the new map
				//drawHpBar(enemiesInFrame.remove(hpEntry.key).hp, hpEntry.value);
				ReducedEnemy e = enemiesInFrame.remove(hpEntry.key);
				hpEntry.value.draw(batch, e.hp, deltaTime, e.x, e.y);
			} else if (hpEntry.value.drawDead(batch, deltaTime)) {  // If not, draw and check if it should be removed
				h.remove();
			}
		}
		// Only the all-new enemies remain in the new frame's map, add their HPs - no need to draw yet
		IntMap.Entries<ReducedEnemy> enemyEntries = enemiesInFrame.entries();
		while (enemyEntries.hasNext) {
			ReducedEnemy e = enemyEntries.next().value;
			enemyHps.put(e.id, new EnemyHealthbar());
		}
	}

	private void prepRockets(RenderInfo r) {
		// Make a map of active rockets
		rocketsInFrame.clear();
		mines.clear();
		int size = r.rockets.size();
		for (int i = 0; i < size; i++) {
			ReducedRocket roc = r.rockets.get(i);
			if (roc.typeOrdinal != RocketType.MINE.ordinal()) {
				rocketsInFrame.put(r.rockets.get(i).id, r.rockets.get(i));
			} else {
				mines.add(roc.id, roc.x, roc.y);
			}

		}
	}

	private void drawRockets(RenderInfo r) {

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
		if (rt[rocket.typeOrdinal] == RocketType.STRAIGHT) {
			effect.getEmitters().get(0).getRotation().setLow(rocket.angle-90);
		}
		effect.setPosition(rocket.x, rocket.y);
		return effect;
	}

	private void drawEnemy(ReducedEnemy e) {
		int o = e.typeOrdinal;
		batch.draw(et[o].tex, e.x-e.width/2, e.y-e.height/2, e.width/2, e.height/2, e.width, e.height, 1, 1, e.rot);
	}

	private void drawVessel(ReducedVessel v, int i, boolean updateParticles) {
		TextureRegion t = (v.id == vesselID) ? Assets.vessel : Assets.vesselRed;
		batch.draw(t, v.x-Vessel.HALF_WIDTH, v.y-Vessel.HALF_HEIGHT, 
				Vessel.HALF_WIDTH, Vessel.HALF_HEIGHT, Vessel.DRAW_WIDTH, Vessel.DRAW_HEIGHT, 1, 1, v.angle);
		//batch.draw(Assets.smallCircle, v.x-Vessel.COLLIDER_HALF, v.y-Vessel.COLLIDER_HALF, 
		//		Vessel.COLLIDER_HALF, Vessel.COLLIDER_HALF, Vessel.COLLIDER_SIZE, Vessel.COLLIDER_SIZE, 1, 1, v.angle);
		if (v.shieldAlpha > 0) {
			batch.setColor(1, 1, 1, v.shieldAlpha);
			batch.draw(Assets.shield, v.x-Vessel.SHIELD_HALF_SIZE, v.y-Vessel.SHIELD_HALF_SIZE, Vessel.SHIELD_SIZE, Vessel.SHIELD_SIZE);
			batch.setColor(1, 1, 1, 1);
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
