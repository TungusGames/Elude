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

	private static SyncPool<ReducedEnemy> enemyPool = new SyncPool<ReducedEnemy>(120) {
		@Override
		public synchronized ReducedEnemy newObject() {
			return new ReducedEnemy();
		}
	};
	private static SyncPool<ReducedPickup> pickupPool = new SyncPool<ReducedPickup>(30) {
		@Override
		public synchronized ReducedPickup newObject() {
			return new ReducedPickup();
		}
	};
	private static SyncPool<ReducedRocket> rocketPool = new SyncPool<ReducedRocket>(240) {
		@Override
		public synchronized ReducedRocket newObject() {
			return new ReducedRocket();
		}
	};
	private static SyncPool<ReducedVessel> vesselPool = new SyncPool<ReducedVessel>() {
		@Override
		public synchronized ReducedVessel newObject() {
			return new ReducedVessel();
		}
	};
	private static SyncPool<Effect> effectPool = new SyncPool<Effect>(60) {
		@Override
		public synchronized Effect newObject() {
			return new Effect();
		}
	};
	private static SyncPool<DebrisEffect> debrisPool = new SyncPool<DebrisEffect>() {
		@Override
		public synchronized DebrisEffect newObject() {
			return new DebrisEffect();
		}
	};
	
	
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
