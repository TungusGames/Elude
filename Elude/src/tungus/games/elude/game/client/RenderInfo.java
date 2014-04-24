package tungus.games.elude.game.client;

import java.io.Serializable;
import java.util.ArrayList;

import tungus.games.elude.game.multiplayer.Connection.TransferData;
import tungus.games.elude.game.server.Vessel;
import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.enemies.Enemy;
import tungus.games.elude.game.server.pickups.Pickup;
import tungus.games.elude.game.server.rockets.Rocket;

import com.badlogic.gdx.math.Vector2;

public class RenderInfo implements Serializable, TransferData {
	private static final long serialVersionUID = -4315239911779247372L;
	static class ReducedEnemy implements Serializable {
		private static final long serialVersionUID = -7557852638993394399L;
		public Vector2 pos;
		public float rot;
		public int typeOrdinal;
		public ReducedEnemy(Vector2 p, float r, int t) {
			pos = p; rot = r; typeOrdinal = t;
		}
	}
	static class ReducedPickup implements Serializable {
		private static final long serialVersionUID = 9072429402777178805L;
		public Vector2 pos;
		public float alpha;
		public int typeOrdinal;
		public ReducedPickup(Vector2 p, float a, int t) {
			pos = p; alpha = a; typeOrdinal = t;
		}
	}
	static class ReducedRocket implements Serializable {
		private static final long serialVersionUID = 4227518796828753878L;
		public Vector2 pos;
		public float angle;
		public int typeOrdinal;
		public int id;
		public ReducedRocket(Vector2 p, float a, int t, int id) {
			pos = p; angle = a; typeOrdinal = t; this.id = id;
		}
	}
	static class ReducedVessel implements Serializable {
		private static final long serialVersionUID = 4956172612818466522L;
		public Vector2 pos;
		public float angle;
		public int id;
		public float shieldAlpha;
		public ReducedVessel(Vector2 p, float a, int i, float s) {
			pos = p; angle = a; id = i; shieldAlpha = s;
		}
	}
	static class Effect implements Serializable {
		public enum EffectType{EXPLOSION, DEBRIS, CAMSHAKE}
		public EffectType type;
		public Effect(EffectType t) {
			type = t;
		}
	}
	static class DebrisEffect extends Effect {
		public float direction;
		public DebrisEffect(float dir) {
			super(EffectType.DEBRIS);
			direction = dir;
		}
	}
	
	public ArrayList<ReducedEnemy> enemies = new ArrayList<ReducedEnemy>();
	public ArrayList<ReducedPickup> pickups = new ArrayList<ReducedPickup>();
	public ArrayList<ReducedRocket> rockets = new ArrayList<ReducedRocket>();
	public ArrayList<ReducedVessel> vessels = new ArrayList<ReducedVessel>();
	public ArrayList<Effect> effects = new ArrayList<Effect>();
	
	public float[] hp;
	public int info;
	public boolean handled = true;
	
	public void setFromWorld(World w) {		//TODO Pool reduced entities!
		enemies.clear();
		int s = w.enemies.size();
		for (int i = 0; i < s; i++) {
			Enemy e = w.enemies.get(i);
			enemies.add(new ReducedEnemy(e.pos, e.rot, e.type.ordinal()));
		}
		pickups.clear();
		s = w.pickups.size();
		for (int i = 0; i < s; i++) {
			Pickup p = w.pickups.get(i);
			pickups.add(new ReducedPickup(new Vector2(p.collisionBounds.x+Pickup.HALF_SIZE, p.collisionBounds.y+Pickup.HALF_SIZE), p.alpha, p.type.ordinal()));
		}
		vessels.clear();
		s = w.vessels.size();
		for (int i = 0; i < s; i++) {
			Vessel v = w.vessels.get(i);
			vessels.add(new ReducedVessel(v.pos, v.rot, i, v.shieldAlpha));
		}
		rockets.clear();
		s = w.rockets.size();
		for (int i = 0; i < s; i++) {
			Rocket r = w.rockets.get(i);
			rockets.add(new ReducedRocket(r.pos, r.vel.angle(), r.type.ordinal(), r.id));
		}
		effects.clear();
	}
	
	public void copyTo(TransferData otherData) {
		RenderInfo other = (RenderInfo)otherData;
		other.enemies.clear();
		int s = enemies.size();
		for (int i = 0; i < s; i++) {
			ReducedEnemy e = enemies.get(i);
			other.enemies.add(new ReducedEnemy(e.pos, e.rot, e.typeOrdinal));
		}
		other.pickups.clear();
		s = pickups.size();
		for (int i = 0; i < s; i++) {
			ReducedPickup p = pickups.get(i);
			other.pickups.add(new ReducedPickup(p.pos, p.alpha, p.typeOrdinal));
		}
		other.vessels.clear();
		s = vessels.size();
		for (int i = 0; i < s; i++) {
			ReducedVessel v = vessels.get(i);
			other.vessels.add(new ReducedVessel(v.pos, v.angle, i, v.shieldAlpha));
		}
		other.rockets.clear();
		s = rockets.size();
		for (int i = 0; i < s; i++) {
			ReducedRocket r = rockets.get(i);
			other.rockets.add(new ReducedRocket(r.pos, r.angle, r.typeOrdinal, r.id));
		}
		other.effects.clear();
		s = effects.size();
		for (int i = 0; i < s; i++) {
			Effect e = effects.get(i);
			other.effects.add(e.type == Effect.EffectType.DEBRIS ? new DebrisEffect(((DebrisEffect)e).direction) : new Effect(e.type));
		}
		other.info = info;
		s = hp.length;
		if (other.hp == null || other.hp.length < hp.length)
			other.hp = new float[hp.length];
		for (int i = 0; i < s; i++)
			other.hp[i] = hp[i];
		other.handled = false;
	}
}
