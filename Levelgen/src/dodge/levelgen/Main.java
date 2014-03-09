package dodge.levelgen;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

import tungus.games.dodge.game.enemies.Enemy.EnemyType;
import tungus.games.dodge.game.level.FiniteLevel.Wave;

public class Main {

	public static Deque<Wave> waves = new ArrayDeque<Wave>();
	
	public static void main(String[] args) {
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
		FileOutputStream fileOut = null;
		try {
			fileOut = new FileOutputStream("level.lvl");
			ObjectOutputStream out;
			out = new ObjectOutputStream(fileOut);
			out.writeObject(waves);
			out.close();
		} catch (IOException e11) {
			e11.printStackTrace();
		}
	}

}
