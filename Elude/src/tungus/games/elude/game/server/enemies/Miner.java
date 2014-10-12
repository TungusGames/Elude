package tungus.games.elude.game.server.enemies;

import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.rockets.Rocket.RocketType;

import com.badlogic.gdx.math.Vector2;

public class Miner extends MovingEnemy {
	
	private static final float SPEED = 2f;
	private static final float RELOAD = 3f;
	
	public Miner(Vector2 pos, World w) {
		super(pos, EnemyType.MINER, w, RocketType.MINE, SPEED, RELOAD);
	}

}
