package tungus.games.elude.game.multiplayer.transfer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import tungus.games.elude.game.multiplayer.Connection.TransferData;
import tungus.games.elude.game.server.Vessel;
import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.enemies.Enemy;
import tungus.games.elude.game.server.pickups.Pickup;
import tungus.games.elude.game.server.rockets.Rocket;

import com.badlogic.gdx.math.Vector2;

public class RenderInfo extends TransferData {
	private static final long serialVersionUID = -4315239911779247372L;
	public static class ReducedEnemy implements Serializable {
		private static final long serialVersionUID = -7557852638993394399L;
		public float x, y;
		public float rot;
		public float hp;
		public float width;
		public float height;
		public int id;
		public int typeOrdinal;
	}
	public static class ReducedPickup implements Serializable {
		private static final long serialVersionUID = 9072429402777178805L;
		public float x, y;
		public float alpha;
		public int typeOrdinal;
	}
	public static class ReducedRocket implements Serializable {
		private static final long serialVersionUID = 4227518796828753878L;
		public float x, y;
		public float angle;
		public int typeOrdinal;
		public int id;
	}
	public static class ReducedVessel implements Serializable {
		private static final long serialVersionUID = 4956172612818466522L;
		public float x, y;
		public float angle;
		public int id;
		public float shieldAlpha;
	}
	public static class Effect implements Serializable {
		private static final long serialVersionUID = 1892821292900322357L;
		public enum EffectType{EXPLOSION, DEBRIS, CAMSHAKE, LASERSHOT}
		public int typeOrdinal;
		public float x, y;
	}
	public static class DebrisEffect extends Effect {
		private static final long serialVersionUID = -5099330118557050914L;
		public float direction;
		public int enemy;
	}
	
	public List<ReducedEnemy> enemies = new ArrayList<ReducedEnemy>();
	public List<ReducedPickup> pickups = new ArrayList<ReducedPickup>();
	public List<ReducedRocket> rockets = new ArrayList<ReducedRocket>();
	public List<ReducedVessel> vessels = new ArrayList<ReducedVessel>();
	public List<Effect> effects;
	
	public float[] hp;
	
	private final World w;
	
	public RenderInfo(World world) {
		w = world;
		if (w != null) {
			effects = world.effects;
		} else {
			effects = new ArrayList<Effect>();
		}
		
	}

	public void setFromWorld() {
		RenderInfoPool.freeAlmostAll(this); // Puts everything in the pool except effects
		enemies.clear();
		int s = w.enemies.size();
		for (int i = 0; i < s; i++) {
			Enemy e = w.enemies.get(i);
			enemies.add(RenderInfoPool.newEnemy(e.pos, e.rot, e.type.ordinal(), e.hp / e.maxHp, e.id, e.width(), e.height()));
		}
		pickups.clear();
		s = w.pickups.size();
		for (int i = 0; i < s; i++) {
			Pickup p = w.pickups.get(i);
			pickups.add(RenderInfoPool.newPickup(new Vector2(p.collisionBounds.x+Pickup.HALF_SIZE, p.collisionBounds.y+Pickup.HALF_SIZE), p.alpha, p.type.ordinal()));
		}
		vessels.clear();
		s = w.vessels.size();
		for (int i = 0; i < s; i++) {
			Vessel v = w.vessels.get(i);
			vessels.add(RenderInfoPool.newVessel(v.pos, v.rot, i, v.shieldAlpha));
		}
		rockets.clear();
		s = w.rockets.size();
		for (int i = 0; i < s; i++) {
			Rocket r = w.rockets.get(i);
			rockets.add(RenderInfoPool.newRocket(r.pos, r.vel.angle(), r.type.ordinal(), r.id));
		}
		for (int i = 0; i < hp.length; i++) {
			hp[i] = w.vessels.get(i).hp / Vessel.MAX_HP;
		}
	}
	
	@Override
	public TransferData copyTo(TransferData otherData) {
		RenderInfo other = null;
		if (otherData instanceof RenderInfo) {
			other = (RenderInfo)otherData;
		} else {
			other = new RenderInfo(null);
		}
		super.copyTo(other);
		RenderInfoPool.freeAll(other);
		other.enemies.clear();
		int s = enemies.size();
		for (int i = 0; i < s; i++) {
			ReducedEnemy e = enemies.get(i);
			other.enemies.add(RenderInfoPool.newEnemy(e.x, e.y, e.rot, e.typeOrdinal, e.hp, e.id, e.width, e.height));
		}
		other.pickups.clear();
		s = pickups.size();
		for (int i = 0; i < s; i++) {
			ReducedPickup p = pickups.get(i);
			other.pickups.add(RenderInfoPool.newPickup(p.x, p.y, p.alpha, p.typeOrdinal));
		}
		other.vessels.clear();
		s = vessels.size();
		for (int i = 0; i < s; i++) {
			ReducedVessel v = vessels.get(i);
			other.vessels.add(RenderInfoPool.newVessel(v.x, v.y, v.angle, i, v.shieldAlpha));
		}
		other.rockets.clear();
		s = rockets.size();
		for (int i = 0; i < s; i++) {
			ReducedRocket r = rockets.get(i);
			other.rockets.add(RenderInfoPool.newRocket(r.x, r.y, r.angle, r.typeOrdinal, r.id));
		}
		other.effects.clear();
		s = effects.size();
		for (int i = 0; i < s; i++) {
			Effect e = effects.get(i);
			other.effects.add(e.typeOrdinal == Effect.EffectType.DEBRIS.ordinal() ? 
					RenderInfoPool.newDebris(e.x, e.y, ((DebrisEffect)e).direction, ((DebrisEffect)e).enemy) : 
						RenderInfoPool.newEffect(e.x, e.y, e.typeOrdinal));
		}
		s = hp.length;
		if (other.hp == null || other.hp.length < hp.length)
			other.hp = new float[hp.length];
		for (int i = 0; i < s; i++)
			other.hp[i] = hp[i];
		return other;
	}
}
