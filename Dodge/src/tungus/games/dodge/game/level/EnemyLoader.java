package tungus.games.dodge.game.level;

import tungus.games.dodge.game.World;
import tungus.games.dodge.game.enemies.Enemy;

public abstract class EnemyLoader {
	protected final World world;
	
	protected EnemyLoader(World w) {
		world = w;
	}
	
	public void update(float deltaTime) {}
	public void onEnemyDead(Enemy e) {}
}
