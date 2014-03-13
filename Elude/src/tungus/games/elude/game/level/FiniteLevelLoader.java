package tungus.games.elude.game.level;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Deque;
import java.util.List;

import tungus.games.elude.game.World;
import tungus.games.elude.game.enemies.Enemy;
import tungus.games.elude.game.enemies.Enemy.EnemyType;

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
		public float hpDropByEnemy;
		public float speedDropByEnemy;
		public float rocketWipeDropByEnemy;
		
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
	private float time = 0;
	
	public FiniteLevelLoader(Level level, World world) {
		super(world, level.hpDropByEnemy, level.speedDropByEnemy, level.rocketWipeDropByEnemy);
		this.level = level;
		Wave w = level.waves.removeFirst();
		int size = w.enemies.size();
		for (int i = 0; i < size; i++) {
			world.enemies.add(Enemy.newEnemy(world, w.enemies.get(i)));
		}
	}
	
	@Override
	public void update(float deltaTime) {
		time += deltaTime;
		Wave w = level.waves.peek();
		if (w != null && ((w.timeAfterLast < time && w.timeAfterLast != -1f) || w.enemiesAfterLast >= world.enemies.size())) {
			time = 0;
			int size = w.enemies.size();
			for (int i = 0; i < size; i++) {
				world.enemies.add(Enemy.newEnemy(world, w.enemies.get(i)));
			}
			level.waves.removeFirst();
		}
	}
}
