package elude.levelgen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import tungus.games.elude.game.server.enemies.Enemy.EnemyType;
import tungus.games.elude.game.server.pickups.Pickup.PickupType;
import tungus.games.elude.levels.loader.FiniteLevelLoader.Level;
import tungus.games.elude.levels.loader.FiniteLevelLoader.Wave;
import tungus.games.elude.levels.scoredata.ScoreData.ArcadeLevelScore;
import tungus.games.elude.levels.scoredata.ScoreData.FiniteLevelScore;

public class Main {

	public static Deque<Wave> waves = new ArrayDeque<Wave>();
	
	private static float hpDrop;
	private static float speedDrop;
	private static float wipeDrop;
	private static float shieldDrop;
	
	private static int levelNum = 1;
	
	private static boolean running = true;
		
	private static Scanner sc;
	
	
	public static void main(String[] args) {
		readAndOutputLevels();
		writeArcadeMedals();
	}
	
	
	public static void readAndOutputLevels() {
		List<FiniteLevelScore> scores = new ArrayList<>();
		while (running) {
			try {
				System.out.print("Reading file: " + (levelNum) + ".tel ");
				sc = new Scanner(new File((levelNum) + ".tel"));
				sc.useLocale(Locale.US);
				scores.add(new FiniteLevelScore(sc.nextFloat(), sc.nextFloat()));
				hpDrop = sc.nextFloat();
				speedDrop = sc.nextFloat();
				wipeDrop = sc.nextFloat();
				shieldDrop = sc.nextFloat();
				while (sc.hasNext()) {
					if (sc.next().equals("wavestart")) {
						int t = sc.nextInt();
						int n = sc.nextInt();
						List<EnemyType> e = new ArrayList<>();
						List<PickupType> p = new ArrayList<>();
						outer: while (true)
							switch (sc.next()) {
								case "standing":
									e.add(EnemyType.STANDING);
									break;
								case "moving":
									e.add(EnemyType.MOVING);
									break;
								case "kamikaze":
									e.add(EnemyType.KAMIKAZE);
									break;
								case "standing_fast":
									e.add(EnemyType.STANDING_FAST);
									break;
								case "moving_matrix":
									e.add(EnemyType.MOVING_MATRIX);
									break;
								case "health":
									p.add(PickupType.HEALTH);
									break;
								case "speed":
									p.add(PickupType.SPEED);
									break;
								case "shield":
									p.add(PickupType.SHIELD);
									break;
								case "rocketwipe":
									p.add(PickupType.ROCKETWIPER);
									break;
								case "end":
									break outer; //Breaks from the outer loop...
								default:
									break;
							}
						waves.add(new Wave(t, n, e, p));
					 }
				}
				System.out.println("finished");
				System.out.println(waves.size() + " wave(s) found");
			} catch (FileNotFoundException e) {
				System.out.println();
				System.out.println("File not found: " + (levelNum) + ".tel, finished at this file: " + (levelNum-1)  + ".tel");
				running = false;
				break;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			FileOutputStream fileOut = null;
			Level lvl = new Level();
			lvl.waves = waves;
			lvl.hpChance = hpDrop;
			lvl.speedChance = speedDrop;
			lvl.rocketWipeChance = wipeDrop;
			lvl.shieldChance = shieldDrop;
			try {
				System.out.print("Writing file: " + (levelNum) + ".lvl ");
				fileOut = new FileOutputStream("../Elude - Android/assets/levels/" + (levelNum) + ".lvl");
				ObjectOutputStream out;
				out = new ObjectOutputStream(fileOut);
				out.writeObject(lvl);
				out.close();
				System.out.println("finished");
			} catch (IOException e) {
				e.printStackTrace();
			}
			waves.clear();
			levelNum++;
		}
		// Fill up scores to 50 levels
		for (int i = levelNum; i <= 50; i++)
			scores.add(new FiniteLevelScore(150, 50));
		// Output score file
		try {
			FileOutputStream fileOut = new FileOutputStream("../Elude - Android/assets/medals/finite.medal");
			ObjectOutputStream out;
			out = new ObjectOutputStream(fileOut);
			out.writeObject(scores);
			out.close();
		} catch (IOException e11) {
			e11.printStackTrace();
		}
	}
	
	
	
	@Deprecated
	public static void writeFiniteMedals() {
		List<FiniteLevelScore> list = new ArrayList<>();
		FiniteLevelScore medal = new FiniteLevelScore();
		medal.completed = true;
		medal.timeTaken = 150;
		medal.hpLost = 50;
		for (int i = 0; i < 50; i++) {
			list.add(medal);
		}
		try {
			FileOutputStream fileOut = new FileOutputStream("../Elude - Android/assets/medals/finite.medal");
			ObjectOutputStream out;
			out = new ObjectOutputStream(fileOut);
			out.writeObject(list);
			out.close();
		} catch (IOException e11) {
			e11.printStackTrace();
		}
	}
	
	public static void writeArcadeMedals() {
		List<ArcadeLevelScore> list = new ArrayList<>();
		ArcadeLevelScore medal = new ArcadeLevelScore();
		medal.tried = true;
		medal.timeSurvived = 150;
		medal.enemiesKilled = 40;
		for (int i = 0; i < 15; i++) {
			list.add(medal);
		}
		try {
			FileOutputStream fileOut = new FileOutputStream("../Elude - Android/assets/medals/arcade.medal");
			ObjectOutputStream out;
			out = new ObjectOutputStream(fileOut);
			out.writeObject(list);
			out.close();
		} catch (IOException e11) {
			e11.printStackTrace();
		}
	}
	
	/*@Deprecated
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

	@Deprecated
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
	
	@Deprecated
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
	
	@Deprecated
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

	@Deprecated
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
	}*/
}
