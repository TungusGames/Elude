package tungus.games.elude.levels.loader;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.List;

import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.enemies.Enemy;
import tungus.games.elude.game.server.enemies.Enemy.EnemyType;
import tungus.games.elude.game.server.pickups.Pickup;
import tungus.games.elude.game.server.pickups.Pickup.PickupType;
import tungus.games.elude.levels.scoredata.ScoreData;
import tungus.games.elude.levels.scoredata.ScoreData.FiniteLevelScore;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class FiniteLevelLoader extends EnemyLoader {
	
	public static class Wave implements Serializable {

		private static final long serialVersionUID = 636151966112895918L;
		public final float timeAfterLast;	//Triggers when more than X time has passed
		public final int enemiesAfterLast;	//Or only Y enemies remain
		public final List<EnemyType> enemies;
		public final List<PickupType> pickups;
		public Wave(float time, int enemyCount, List<EnemyType> enemyList, List<PickupType> pickupList) {
			timeAfterLast = time;
			enemiesAfterLast = enemyCount;
			enemies = enemyList;
			pickups = pickupList;
		}
	}
	
	public static class Level implements Serializable {

		private static final long serialVersionUID = 3972235095607047708L;
		public Wave[] waves;
		public String name;
		public float hpInc;
		public float speedInc;
		public float freezerInc;
		public float shieldInc;
		public float totalEnemyHP;
		
		public static Level levelFromFile(FileHandle file) {
			try {
				ObjectInputStream o = new ObjectInputStream(file.read());
				Gdx.app.log("AVAILABLE: ", ""+o.available());
				Level level = (Level)(o.readObject());
				return level;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new RuntimeException("Badly serialized level file " + file.path(), e);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("Failed to read level file " + file.path(), e);
			}			
		}
		
		public Level() {
			
		}
	}
	
	private Level level;
	private float timeSinceLastWave = 0;
	private boolean completed = false;
	private float enemiesHpTaken = 0;
	
	private int nextWave = 0;

	public FiniteLevelLoader(Level level, World world, int levelNum) {
		super(world, level.hpInc, level.speedInc, level.shieldInc, level.freezerInc, levelNum);
		this.level = level;
		keepsWorldGoing = true;
		
	}
	
	public String levelName() {
		return "STAGE " + (levelNum+1) + "\n" + level.name;		
	}
	
	@Override
	public boolean update(float deltaTime) {
		super.update(deltaTime);
		timeSinceLastWave += deltaTime;
		if (nextWave >= level.waves.length) {
                    keepsWorldGoing = false;
                    return false;
		}
		Wave w = level.waves[nextWave];
		if (w != null && ((w.timeAfterLast < timeSinceLastWave && w.timeAfterLast != -1f) || w.enemiesAfterLast >= world.enemyCount)) {
			timeSinceLastWave = 0;
			int size = w.enemies.size();
			for (int i = 0; i < size; i++) {
				world.addEnemy(Enemy.fromType(world, w.enemies.get(i)));
			}
			size = w.pickups.size();
			for (int i = 0; i < size; i++) {
				world.addNextFrame.add(Pickup.fromType(world, w.pickups.get(i)));
			}
			nextWave++;
		}
		return false;
	}
	
	@Override
	public void saveScore() {
		FiniteLevelScore score = ScoreData.playerFiniteScore.get(levelNum);
                float newHP = world.vessels.get(0).hp;
                boolean save = false;
                
                int medalsBefore = ((score.completed ? 1 : 0) + (ScoreData.hasMedal(true, true, levelNum) ? 1 : 0) + (ScoreData.hasMedal(true, false, levelNum) ? 1 : 0));
                if (!score.completed || score.hpLeft < newHP) {
                    score.hpLeft = newHP;
                    save = true;
                }
                if (!score.completed || score.timeTaken > timeSinceStart) {
                    score.timeTaken = timeSinceStart;
                    save = true;
                }
		score.completed = true;
		this.completed = true;
                if (save) {
                    ScoreData.save(true);
                    int medalsAfter = ((score.completed ? 1 : 0) + (ScoreData.hasMedal(true, true, levelNum) ? 1 : 0) + (ScoreData.hasMedal(true, false, levelNum) ? 1 : 0));
                    ScoreData.starsEarned += (medalsAfter-medalsBefore);
                }		
	}
	
	public FiniteLevelScore getScore() {
		FiniteLevelScore s = new FiniteLevelScore();
		s.completed = this.completed;
		s.hpLeft = world.vessels.get(0).hp;
		s.timeTaken = timeSinceStart;
		return s;
	}
	
	public float progress() {
		return (float)enemiesHpTaken / level.totalEnemyHP;
	}
	
	@Override
	public void onEnemyHurt(Enemy e, float dmg) {
		super.onEnemyHurt(e, dmg);
		if (e.countsForProgress) {
			enemiesHpTaken += dmg;
		}
	}
}
