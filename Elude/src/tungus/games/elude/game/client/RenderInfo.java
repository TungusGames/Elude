package tungus.games.elude.game.client;

import java.io.Serializable;
import java.util.ArrayList;

import tungus.games.elude.game.server.enemies.Enemy.EnemyType;
import tungus.games.elude.game.server.pickups.Pickup.PickupType;
import tungus.games.elude.game.server.rockets.Rocket.RocketType;

import com.badlogic.gdx.math.Vector2;

public class RenderInfo implements Serializable {
	private static final long serialVersionUID = -4315239911779247372L;
	static class ReducedEnemy implements Serializable {
		private static final long serialVersionUID = -7557852638993394399L;
		public Vector2 pos;
		public float rot;
		public EnemyType type;
		public ReducedEnemy(Vector2 p, float r, EnemyType t) {
			pos = p; rot = r; type = t;
		}
	}
	static class ReducedPickup implements Serializable {
		private static final long serialVersionUID = 9072429402777178805L;
		public Vector2 pos;
		public float alpha;
		public PickupType type;
		public ReducedPickup(Vector2 p, float a, PickupType t) {
			pos = p; alpha = a; type = t;
		}
	}
	static class ReducedRocket implements Serializable {
		private static final long serialVersionUID = 4227518796828753878L;
		public Vector2 pos;
		public float angle;
		public RocketType type;
		public ReducedRocket(Vector2 p, float a, RocketType t) {
			pos = p; angle = a; type = t;
		}
	}
	static class ReducedVessel implements Serializable {
		private static final long serialVersionUID = 4956172612818466522L;
		public Vector2 pos;
		public float angle;
		public int id;
		public ReducedVessel(Vector2 p, float a, int i) {
			pos = p; angle = a; id = i;
		}
	}
	public ArrayList<ReducedEnemy> enemies;
	public ArrayList<ReducedPickup> pickups;
	public ArrayList<ReducedRocket> rockets;
}
