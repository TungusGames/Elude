package tungus.games.elude.game.client;

import java.io.Serializable;
import java.util.ArrayList;

import tungus.games.elude.game.server.Vessel;
import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.enemies.Enemy;
import tungus.games.elude.game.server.enemies.Enemy.EnemyType;
import tungus.games.elude.game.server.pickups.Pickup;
import tungus.games.elude.game.server.pickups.Pickup.PickupType;
import tungus.games.elude.game.server.rockets.Rocket;
import tungus.games.elude.game.server.rockets.Rocket.RocketType;

import com.badlogic.gdx.math.Vector2;

public class RenderInfo implements Serializable {
	private static final long serialVersionUID = -4315239911779247372L;
	static class ReducedEnemy implements Serializable {
		private static final long serialVersionUID = -7557852638993394399L;
		public Vector2 pos;
		public float rot;
		public int typeOrdinal;
		public ReducedEnemy(Vector2 p, float r, EnemyType t) {
			pos = p; rot = r; typeOrdinal = t.ordinal();
		}
	}
	static class ReducedPickup implements Serializable {
		private static final long serialVersionUID = 9072429402777178805L;
		public Vector2 pos;
		public float alpha;
		public int typeOrdinal;
		public ReducedPickup(Vector2 p, float a, PickupType t) {
			pos = p; alpha = a; typeOrdinal = t.ordinal();
		}
	}
	static class ReducedRocket implements Serializable {
		private static final long serialVersionUID = 4227518796828753878L;
		public Vector2 pos;
		public float angle;
		public int typeOrdinal;
		public ReducedRocket(Vector2 p, float a, RocketType t) {
			pos = p; angle = a; typeOrdinal = t.ordinal();
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
	public ArrayList<ReducedEnemy> enemies = new ArrayList<ReducedEnemy>();
	public ArrayList<ReducedPickup> pickups = new ArrayList<ReducedPickup>();
	public ArrayList<ReducedRocket> rockets = new ArrayList<ReducedRocket>();
	public ArrayList<ReducedVessel> vessels = new ArrayList<ReducedVessel>();
	public void setFromWorld(World w) {		//TODO Pool reduced entities!
		enemies.clear();
		int s = w.enemies.size();
		for (int i = 0; i < s; i++) {
			Enemy e = w.enemies.get(i);
			enemies.add(new ReducedEnemy(e.pos, e.rot, e.type));
		}
		s = w.pickups.size();
		for (int i = 0; i < s; i++) {
			Pickup p = w.pickups.get(i);
			pickups.add(new ReducedPickup(new Vector2(p.collisionBounds.x+Pickup.HALF_SIZE, p.collisionBounds.y+Pickup.HALF_SIZE), p.alpha, p.type));
		}
		s = w.vessels.size();
		for (int i = 0; i < s; i++) {
			Vessel v = w.vessels.get(i);
			vessels.add(new ReducedVessel(v.pos, v.rot, i, v.shieldAlpha));
		}
		s = w.rockets.size();
		for (int i = 0; i < s; i++) {
			Rocket r = w.rockets.get(i);
			rockets.add(new ReducedRocket(r.pos, r.vel.angle(), r.type));
		}
	}
}
