package tungus.games.elude.levels.loader;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Deque;
import java.util.List;

import tungus.games.elude.game.World;
import tungus.games.elude.game.enemies.Enemy;
import tungus.games.elude.game.enemies.Enemy.EnemyType;
import tungus.games.elude.levels.scoredata.ScoreData;
import tungus.games.elude.levels.scoredata.ScoreData.FiniteLevelScore;

import com.badlogic.gdx.files.FileHandle;

public class FiniteLevelLoader extends EnemyLoader {
	
	public static class Wave implements Serializable {

		private static final long serialVersionUID = 636151966112895918L;
		public final float timeAfterLast;	//Triggers when more than X time has passed
		public final int enemiesAfterLast;	//Or only Y enemies remain
		public final List<EnemyType> enemies;
		public Wave(float time, int enemyCount, List<EnemyType> enemyList) {
			timeAfterLast = time;
			enemiesAfterLast = enemyCount;
			enemies = enemyList;
		}
	}
	
	public static class Level implements Serializable {

		private static final long serialVersionUID = 3972235095607047708L;
		public Deque<Wave> waves;
		public float hpChance;
		public float speedChance;
		public float rocketWipeChance;
		public float shieldChance;
		
		public static Level levelFromFile(FileHandle file) {
			try {
				Level level = (Level)(new ObjectInputStream(file.read()).readObject());
				return level;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new RuntimeException("Badly serialized level file " + file.path(), e);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("Failed to read level file " + file.path(), e);
			}
		}
	}
	
	private Level level;
	private float timeSinceLastWave = 0;
	public float hpLost = 0;
	

	public FiniteLevelLoader(Level level, World world, int levelNum) {
		super(world, level.hpChance, level.speedChance, level.rocketWipeChance, level.shieldChance, levelNum);
		this.level = level;
		Wave w = level.waves.removeFirst();
		int size = w.enemies.size();
		for (int i = 0; i < size; i++) {
			world.enemies.add(Enemy.newEnemy(world, w.enemies.get(i)));
		}
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		timeSinceLastWave += deltaTime;
		Wave w = level.waves.peek();
		if (w != null && ((w.timeAfterLast < timeSinceLastWave && w.timeAfterLast != -1f) || w.enemiesAfterLast >= world.enemies.size())) {
			timeSinceLastWave = 0;
			int size = w.enemies.size();
			for (int i = 0; i < size; i++) {
				world.enemies.add(Enemy.newEnemy(world, w.enemies.get(i)));
			}
			level.waves.removeFirst();
		}
	}
	
	@Override
	public void saveScore() {
		FiniteLevelScore score = ScoreData.playerFiniteScore.get(levelNum);
		score.hpLost = score.completed ? Math.min(score.hpLost, hpLost) : hpLost;
		score.timeTaken = score.completed ? Math.min(score.timeTaken, timeSinceStart) : timeSinceStart;
		score.completed = true;
		ScoreData.save(true);
	}
}
