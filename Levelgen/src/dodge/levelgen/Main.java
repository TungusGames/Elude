package dodge.levelgen;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import tungus.games.dodge.game.enemies.Enemy.EnemyType;
import tungus.games.dodge.game.level.FiniteLevelLoader.Level;
import tungus.games.dodge.game.level.FiniteLevelLoader.Wave;

public class Main {

	public static Deque<Wave> waves = new ArrayDeque<Wave>();
	private static float hpDrop = 0.1f;
	private static float speedDrop = 0.1f;
	private static float wipeDrop = 0.1f;
	
	public static void main(String[] args) {
		level3();
		
		FileOutputStream fileOut = null;
		Level lvl = new Level();
		lvl.waves = waves;
		lvl.hpDropByEnemy = hpDrop;
		lvl.speedDropByEnemy = speedDrop;
		lvl.rocketWipeDropByEnemy = wipeDrop;
		try {
			fileOut = new FileOutputStream("3.lvl");
			ObjectOutputStream out;
			out = new ObjectOutputStream(fileOut);
			out.writeObject(lvl);
			out.close();
		} catch (IOException e11) {
			e11.printStackTrace();
		}
	}

	public static void level1() {
		for (int i = 0; i < 5; i++) {
			ArrayList<EnemyType> e = new ArrayList<EnemyType>();
			for (int j = 0; j <= i; j++) {
				e.add(EnemyType.STANDING);
			}
			Wave w = new Wave(-1, 0, e);
			waves.add(w);
		}
		ArrayList<EnemyType> e = new ArrayList<EnemyType>();
		e.add(EnemyType.MOVING);
		Wave w = new Wave(-1, 0, e);
		waves.add(w);
		for (int i = 1; i < 3; i++) {
			ArrayList<EnemyType> e1 = new ArrayList<EnemyType>();
			for (int j = 0; j <= i; j++) {
				e1.add(EnemyType.MOVING);
			}
			Wave w1 = new Wave(5, 0, e1);
			waves.add(w1);
		}
		ArrayList<EnemyType> e1 = new ArrayList<EnemyType>();
		e1.add(EnemyType.STANDING);
		e1.add(EnemyType.MOVING);
		e1.add(EnemyType.STANDING);
		e1.add(EnemyType.MOVING);
		Wave w1 = new Wave(-1, 2, e1);
		waves.add(w1);
	}
	
	public static void level2() {
		for (int i = 0; i < 5; i++) {
			List<EnemyType> l = new ArrayList<EnemyType>();
			l.add(EnemyType.STANDING);
			l.add(EnemyType.MOVING);
			waves.add(new Wave(3, 0, l));
		}
		
		List<EnemyType> l = new ArrayList<EnemyType>();
		for (int i = 0; i < 8; i++)
			l.add(EnemyType.STANDING);
		waves.add(new Wave(-1, 0, l));
		
		l = new ArrayList<EnemyType>();
		for (int i = 0; i < 8; i++)
			l.add(EnemyType.MOVING);
		waves.add(new Wave(-1, 0, l));
		
		l = new ArrayList<EnemyType>();
		for (int i = 0; i < 8; i++) {
			l.add(EnemyType.STANDING);
			l.add(EnemyType.MOVING);
		}
		waves.add(new Wave(-1, 0, l));
	}
	
	private static void level3() {
		ArrayList<EnemyType> e = new ArrayList<EnemyType>();
		e.add(EnemyType.MOVING);
		e.add(EnemyType.MOVING);
		e.add(EnemyType.MOVING);
		Wave w = new Wave(-1, 0, e);
		waves.add(w);
		
		e = new ArrayList<EnemyType>();
		e.add(EnemyType.STANDING);
		e.add(EnemyType.STANDING);
		e.add(EnemyType.MOVING);
		e.add(EnemyType.MOVING);
		w = new Wave(7, 0, e);
		waves.add(w);
		
		e = new ArrayList<EnemyType>();
		e.add(EnemyType.KAMIKAZE);
		e.add(EnemyType.KAMIKAZE);
		w = new Wave(7, 3, e);
		waves.add(w);
		
		e = new ArrayList<EnemyType>();
		for (int i = 0; i < 4; i++)
			e.add(EnemyType.MOVING);
		w = new Wave(3, 0, e);
		waves.add(w);
		
		e = new ArrayList<EnemyType>();
		for (int i = 0; i < 8; i++)
			e.add(EnemyType.KAMIKAZE);
		w = new Wave(-1, 0, e);
		waves.add(w);
		
		e = new ArrayList<EnemyType>();
		for (int i = 0; i < 5; i++) {
			e.add(EnemyType.KAMIKAZE);
			e.add(EnemyType.MOVING);
			e.add(EnemyType.STANDING);
		}
			
		w = new Wave(-1, 3, e);
		waves.add(w);
	}
}
