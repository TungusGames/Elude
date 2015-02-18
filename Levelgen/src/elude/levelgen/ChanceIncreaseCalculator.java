package elude.levelgen;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;


public class ChanceIncreaseCalculator {
	private static final int ITERATIONS = 1_000_000;
	private static final double INC_STEP = 0.0001f;
	private static final double VALUE_STEP = 0.001f;
	private static final int VALUE_COUNT = (int)(1 / VALUE_STEP) + 1; // Should be castable without rounding
	private static final Random rand = new Random();

	private static final String filename = "chances.obj";
	/**
	 * Each element is the amount the chance should be increased on every miss for
	 * a certain average chance. The average chance increases by VALUE_STEP after
	 * each element.
	 * E.g. the first element is the increase needed for an average chance of 0, the
	 * second is the increase for an average of VALUE_STEP, the third for VALUE_STEP*2
	 */
	private static float[] incForAverageChance;

	/**
	 * Calculates the lookup table and saves it to the file.
	 */
	public static void main(String[] args) {				
		incForAverageChance = new float[VALUE_COUNT];
		incForAverageChance[0] = 0;
		int valueIndex = 1;
		double nextChanceToFind = VALUE_STEP;

		for (double inc = 0; inc <= 1; inc += INC_STEP) {			
			double chance = inc;			
			int countTrue = 0;
			for (int it = 0; it < ITERATIONS; it++) {
				if (rand.nextFloat() < chance) {
					chance = inc;
					countTrue++;
				} else {
					chance += inc;
				}
			}
			double average = (double)countTrue / ITERATIONS;
			while (average >= nextChanceToFind + VALUE_STEP) {
				incForAverageChance[valueIndex++] = (float)inc;
				nextChanceToFind += VALUE_STEP;
			}
			System.out.println((int)(inc / INC_STEP) + "\t/\t" + (int)(1/INC_STEP));
		}

		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename));
			out.writeObject(incForAverageChance);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void load() {
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
			incForAverageChance = (float[])in.readObject();
			in.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static float incForAverage(float f) {
		int index = (int)Math.floor(f / VALUE_STEP);
		return incForAverageChance[index];
	}
}
