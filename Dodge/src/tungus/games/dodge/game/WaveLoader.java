package tungus.games.dodge.game;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.List;

import tungus.games.dodge.game.enemies.Enemy;
import tungus.games.dodge.game.enemies.Enemy.EnemyType;

import com.badlogic.gdx.files.FileHandle;

public class WaveLoader {
	
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
	
	private ArrayDeque<Wave> waves;
	private final World world;
	private float time = 0;
	
	public WaveLoader(FileHandle file, World world) {
		try {
			waves = (ArrayDeque<Wave>)(new ObjectInputStream(file.read()).readObject());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.world = world;
	}
	
	public void update(float deltaTime) {
		time += deltaTime;
		Wave w = waves.peek();
		if (w.timeAfterLast < time || w.enemiesAfterLast <= world.enemies.size()) {
			time = Math.max(0, time-w.timeAfterLast);
			int size = w.enemies.size();
			for (int i = 0; i < size; i++) {
				world.enemies.add(Enemy.newEnemy(w.enemies.get(i)));
			}
			waves.removeFirst();
		}
	}
	
	

}
