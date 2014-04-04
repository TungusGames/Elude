package tungus.games.elude.levels.loader.arcade;

import tungus.games.elude.game.World;
import tungus.games.elude.game.enemies.Enemy;
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
		super(w, hpChance, speedChance, wipeChance, shieldChance, levelNum);
	}
	
	@Override
	public void onEnemyDead(Enemy e) {
		super.onEnemyDead(e);
		enemiesKilled++;
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		timeSurvived += deltaTime;
	}

	@Override
	public void saveScore() {
		ArcadeLevelScore score = ScoreData.playerArcadeScore.get(levelNum);
		score.enemiesKilled = Math.max(score.enemiesKilled, enemiesKilled);
		score.timeSurvived  = Math.max(score.timeSurvived , timeSurvived);
		score.tried = true;
		ScoreData.save(false);
	}

}