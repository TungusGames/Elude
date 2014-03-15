package tungus.games.elude.game.level;

import tungus.games.elude.Assets;
import tungus.games.elude.game.World;
import tungus.games.elude.game.enemies.Enemy;
import tungus.games.elude.game.level.FiniteLevelLoader.Level;
import tungus.games.elude.game.level.arcade.OneDeadTwoCome;
import tungus.games.elude.game.pickups.HealthPickup;
import tungus.games.elude.game.pickups.RocketKillerPickup;
import tungus.games.elude.game.pickups.SpeedPickup;

import com.badlogic.gdx.math.MathUtils;

public abstract class EnemyLoader {
	protected final World world;
	protected final float hpChance;
	protected final float speedChance;
	protected final float wipeChance;
	
	public static EnemyLoader loaderFromLevelNum(World world, int n) {
		if (n <= 40)
			return new FiniteLevelLoader(Level.levelFromFile(Assets.levelFile(n)), world);
		else
			return new OneDeadTwoCome(world);
	}
	
	protected EnemyLoader(World w) {
		this(w, 0, 0, 0);
	}
	
	protected EnemyLoader(World w, float hpChance, float speedChance, float wipeChance) {
		this.world = w;
		this.hpChance = hpChance;
		this.speedChance = speedChance;
		this.wipeChance = wipeChance;
	}
	
	public void update(float deltaTime) {}
	
	public void onEnemyDead(Enemy e) {
		float rand = MathUtils.random();
		if (rand < hpChance)
			world.pickups.add(new HealthPickup(world, e.pos));
		else if ((rand -= hpChance) < speedChance)
			world.pickups.add(new SpeedPickup(world, e.pos));
		else if ((rand -= speedChance) < wipeChance)
			world.pickups.add(new RocketKillerPickup(world, e.pos));
	}
}
