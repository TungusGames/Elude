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
import tungus.games.elude.game.server.enemies.Splitter;
import tungus.games.elude.game.server.pickups.Pickup.PickupType;
import tungus.games.elude.levels.loader.FiniteLevelLoader.Level;
import tungus.games.elude.levels.loader.FiniteLevelLoader.Wave;
import tungus.games.elude.levels.scoredata.ScoreData.ArcadeLevelScore;
import tungus.games.elude.levels.scoredata.ScoreData.FiniteLevelScore;

public class Main {

	public static Deque<Wave> waves = new ArrayDeque<Wave>();
	
	private static float hpDrop;
	private static float speedDrop;
	private static float freezerDrop;
	private static float shieldDrop;
	
	private static int levelNum = 1;
	
	private static boolean running = true;
		
	private static Scanner sc;
	
	
	public static void main(String[] args) {
		ChanceIncreaseCalculator.load();
		readAndOutputLevels();
		writeArcadeMedals();
	}
	
	
	public static void readAndOutputLevels() {
		List<FiniteLevelScore> scores = new ArrayList<>();
		List<Integer> ntoidList = new ArrayList<>();
		String name = null;
		while (running) {
			float totalEnemyHP = 0;
			try {
				System.out.print("Reading file: " + (levelNum) + ".tel ");
				sc = new Scanner(new File((levelNum) + ".tel"));
				sc.useLocale(Locale.US);
				ntoidList.add(sc.nextInt());
				sc.nextLine();
				name = sc.nextLine();
				System.out.print("(" + name + ") ");
				
				scores.add(new FiniteLevelScore(sc.nextFloat(), sc.nextFloat()));				
				
				hpDrop = sc.nextFloat();
				speedDrop = sc.nextFloat();
				shieldDrop = sc.nextFloat();
				freezerDrop = sc.nextFloat();
				
				while (!sc.next().equals("wavestart"))
					;
				float t = sc.nextInt();
				int n = sc.nextInt();
				List<EnemyType> e = new ArrayList<>();
				List<PickupType> p = new ArrayList<>();
				while (sc.hasNext()) {
					int mul = sc.hasNextInt() ? sc.nextInt() : 1;
					String str = sc.next();
					try {
						EnemyType et = EnemyType.valueOf(str.toUpperCase());
						for (int i = 0; i < mul; i++) {
							e.add(et);
							if (et != EnemyType.SPLITTER) {
								totalEnemyHP += et.hp;
							} else {
								totalEnemyHP += Splitter.totalHP();
							}
							
						}						
					} catch (IllegalArgumentException ex) {
						try {
							PickupType pt = PickupType.valueOf(str.toUpperCase());
							for (int i = 0; i < mul; i++) {
								p.add(pt);
							}
						} catch (IllegalArgumentException ex2) {
							if (str.equals("wavestart")) {
								waves.add(new Wave(t, n, e, p));
								t = sc.nextFloat();
								n = sc.nextInt();
								e = new ArrayList<>();
								p = new ArrayList<>();
							}
						}
					}
				}
				waves.add(new Wave(t, n, e, p));
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
			lvl.name = name.toUpperCase();
			lvl.waves = new Wave[waves.size()];
			lvl.waves = waves.toArray(lvl.waves);
			lvl.hpInc = ChanceIncreaseCalculator.incForAverage(hpDrop);
			lvl.speedInc = ChanceIncreaseCalculator.incForAverage(speedDrop);
			lvl.freezerInc = ChanceIncreaseCalculator.incForAverage(freezerDrop);
			lvl.shieldInc = ChanceIncreaseCalculator.incForAverage(shieldDrop);
			lvl.totalEnemyHP = totalEnemyHP;
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
		// Turn ntoid Integer-list to int array
		int ntoidArray[] = new int[ntoidList.size()];
		for (int i = 0; i < ntoidArray.length; i++) {
			ntoidArray[i] = ntoidList.get(i);
		}
		// Output score file
		try {
			FileOutputStream fileOut = new FileOutputStream("../Elude - Android/assets/medals/finite.medal");
			ObjectOutputStream out;
			out = new ObjectOutputStream(fileOut);
			out.writeObject(scores);
			out.close();
			fileOut = new FileOutputStream("../Elude - Android/assets/levels/finitentoid");
			out = new ObjectOutputStream(fileOut);
			out.writeObject(ntoidArray);
			out.close();
		} catch (IOException e11) {
			e11.printStackTrace();
		}
	}
	
	public static void writeArcadeMedals() {
		List<ArcadeLevelScore> list = new ArrayList<>();
		try {
			sc = new Scanner(new File("arcadeinfo.tel"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("No arcade info file found!"); 			
		}
		sc.useLocale(Locale.US);
		List<Integer> ntoidList = new ArrayList<Integer>();
		while(sc.hasNext()) {
			ntoidList.add(sc.nextInt());
			list.add(new ArcadeLevelScore(sc.nextFloat(), sc.nextInt()));
		}
		int ntoidArray[] = new int[ntoidList.size()];
		for (int i = 0; i < ntoidArray.length; i++) {
			ntoidArray[i] = ntoidList.get(i);
		}
		try {
			FileOutputStream fileOut = new FileOutputStream("../Elude - Android/assets/medals/arcade.medal");
			ObjectOutputStream out;
			out = new ObjectOutputStream(fileOut);
			out.writeObject(list);
			out.close();
			fileOut = new FileOutputStream("../Elude - Android/assets/levels/arcadentoid");
			out = new ObjectOutputStream(fileOut);
			out.writeObject(ntoidArray);
			out.close();
		} catch (IOException e11) {
			e11.printStackTrace();
		}
	}
}
