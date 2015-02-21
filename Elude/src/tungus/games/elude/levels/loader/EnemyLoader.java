package tungus.games.elude.levels.loader;

import tungus.games.elude.Assets;
import tungus.games.elude.game.client.worldrender.renderable.Renderable;
import tungus.games.elude.game.server.Updatable;
import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.enemies.Enemy;
import tungus.games.elude.game.server.enemies.Enemy.EnemyType;
import tungus.games.elude.game.server.pickups.FreezerPickup;
import tungus.games.elude.game.server.pickups.HealthPickup;
import tungus.games.elude.game.server.pickups.ShieldPickup;
import tungus.games.elude.game.server.pickups.SpeedPickup;
import tungus.games.elude.levels.loader.FiniteLevelLoader.Level;
import tungus.games.elude.levels.loader.arcade.BalancedPlusPlus;
import tungus.games.elude.levels.loader.arcade.BossFun;
import tungus.games.elude.levels.loader.arcade.FillUp;
import tungus.games.elude.levels.loader.arcade.PlusPlus;

import com.badlogic.gdx.math.MathUtils;

public abstract class EnemyLoader extends Updatable {
	protected final World world;
	protected float hpChance, hpInc;
	protected float speedChance, speedInc;
	protected float freezerChance, freezerInc;
	protected float shieldChance, shieldInc;

	protected final int levelNum;
	protected float timeSinceStart;

	public static EnemyLoader loaderFromLevelNum(World world, int n, boolean finite) {
		if (finite)
			return new FiniteLevelLoader(Level.levelFromFile(Assets.levelFile(n+1)), world, n);
		else {
			switch(n) {
			case 0:
				return new FillUp(world, n, 10, 50f, EnemyType.MOVING, EnemyType.MOVING, EnemyType.STANDING, EnemyType.STANDING, EnemyType.KAMIKAZE);
			case 1:
				return new PlusPlus(world, n, 100f, EnemyType.KAMIKAZE);
			case 2:
				return new FillUp(world, n, 10, 30f, EnemyType.SHIELDED, EnemyType.KAMIKAZE);
			case 3:
				return new FillUp(world, n, 8, 50f, EnemyType.STANDING, EnemyType.KAMIKAZE, EnemyType.MINER);
			case 4:
				EnemyLoader e = new FillUp(world, n, 8, 60f, EnemyType.MINER, EnemyType.MINER, EnemyType.MOVING);
				e.shieldInc = 0.01f;
				return e;
			case 5:
				return new FillUp(world, n, 5, 30f, EnemyType.SPLITTER, EnemyType.KAMIKAZE, EnemyType.KAMIKAZE);
			case 6:
				return new FillUp(world, n, 6, 30f, EnemyType.KAMIKAZE, EnemyType.MINER);
			case 7:
				return new PlusPlus(world, n, 20f, EnemyType.SHARPSHOOTER, EnemyType.SHIELDED);
			case 8:				
				return new FillUp(world, n, 5, 20, EnemyType.SHARPSHOOTER);
			case 9:
				return new FillUp(world, n, 6, 40f, EnemyType.MACHINEGUNNER, EnemyType.SHARPSHOOTER);
			case 10:
				return new FillUp(world, n, 6, 30f, EnemyType.SHIELDED, EnemyType.MACHINEGUNNER, EnemyType.MINER);
			case 11:
				return new PlusPlus(world, n, 30f, EnemyType.FACTORY);
			case 12:
				return new FillUp(world, n, 6, 50f, EnemyType.FACTORY, EnemyType.MACHINEGUNNER, EnemyType.KAMIKAZE, EnemyType.SHARPSHOOTER);
			case 13:
				return new FillUp(world, n, 10, 50f, EnemyType.normalSpawners());
			case 14:
				return new BossFun(world, n);
			default:
				throw new IllegalArgumentException("Unknown arcade level number");

			}
		}

	}

	protected EnemyLoader(World w, int levelNum) {
		this(w, 0, 0, 0, 0, levelNum);
	}

	protected EnemyLoader(World w, float hpInc, float speedInc, float shieldInc, float freezerInc, int levelNum) {
		this.world = w;
		this.hpChance = this.hpInc = hpInc;
		this.speedChance = this.speedInc = speedInc;
		this.shieldChance = this.shieldInc = shieldInc;
		this.freezerChance = this.freezerInc = freezerInc;
		this.levelNum = levelNum;

	}

	public boolean update(float deltaTime) { 
		timeSinceStart += deltaTime;
		return false;
	}

	public void onEnemyDead(Enemy e) {
		boolean spawning = false;
		if (MathUtils.random() < hpChance) {
			world.addNextFrame.add(new HealthPickup(world, e.pos));
			hpChance = hpInc;
			spawning = true;
		} else {
			hpChance += hpInc;
		}

		if (MathUtils.random() < speedChance) {
			if (!spawning) {
				world.addNextFrame.add(new SpeedPickup(world, e.pos));
				speedChance = speedInc;
				spawning = true;
			} else {
				speedChance = 1;
			}			
		} else {
			speedChance += speedInc;
		}

		if (MathUtils.random() < shieldChance) {
			if (!spawning) {
				world.addNextFrame.add(new ShieldPickup(world, e.pos));
				shieldChance = shieldInc;
				spawning = true;
			} else {
				shieldChance = 1;
			}
		} else {
			shieldChance += shieldInc;
		}

		if (MathUtils.random() < freezerChance) {
			if (!spawning) {
				world.addNextFrame.add(new FreezerPickup(world, e.pos));
				freezerChance = freezerInc;
				spawning = true;
			} else {
				freezerChance = 1;
			}			
		} else {
			freezerChance += freezerInc;
		}

	}

	public void onEnemyHurt(Enemy e, float dmg) {}

	@Override
	public Renderable getRenderable() {
		return null;
	}

	public abstract void saveScore();
	public abstract String levelName();
}
