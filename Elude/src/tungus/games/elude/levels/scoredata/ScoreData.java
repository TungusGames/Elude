package tungus.games.elude.levels.scoredata;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class ScoreData {
	public static class FiniteLevelScore implements Serializable {
		public float timeTaken;
		public float hpLost;
		public boolean completed;
		public FiniteLevelScore() {
			timeTaken = hpLost = -1;
			completed = false;
		}
	}
	public static class ArcadeLevelScore implements Serializable {
		public float timeSurvived;
		public int enemiesKilled;
		public boolean tried;
		public ArcadeLevelScore() {
			timeSurvived = enemiesKilled = -1;
			tried = false;
		}
	}
	
	//private ScoreData() {} // Enforce non-instantiability
	
	public static List<FiniteLevelScore> playerFiniteScore;
	public static List<ArcadeLevelScore> playerArcadeScore;
	public static List<FiniteLevelScore[]> finiteMedals;
	public static List<ArcadeLevelScore[]> arcadeMedals;
	
	private static FileHandle medalFinite = Gdx.files.internal("medals/finite.score");
	private static FileHandle medalArcade = Gdx.files.internal("medals/arcade.score");
	private static FileHandle playerFinite = Gdx.files.local("scores/finite.score");
	private static FileHandle playerArcade = Gdx.files.local("scores/arcade.score");
	
	@SuppressWarnings("unchecked")
	public static void load() {
		try {
			arcadeMedals = (List<ArcadeLevelScore[]>)(new ObjectInputStream(medalArcade.read()).readObject());
			finiteMedals = (List<FiniteLevelScore[]>)(new ObjectInputStream(medalFinite.read()).readObject());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Badly serialized score file", e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to read score file", e);
		}
		
		try {
			playerArcadeScore = (List<ArcadeLevelScore>)(new ObjectInputStream(playerArcade.read()).readObject());
			playerFiniteScore = (List<FiniteLevelScore>)(new ObjectInputStream(playerFinite.read()).readObject());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			Gdx.app.log("Files", "Badly serialized player score file", e);
			genPlayerScoreFiles();
		} catch (IOException e) {
			e.printStackTrace();
			Gdx.app.log("Files", "Failed to read player score file", e);
			genPlayerScoreFiles();
		} catch (GdxRuntimeException e) {
			e.printStackTrace();
			Gdx.app.log("Files", "Failed to read player score file", e);
			genPlayerScoreFiles();
		}
		
		
	}
	
	private static void genPlayerScoreFiles() {
		playerArcadeScore = new ArrayList<ArcadeLevelScore>();
		int s = arcadeMedals.size();
		for (int i = 0; i < s; i++)
			playerArcadeScore.add(new ArcadeLevelScore());
		
		playerFiniteScore = new ArrayList<FiniteLevelScore>();
		s = finiteMedals.size();
		for (int i = 0; i < s; i++)
			playerFiniteScore.add(new FiniteLevelScore());
		save(false);
		save(true);
	}
	
	public static void save(boolean finite) {
		Object o = finite ? playerFiniteScore : playerArcadeScore;
		FileHandle file = finite ? playerFinite : playerArcade;
		ObjectOutputStream out;
		try {
			out = new ObjectOutputStream(file.write(false));
			out.writeObject(o);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			Gdx.app.log("Files", "Failed to save player score", e);
		}
	}
	
	public static int getMedal(boolean finite, boolean time, int levelNum) { // 0: no medal, 1-3: bronze, silver, gold
		if (finite) {
			FiniteLevelScore[] medals = finiteMedals.get(levelNum);
			FiniteLevelScore player = playerFiniteScore.get(levelNum);
			if (!player.completed)
				return 0;
			int i = 0;
			if (time) {
				while (i < 3 && player.timeTaken <= medals[i].timeTaken)
					i++;
			} else {
				while (i < 3 && player.hpLost <= medals[i].hpLost)
					i++;
			}
			return i;
		} else {
			ArcadeLevelScore[] medals = arcadeMedals.get(levelNum);
			ArcadeLevelScore player = playerArcadeScore.get(levelNum);
			int i = 0;
			if (time) {
				while (i < 3 && player.timeSurvived >= medals[i].timeSurvived)
					i++;
			} else {
				while (i < 3 && player.enemiesKilled >= medals[i].enemiesKilled)
					i++;
			}
			return i;
		}
	}
}
