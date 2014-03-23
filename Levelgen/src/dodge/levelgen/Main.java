package dodge.levelgen;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import tungus.games.elude.game.enemies.Enemy.EnemyType;
import tungus.games.elude.levels.loader.FiniteLevelLoader.Level;
import tungus.games.elude.levels.loader.FiniteLevelLoader.Wave;
import tungus.games.elude.levels.scoredata.ScoreData.ArcadeLevelScore;
import tungus.games.elude.levels.scoredata.ScoreData.FiniteLevelScore;

public class Main {

	public static Deque<Wave> waves = new ArrayDeque<Wave>();
	private static float hpDrop = 0.1f;
	private static float speedDrop = 0.1f;
	private static float wipeDrop = 0.1f;
	private static float shieldDrop = 0.1f;
	
	private static int levelOffset = 0;
	
	public static void main(String[] args) {
		/*for (levelOffset = 0; levelOffset < 50; levelOffset += 3) {
			level1();
			level2();
			level3();
		}*/
		level4();
		/*level1();
		level2();
		level3();*/
		//writeFiniteMedals();
		writeArcadeMedals();
	}
	
	public static void writeFiniteMedals() {
		List<FiniteLevelScore[]> list = new ArrayList<>();
		for (int i = 0; i < 50; i++) {
			FiniteLevelScore[] medals = new FiniteLevelScore[3];
			for (int j = 0; j < 3; j++) {
				medals[j] = new FiniteLevelScore();
				medals[j].completed = true;
				medals[j].timeTaken = 180 - 60*j;
				medals[j].hpLost = 150 - 50*j;
			}
			list.add(medals);
		}
		try {
			FileOutputStream fileOut = new FileOutputStream("finite.score");
			ObjectOutputStream out;
			out = new ObjectOutputStream(fileOut);
			out.writeObject(list);
			out.close();
		} catch (IOException e11) {
			e11.printStackTrace();
		}
	}
	
	public static void writeArcadeMedals() {
		List<ArcadeLevelScore[]> list = new ArrayList<>();
		for (int i = 0; i < 15; i++) {
			ArcadeLevelScore[] medals = new ArcadeLevelScore[3];
			for (int j = 0; j < 3; j++) {
				medals[j] = new ArcadeLevelScore();
				medals[j].tried = true;
				medals[j].timeSurvived = 60*j;
				medals[j].enemiesKilled = 15*(j+1);
			}
			list.add(medals);
		}
		try {
			FileOutputStream fileOut = new FileOutputStream("arcade.score");
			ObjectOutputStream out;
			out = new ObjectOutputStream(fileOut);
			out.writeObject(list);
			out.close();
		} catch (IOException e11) {
			e11.printStackTrace();
		}
	}
	
	public static void outputLevel(int num) {
		FileOutputStream fileOut = null;
		Level lvl = new Level();
		lvl.waves = waves;
		lvl.hpChance = hpDrop;
		lvl.speedChance = speedDrop;
		lvl.rocketWipeChance = wipeDrop;
		lvl.shieldChance = shieldDrop;
		try {
			fileOut = new FileOutputStream(num+levelOffset + ".lvl");
			ObjectOutputStream out;
			out = new ObjectOutputStream(fileOut);
			out.writeObject(lvl);
			out.close();
		} catch (IOException e11) {
			e11.printStackTrace();
		}
		waves.clear();
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
		outputLevel(1);
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
		outputLevel(2);
	}
	
	public static void level3() {
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
		outputLevel(3);
	}

	public static void level4() {
		ArrayList<EnemyType> e = new ArrayList<>();
		e.add(EnemyType.MOVING);
		e.add(EnemyType.MOVING);
		e.add(EnemyType.MOVING);
		Wave w = new Wave(-1, 0, e);
		waves.add(w);
		
		e = new ArrayList<>();
		e.add(EnemyType.MOVING);
		e.add(EnemyType.MOVING);
		e.add(EnemyType.MOVING);
		waves.add(new Wave(10, 2, e));
		
		e = new ArrayList<>();
		e.add(EnemyType.STANDING_FAST);
		waves.add(new Wave(-1, 0, e));
		
		e = new ArrayList<>();
		e.add(EnemyType.STANDING_FAST);
		e.add(EnemyType.STANDING_FAST);
		e.add(EnemyType.STANDING_FAST);
		waves.add(new Wave(-1, 0, e));
		
		e = new ArrayList<>();
		e.add(EnemyType.MOVING_MATRIX);
		waves.add(new Wave(-1, 0, e));
		
		e = new ArrayList<>();
		e.add(EnemyType.MOVING_MATRIX);
		e.add(EnemyType.MOVING_MATRIX);
		e.add(EnemyType.MOVING_MATRIX);
		waves.add(new Wave(-1, 0, e));
		
		e = new ArrayList<>();
		e.add(EnemyType.MOVING_MATRIX);
		e.add(EnemyType.MOVING_MATRIX);
		e.add(EnemyType.STANDING_FAST);
		e.add(EnemyType.STANDING_FAST);
		waves.add(new Wave(-1, 0, e));
		
		e = new ArrayList<>();
		e.add(EnemyType.MOVING_MATRIX);
		e.add(EnemyType.STANDING_FAST);
		waves.add(new Wave(8, 2, e));
		
		e = new ArrayList<>();
		e.add(EnemyType.MOVING_MATRIX);
		e.add(EnemyType.STANDING_FAST);
		waves.add(new Wave(8, 2, e));
		
		e = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			e.add(EnemyType.STANDING);
			e.add(EnemyType.STANDING_FAST);
			e.add(EnemyType.MOVING);
			e.add(EnemyType.MOVING_MATRIX);
		}
		waves.add(new Wave(-1, 0, e));
		
		outputLevel(4);
	}
}
