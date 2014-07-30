package tungus.games.elude.game.multiplayer.transfer;

import tungus.games.elude.game.multiplayer.transfer.RenderInfo.DebrisEffect;
import tungus.games.elude.game.multiplayer.transfer.RenderInfo.Effect;
import tungus.games.elude.game.multiplayer.transfer.RenderInfo.Effect.EffectType;
import tungus.games.elude.game.multiplayer.transfer.RenderInfo.ReducedEnemy;
import tungus.games.elude.game.multiplayer.transfer.RenderInfo.ReducedPickup;
import tungus.games.elude.game.multiplayer.transfer.RenderInfo.ReducedRocket;
import tungus.games.elude.game.multiplayer.transfer.RenderInfo.ReducedVessel;
import tungus.games.elude.util.SyncPool;

import com.badlogic.gdx.math.Vector2;

public class RenderInfoPool {

	//Multiplication by 3 because of the three RenderInfos
	private static final int ENEMY_POOL_SIZE = 3 * 40;
	private static final int PICKUP_POOL_SIZE = 3 * 10;
	private static final int ROCKET_POOL_SIZE = 3 * 80;
	private static final int VESSEL_POOL_SIZE = 3 * 3;
	private static final int EFFECT_POOL_SIZE = 3 * 20;
	private static final int DEBRIS_POOL_SIZE = 3 * 10;
	
	private static SyncPool<ReducedEnemy> enemyPool;
	private static SyncPool<ReducedPickup> pickupPool;
	private static SyncPool<ReducedRocket> rocketPool;
	private static SyncPool<ReducedVessel> vesselPool;
	private static SyncPool<Effect> effectPool;
	private static SyncPool<DebrisEffect> debrisPool;
	
	public static void init() {
		enemyPool = new SyncPool<ReducedEnemy>(ENEMY_POOL_SIZE) {
			@Override
			public synchronized ReducedEnemy newObject() {
				return new ReducedEnemy();
			}
		};
		pickupPool = new SyncPool<ReducedPickup>(PICKUP_POOL_SIZE) {
			@Override
			public synchronized ReducedPickup newObject() {
				return new ReducedPickup();
			}
		};
		rocketPool = new SyncPool<ReducedRocket>(ROCKET_POOL_SIZE) {
			@Override
			public synchronized ReducedRocket newObject() {
				return new ReducedRocket();
			}
		};
		vesselPool = new SyncPool<ReducedVessel>(VESSEL_POOL_SIZE) {
			@Override
			public synchronized ReducedVessel newObject() {
				return new ReducedVessel();
			}
		};
		effectPool = new SyncPool<Effect>(EFFECT_POOL_SIZE) {
			@Override
			public synchronized Effect newObject() {
				return new Effect();
			}
		};
		debrisPool = new SyncPool<DebrisEffect>(DEBRIS_POOL_SIZE) {
			@Override
			public synchronized DebrisEffect newObject() {
				return new DebrisEffect();
			}
		};
		//Filling up
		for (int i = 0; i < ENEMY_POOL_SIZE; i++) {
			enemyPool.free(enemyPool.obtain());
		}
		for (int i = 0; i < PICKUP_POOL_SIZE; i++) {
			pickupPool.free(pickupPool.obtain());
		}
		for (int i = 0; i < ROCKET_POOL_SIZE; i++) {
			rocketPool.free(rocketPool.obtain());
		}
		for (int i = 0; i < VESSEL_POOL_SIZE; i++) {
			vesselPool.free(vesselPool.obtain());
		}
		for (int i = 0; i < EFFECT_POOL_SIZE; i++) {
			effectPool.free(effectPool.obtain());
		}
		for (int i = 0; i < DEBRIS_POOL_SIZE; i++) {
			debrisPool.free(debrisPool.obtain());
		}
	}
	
	public static ReducedEnemy newEnemy(Vector2 p, float r, int t) {
		return newEnemy(p.x, p.y, r, t);
	}
	public static ReducedEnemy newEnemy(float x, float y, float r, int t) {
		ReducedEnemy e = enemyPool.obtain();
		e.x = x; e.y = y; e.rot = r; e.typeOrdinal = t;	return e;
	}
	
	public static ReducedPickup newPickup(Vector2 p, float a, int t) {
		return newPickup(p.x, p.y, a, t);
	}
	public static ReducedPickup newPickup(float x, float y, float a, int t) {
		ReducedPickup p = pickupPool.obtain();
		p.x = x; p.y = y; p.alpha = a; p.typeOrdinal = t; return p;
	}
	
	public static ReducedRocket newRocket(Vector2 p, float a, int t, int id) {
		return newRocket(p.x, p.y, a, t, id);
	}
	public static ReducedRocket newRocket(float x, float y, float a, int t, int id) {
		ReducedRocket r = rocketPool.obtain();
		r.x = x; r.y = y; r.angle = a; r.typeOrdinal = t; r.id = id; return r;
	}
	
	public static ReducedVessel newVessel(Vector2 p, float a, int i, float s) {
		return newVessel(p.x, p.y, a, i, s);
	}
	public static ReducedVessel newVessel(float x, float y, float a, int i, float s) {
		ReducedVessel v = vesselPool.obtain();
		v.x = x; v.y = y; v.angle = a; v.id = i; v.shieldAlpha = s; return v;
	}
	
	public static Effect newEffect(Vector2 p, int t) {
		return newEffect(p.x, p.y, t);
	}
	public static Effect newEffect(float x, float y, int t) {
		Effect e = effectPool.obtain();
		e.x = x; e.y = y; e.typeOrdinal = t; return e;
	}

	public static DebrisEffect newDebris(Vector2 p, float dir, int e) {
		return newDebris (p.x, p.y, dir, e);
	}
	public static DebrisEffect newDebris(float x, float y, float dir, int e) {
		DebrisEffect d = debrisPool.obtain();
		d.x = x; d.y = y; d.typeOrdinal = EffectType.DEBRIS.ordinal();
		d.direction = dir; d.enemy = e; return d;
	}
	
	public static void freeAlmostAll(RenderInfo ri) {
		for (int i = 0; i < ri.enemies.size(); i++)
			enemyPool.free(ri.enemies.get(i));
		for (int i = 0; i < ri.pickups.size(); i++)
			pickupPool.free(ri.pickups.get(i));
		for (int i = 0; i < ri.rockets.size(); i++)
			rocketPool.free(ri.rockets.get(i));
		for (int i = 0; i < ri.vessels.size(); i++)
			vesselPool.free(ri.vessels.get(i));
	}
	
	public static void freeAll(RenderInfo ri) {
		freeAlmostAll(ri);
		for (int i = 0; i < ri.effects.size(); i++) {
			Effect e = ri.effects.get(i);
			if (e instanceof DebrisEffect)
				debrisPool.free((DebrisEffect)e);
			else effectPool.free(e);
		}
	}
}
