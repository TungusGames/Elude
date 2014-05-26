package tungus.games.elude.levels.scoredata;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class ScoreData {
	public static class FiniteLevelScore implements Serializable {
		private static final long serialVersionUID = -7484248790248208142L;
		public float timeTaken;
		public float hpLeft;
		public boolean completed;
		public FiniteLevelScore() {
			timeTaken = 1000000;
			hpLeft = -1;
			completed = false;
		}
		public FiniteLevelScore(float time, float hp) {
			timeTaken = time;
			hpLeft = hp;
			completed = true;
		}
		public void copyTo(FiniteLevelScore other) {
			other.timeTaken = timeTaken;
			other.hpLeft = hpLeft;
			other.completed = completed;
		}
	}
	public static class ArcadeLevelScore implements Serializable {
		private static final long serialVersionUID = 4906372665430662980L;
		public float timeSurvived;
		public int enemiesKilled;
		public boolean tried;
		public ArcadeLevelScore() {
			timeSurvived = enemiesKilled = -1;
			tried = false;
		}
		public ArcadeLevelScore(float time, int e) {
			timeSurvived = time;
			enemiesKilled = e;
			tried = true;
		}
		public void copyTo(ArcadeLevelScore other) {
			other.timeSurvived = timeSurvived;
			other.enemiesKilled = enemiesKilled;
			other.tried = tried;
		}
	}
		
	public static List<FiniteLevelScore> playerFiniteScore;
	public static List<ArcadeLevelScore> playerArcadeScore;
	public static List<FiniteLevelScore> finiteMedals;
	public static List<ArcadeLevelScore> arcadeMedals;
	public static int lastFiniteCompleted = -1;
	public static int starsEarned = 0;
	public static int starsMax = 0;
	private static Map<Integer, FiniteLevelScore> finiteScoreMap;
	private static Map<Integer, ArcadeLevelScore> arcadeScoreMap;
	private static int finiteLevelnumToID[];
	private static int arcadeLevelnumToID[];
	
	private static final FileHandle medalFiniteFile = Gdx.files.internal("medals/finite.medal");
	private static final FileHandle medalArcadeFile = Gdx.files.internal("medals/arcade.medal");
	private static final FileHandle finiteLevelnumToIDFile = Gdx.files.internal("levels/finitentoid");
	private static final FileHandle arcadeLevelnumToIDFile = Gdx.files.internal("levels/arcadentoid");
	private static final FileHandle playerFiniteFile = Gdx.files.local("scores/finite.score");
	private static final FileHandle playerArcadeFile = Gdx.files.local("scores/arcade.score");
	
	@SuppressWarnings("unchecked")
	public static void load() {
		try {
			ObjectInputStream in = new ObjectInputStream(medalArcadeFile.read());
			arcadeMedals = (List<ArcadeLevelScore>)(in.readObject());
			in.close();
			in = new ObjectInputStream(medalFiniteFile.read());
			finiteMedals = (List<FiniteLevelScore>)(in.readObject());
			in.close();
			in = new ObjectInputStream(finiteLevelnumToIDFile.read());
			finiteLevelnumToID = (int[])(in.readObject());
			in.close();
			in = new ObjectInputStream(arcadeLevelnumToIDFile.read());
			arcadeLevelnumToID = (int[])(in.readObject());
			in.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Badly serialized score file", e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to read score file", e);
		}
		
		try {
			finiteScoreMap = (HashMap<Integer,FiniteLevelScore>)(new ObjectInputStream(playerFiniteFile.read()).readObject());
			arcadeScoreMap = (HashMap<Integer,ArcadeLevelScore>)(new ObjectInputStream(playerArcadeFile.read()).readObject());
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
		playerFiniteScore = new ArrayList<FiniteLevelScore>(finiteMedals.size());
		playerArcadeScore = new ArrayList<ArcadeLevelScore>(arcadeMedals.size());
		starsEarned = 0;
		starsMax = 0;
		int s = finiteMedals.size();
		for (int i = 0; i < s; i++) {
			FiniteLevelScore sc = finiteScoreMap.get(finiteLevelnumToID[i]);
			if (sc == null) {
				sc = new FiniteLevelScore();
				finiteScoreMap.put(finiteLevelnumToID[i], sc);
			}
				
			if (sc.completed)
				lastFiniteCompleted = i;
			starsEarned += ((sc.completed ? 1 : 0) + (sc.timeTaken <= finiteMedals.get(i).timeTaken ? 1 : 0) + (sc.hpLeft >= finiteMedals.get(i).hpLeft ? 1 : 0));
			starsMax += 3;
			playerFiniteScore.add(sc);
		}
		s = arcadeMedals.size();
		for (int i = 0; i < s; i++) {
			ArcadeLevelScore sc = arcadeScoreMap.get(arcadeLevelnumToID[i]);
			if (sc == null) {
				sc = new ArcadeLevelScore();
				arcadeScoreMap.put(arcadeLevelnumToID[i], sc);
			}
			playerArcadeScore.add(sc);
			starsEarned += ((sc.timeSurvived >= arcadeMedals.get(i).timeSurvived ? 1 : 0) + (sc.enemiesKilled >= arcadeMedals.get(i).enemiesKilled ? 1 : 0));
			starsMax += 2;
		}
		
	}
	
	private static void genPlayerScoreFiles() {
		finiteScoreMap = new HashMap<Integer,FiniteLevelScore>();
		int s = finiteMedals.size();
		for (int i = 0; i < s; i++)
			finiteScoreMap.put(finiteLevelnumToID[i], new FiniteLevelScore());
		
		arcadeScoreMap = new HashMap<Integer, ArcadeLevelScore>();
		s = arcadeMedals.size();
		for (int i = 0; i < s; i++)
			arcadeScoreMap.put(arcadeLevelnumToID[i], new ArcadeLevelScore());
		save(false);
		save(true);
	}
	
	public static void save(boolean finite) {
		Object o = finite ? finiteScoreMap : arcadeScoreMap;
		FileHandle file = finite ? playerFiniteFile : playerArcadeFile;
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
	
	public static boolean hasMedal(boolean finite, boolean time, int levelNum) {
		if (finite) {
			FiniteLevelScore medal = finiteMedals.get(levelNum);
			FiniteLevelScore player = playerFiniteScore.get(levelNum);
			if (!player.completed)
				return false;
			if (time) {
				return medal.timeTaken >= player.timeTaken;
			} else {
				return medal.hpLeft <= player.hpLeft;
			}
		} else {
			ArcadeLevelScore medal = arcadeMedals.get(levelNum);
			ArcadeLevelScore player = playerArcadeScore.get(levelNum);
			if (time) {
				return medal.timeSurvived <= player.timeSurvived;
			} else {
				return medal.enemiesKilled <= player.enemiesKilled;
			}
		}
	}
}
