package tungus.games.elude.game.client;


import java.util.LinkedList;
import java.util.List;

import tungus.games.elude.Assets;
import tungus.games.elude.game.client.RenderInfo.ReducedEnemy;
import tungus.games.elude.game.client.RenderInfo.ReducedPickup;
import tungus.games.elude.game.client.RenderInfo.ReducedRocket;
import tungus.games.elude.game.client.RenderInfo.ReducedVessel;
import tungus.games.elude.game.server.Vessel;
import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.enemies.Enemy.EnemyType;
import tungus.games.elude.game.server.pickups.Pickup;
import tungus.games.elude.game.server.pickups.Pickup.PickupType;
import tungus.games.elude.game.server.rockets.Rocket.RocketType;
import tungus.games.elude.util.CamShaker;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.IntMap;

public class WorldRenderer {

	private static final EnemyType[] et = EnemyType.values();
	private static final RocketType[] rt = RocketType.values();
	private static final PickupType[] pt = PickupType.values();
	
	private SpriteBatch batch;
	private List<PooledEffect> particles = new LinkedList<PooledEffect>();
	private IntMap<PooledEffect> rockets = new IntMap<PooledEffect>();
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
	
	public void render(float deltaTime, float alpha, RenderInfo r) {
		
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
		for(int i = 0; i < size; i++) {
			drawVessel(r.vessels.get(i));
		}		
		
		size = r.rockets.size();
		IntMap.Entries<PooledEffect> entries = rockets.entries();
		int rocketI = 0;
		ReducedRocket roc = null;
		if (entries.hasNext) {
			IntMap.Entry<PooledEffect> e = null;
			while (entries.hasNext/* && rocketI < r.rockets.size()*/) {
				e = entries.next();
				Gdx.app.log("Rocket effects", "Effect id  " + e.key);
				if (rocketI < r.rockets.size()) {
					while(e.key > r.rockets.get(rocketI).id) {
						Gdx.app.log("Rocket effects", "Rocket id " + r.rockets.get(rocketI).id + " skipped");
						rocketI++;
					}
					if (e.key == (roc = r.rockets.get(rocketI)).id) {
						setRocketEffect(e.value, roc);
						Gdx.app.log("Rocket effects", "Rocket id " + r.rockets.get(rocketI).id + " updated");
						rocketI++;
					} else {
						entries.remove();
						e.value.allowCompletion();
						Gdx.app.log("Rocket effects", "Removed " + e.key);
					}
				} else {
					entries.remove();
					e.value.allowCompletion();
					Gdx.app.log("Rocket effects", "Removed " + e.key);
				}
			}
		}
		Gdx.app.log("Rocket effects", ""+r.rockets.size() + " rockets, " + rockets.size + " effects binded, " + particles.size() + " effects drawn");
		for (; rocketI < r.rockets.size(); rocketI++) {
			roc = r.rockets.get(rocketI);
			PooledEffect e = rt[roc.typeOrdinal].effect.obtain();
			rockets.put(roc.id, setRocketEffect(e, roc));
			particles.add(e);
			Gdx.app.log("Rocket effects", "Added " + roc.id);
		}

		// TODO particle effects - adding effects, modifying rockets, vessel trails, ...
		size = particles.size();
		for (int i = 0; i < size; i++) {
			PooledEffect p = particles.get(i);//TODO iterate
			if (p.isComplete()) {
				p.free();
				particles.remove(i);
				i--;
				size--;
			} else {
				batch.setColor(1, 1, 1, alpha);
				p.draw(batch, deltaTime);
			}
		}
		batch.end();
	}
	
	private PooledEffect setRocketEffect(PooledEffect effect, ReducedRocket rocket) {
		effect.getEmitters().get(0).getAngle().setLow(rocket.angle-180);
		effect.setPosition(rocket.pos.x, rocket.pos.y);
		return effect;
	}
	
	private void drawEnemy(ReducedEnemy e) {
		int o = e.typeOrdinal;
		batch.draw(et[o].tex, e.pos.x-et[o].halfWidth, e.pos.y-et[o].halfHeight, et[o].halfWidth, et[o].halfHeight, et[o].width, et[o].height, 1, 1, e.rot);
	}
	
	private void drawVessel(ReducedVessel v) {
		TextureRegion t = (v.id == vesselID) ? Assets.vessel : Assets.vesselRed;
		batch.draw(t, v.pos.x-Vessel.HALF_WIDTH, v.pos.y-Vessel.HALF_HEIGHT, 
				Vessel.HALF_WIDTH, Vessel.HALF_HEIGHT, Vessel.DRAW_WIDTH, Vessel.DRAW_HEIGHT, 1, 1, v.angle);
		if (v.shieldAlpha > 0) {
			batch.draw(Assets.shield, v.pos.x-Vessel.SHIELD_HALF_SIZE, v.pos.y-Vessel.SHIELD_HALF_SIZE, Vessel.SHIELD_SIZE, Vessel.SHIELD_SIZE);
		}
	}
	
	private void drawPickup(ReducedPickup p) {
		int o = p.typeOrdinal;
		batch.draw(pt[o].tex, p.pos.x-Pickup.HALF_SIZE, p.pos.y-Pickup.HALF_SIZE, Pickup.DRAW_SIZE, Pickup.DRAW_SIZE);
	}
	

}
