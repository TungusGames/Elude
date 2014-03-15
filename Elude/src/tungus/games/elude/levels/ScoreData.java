package tungus.games.elude.levels;

import java.io.Serializable;

import com.badlogic.gdx.files.FileHandle;

public class ScoreData implements Serializable {
	public static class FiniteLevelData implements Serializable {
		public boolean completed;
		public float time;
		public float hpLost;
		
	}
	public static ScoreData INSTANCE = new ScoreData();
	
	private ScoreData() {} // Enforce non-instantiability
	
	public void load(FileHandle file) {
		
	}
	
	public void save(FileHandle file) {
		
	}

}
