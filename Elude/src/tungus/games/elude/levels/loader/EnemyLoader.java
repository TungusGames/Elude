package tungus.games.elude.levels.loader;

import tungus.games.elude.Assets;
import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.enemies.Enemy;
import tungus.games.elude.game.server.enemies.Enemy.EnemyType;
import tungus.games.elude.game.server.pickups.HealthPickup;
import tungus.games.elude.game.server.pickups.RocketWiperPickup;
import tungus.games.elude.game.server.pickups.ShieldPickup;
import tungus.games.elude.game.server.pickups.SpeedPickup;
import tungus.games.elude.levels.loader.FiniteLevelLoader.Level;
import tungus.games.elude.levels.loader.arcade.FillUp;
import tungus.games.elude.levels.loader.arcade.PlusPlus;

import com.badlogic.gdx.math.MathUtils;

public abstract class EnemyLoader {
	protected final World world;
	protected final float hpChance;
	protected final float speedChance;
	protected final float wipeChance;
	protected final float shieldChance;
	
	protected final int levelNum;
	protected float timeSinceStart;
	
	public static EnemyLoader loaderFromLevelNum(World world, int n, boolean finite) {
		if (finite)
			return new FiniteLevelLoader(Level.levelFromFile(Assets.levelFile(n+1)), world, n);
		else {
			switch(n) {
			case 0:
				return new FillUp(world, n, 10, 0.5f, EnemyType.MOVING, EnemyType.MOVING, EnemyType.STANDING, EnemyType.STANDING, EnemyType.STANDING_FAST);
			case 1:
				return new FillUp(world, n, 10, 0.5f, EnemyType.values());
			case 2:
				return new FillUp(world, n, 10, 0.5f, 0, 0.2f, 0, 0, EnemyType.STANDING_FAST, EnemyType.KAMIKAZE);
			case 3:
				return new PlusPlus(world, n, EnemyType.MOVING, EnemyType.STANDING_FAST);
			case 4:
				return new FillUp(world, n, 10, 0.5f, EnemyType.values());
			default:
				throw new IllegalArgumentException("Unknown arcade level number");
				
			}
		}
			
	}
	
	protected EnemyLoader(World w, int levelNum) {
		this(w, 0, 0, 0, 0, levelNum);
	}
	
	protected EnemyLoader(World w, float hpChance, float speedChance, float wipeChance, float shieldChance, int levelNum) {
		this.world = w;
		this.hpChance = hpChance;
		this.speedChance = speedChance;
		this.wipeChance = wipeChance;
		this.levelNum = levelNum;
		this.shieldChance = shieldChance;
	}
	
	public void update(float deltaTime) { 
		timeSinceStart += deltaTime; 
	}
	
	public void onEnemyDead(Enemy e) {
		float rand = MathUtils.random();
		if (rand < hpChance)
			world.pickups.add(new HealthPickup(world, e.pos));
		else if ((rand -= hpChance) < speedChance)
			world.pickups.add(new SpeedPickup(world, e.pos));
		else if ((rand -= speedChance) < wipeChance)
			world.pickups.add(new RocketWiperPickup(world, e.pos));
		else if ((rand -= wipeChance) < shieldChance)
			world.pickups.add(new ShieldPickup(world, e.pos));
	}
	
	public abstract void saveScore();
	public abstract boolean isOver();
}
