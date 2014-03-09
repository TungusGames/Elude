package tungus.games.dodge.game.level;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Deque;
import java.util.List;

import tungus.games.dodge.game.World;
import tungus.games.dodge.game.enemies.Enemy;
import tungus.games.dodge.game.enemies.Enemy.EnemyType;

import com.badlogic.gdx.files.FileHandle;

public class FiniteLevel extends EnemyLoader {
	
	public static class Wave implements Serializable {
		public final float timeAfterLast;	//Triggers when more than X time has passed
		public final int enemiesAfterLast;	//Or only Y enemies remain
		public final List<EnemyType> enemies;
		public Wave(float time, int enemyCount, List<EnemyType> enemyList) {
			timeAfterLast = time;
			enemiesAfterLast = enemyCount;
			enemies = enemyList;
		}
	}
	
	private Deque<Wave> waves;
	private float time = 0;
	
	public FiniteLevel(FileHandle file, World world) {
		super(world);
		try {
			waves = (Deque<Wave>)(new ObjectInputStream(file.read()).readObject());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Badly serialized level file " + file.path(), e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to read level file " + file.path(), e);
		}
		Wave w = waves.removeFirst();
		int size = w.enemies.size();
		for (int i = 0; i < size; i++) {
			world.enemies.add(Enemy.newEnemy(world, w.enemies.get(i)));
		}
	}
	
	public void update(float deltaTime) {
		time += deltaTime;
		Wave w = waves.peek();
		if (w != null && ((w.timeAfterLast < time && w.timeAfterLast != -1f) || w.enemiesAfterLast >= world.enemies.size())) {
			time = 0;
			int size = w.enemies.size();
			for (int i = 0; i < size; i++) {
				world.enemies.add(Enemy.newEnemy(world, w.enemies.get(i)));
			}
			waves.removeFirst();
		}
	}
	
	

}
