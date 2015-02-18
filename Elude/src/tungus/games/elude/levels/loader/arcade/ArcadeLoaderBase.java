package tungus.games.elude.levels.loader.arcade;

import tungus.games.elude.Assets;
import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.enemies.Enemy;
import tungus.games.elude.levels.loader.EnemyLoader;
import tungus.games.elude.levels.scoredata.ScoreData;
import tungus.games.elude.levels.scoredata.ScoreData.ArcadeLevelScore;

public abstract class ArcadeLoaderBase extends EnemyLoader {
	
	private int enemiesKilled = 0;
	private float timeSurvived = 0;
	
	public ArcadeLoaderBase(World w, int levelNum) {
		super(w, levelNum);
	}

	public ArcadeLoaderBase(World w, float hpChance, float speedChance, float wipeChance, float shieldChance, int levelNum) {
		super(w, hpChance, speedChance, shieldChance, wipeChance, levelNum);
	}
	
	@Override
	public void onEnemyDead(Enemy e) {
		super.onEnemyDead(e);
		enemiesKilled++;
	}
	
	@Override
	public boolean update(float deltaTime) {
		super.update(deltaTime);
		timeSurvived += deltaTime;
		return false;
	}

	@Override
	public void saveScore() {
		ArcadeLevelScore score = ScoreData.playerArcadeScore.get(levelNum);
		score.enemiesKilled = Math.max(score.enemiesKilled, enemiesKilled);
		score.timeSurvived  = Math.max(score.timeSurvived , timeSurvived);
		score.tried = true;
		ScoreData.save(false);
	}
	
	public ArcadeLevelScore getScore() {
		ArcadeLevelScore s = new ArcadeLevelScore();
		int medalsBefore = ((ScoreData.hasMedal(false, true, levelNum) ? 1 : 0) + (ScoreData.hasMedal(false, false, levelNum) ? 1 : 0));
		s.tried = true;
		s.timeSurvived = timeSurvived;
		s.enemiesKilled = enemiesKilled;
		int medalsAfter = ((ScoreData.hasMedal(false, true, levelNum) ? 1 : 0) + (ScoreData.hasMedal(false, false, levelNum) ? 1 : 0));
		ScoreData.starsEarned += (medalsAfter-medalsBefore);
		return s;
	}
	
	@Override
	public String levelName() {
		return Assets.Strings.endless + " " + (levelNum+1);
	}
}
