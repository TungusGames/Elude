package tungus.games.elude.game.client;

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

public class RenderInfo implements Serializable, TransferData {
	private static final long serialVersionUID = -4315239911779247372L;
	static class ReducedEnemy implements Serializable {
		private static final long serialVersionUID = -7557852638993394399L;
		public float x, y;
		public float rot;
		public int typeOrdinal;
		public ReducedEnemy(Vector2 p, float r, int t) {
			this(p.x, p.y, r, t);
		}
		public ReducedEnemy(float x, float y, float r, int t) {
			this.x = x; this.y = y; rot = r; typeOrdinal = t;
		}
	}
	static class ReducedPickup implements Serializable {
		private static final long serialVersionUID = 9072429402777178805L;
		public float x, y;
		public float alpha;
		public int typeOrdinal;
		public ReducedPickup(Vector2 p, float a, int t) {
			this(p.x, p.y, a, t);
		}
		public ReducedPickup(float x, float y, float a, int t) {
			this.x = x; this.y = y; alpha = a; typeOrdinal = t;
		}
	}
	static class ReducedRocket implements Serializable {
		private static final long serialVersionUID = 4227518796828753878L;
		public float x, y;
		public float angle;
		public int typeOrdinal;
		public int id;
		public ReducedRocket(Vector2 p, float a, int t, int id) {
			this(p.x, p.y, a, t, id);
		}
		public ReducedRocket(float x, float y, float a, int t, int id) {
			this.x = x; this.y = y; angle = a; typeOrdinal = t; this.id = id;
		}
	}
	static class ReducedVessel implements Serializable {
		private static final long serialVersionUID = 4956172612818466522L;
		public float x, y;
		public float angle;
		public int id;
		public float shieldAlpha;
		public ReducedVessel(Vector2 p, float a, int i, float s) {
			this(p.x, p.y, a, i, s);
		}
		public ReducedVessel(float x, float y, float a, int i, float s) {
			this.x = x; this.y = y; angle = a; id = i; shieldAlpha = s;
		}
	}
	public static class Effect implements Serializable {
		private static final long serialVersionUID = 1892821292900322357L;
		public enum EffectType{EXPLOSION, DEBRIS, CAMSHAKE}
		public int typeOrdinal;
		public float x, y;
		public Effect(Vector2 p, int t) {
			this(p.x, p.y, t);
		}
		public Effect(float x, float y, int t) {
			this.x = x; this.y = y; typeOrdinal = t;
		}
	}
	public static class DebrisEffect extends Effect {
		private static final long serialVersionUID = -5099330118557050914L;
		public float direction;
		public int enemy;
		public DebrisEffect(Vector2 p, float dir, int e) {
			super(p, EffectType.DEBRIS.ordinal());
			direction = dir;
			enemy = e;
		}
		public DebrisEffect(float x, float y, float dir, int e) {
			super(x, y, EffectType.DEBRIS.ordinal());
			direction = dir;
			enemy = e;
		}
	}
	
	public List<ReducedEnemy> enemies = new ArrayList<ReducedEnemy>();
	public List<ReducedPickup> pickups = new ArrayList<ReducedPickup>();
	public List<ReducedRocket> rockets = new ArrayList<ReducedRocket>();
	public List<ReducedVessel> vessels = new ArrayList<ReducedVessel>();
	public List<Effect> effects;
	
	public float[] hp;
	public int info;
	public boolean handled = true;
	
	private final World w;
	
	public RenderInfo(World world) {
		w = world;
		if (w != null) {
			effects = world.effects;
		} else {
			effects = new ArrayList<Effect>();
		}
		
	}

	public void setFromWorld() {		//TODO Pool reduced entities!
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
	}
	
	public void copyTo(TransferData otherData) {
		RenderInfo other = (RenderInfo)otherData;
		other.enemies.clear();
		int s = enemies.size();
		for (int i = 0; i < s; i++) {
			ReducedEnemy e = enemies.get(i);
			other.enemies.add(new ReducedEnemy(e.x, e.y, e.rot, e.typeOrdinal));
		}
		other.pickups.clear();
		s = pickups.size();
		for (int i = 0; i < s; i++) {
			ReducedPickup p = pickups.get(i);
			other.pickups.add(new ReducedPickup(p.x, p.y, p.alpha, p.typeOrdinal));
		}
		other.vessels.clear();
		s = vessels.size();
		for (int i = 0; i < s; i++) {
			ReducedVessel v = vessels.get(i);
			other.vessels.add(new ReducedVessel(v.x, v.y, v.angle, i, v.shieldAlpha));
		}
		other.rockets.clear();
		s = rockets.size();
		for (int i = 0; i < s; i++) {
			ReducedRocket r = rockets.get(i);
			other.rockets.add(new ReducedRocket(r.x, r.y, r.angle, r.typeOrdinal, r.id));
		}
		other.effects.clear();
		s = effects.size();
		for (int i = 0; i < s; i++) {
			Effect e = effects.get(i);
			other.effects.add(e.typeOrdinal == Effect.EffectType.DEBRIS.ordinal() ? 
								new DebrisEffect(e.x, e.y, ((DebrisEffect)e).direction, ((DebrisEffect)e).enemy) : 
								new Effect(e.x, e.y, e.typeOrdinal));
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
