package tungus.games.elude.levels.levelselect;

import java.text.SimpleDateFormat;

import tungus.games.elude.Assets;
import tungus.games.elude.levels.scoredata.ScoreData;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class ScoreDetails {
	
	private final boolean finite;
	private final int levelNum;
	
	private static final float TEXT_X = 11.5f*40;
	private static final float STAR_X = 12.5f;
	private static final float STAR_WIDTH = 0.8f;
	private static final float STAR_HEIGHT = STAR_WIDTH*0.95f;
	private static final float SCORE_INDENT = 80;
	private static final float NEXTMEDAL_OPACITY = 0.45f;
	
	private static final float LINE_APPEAR_TIME = 0.25f;
	private static final float OFFSET_UNIT = (DetailsPanel.SWITCH_TIME-LINE_APPEAR_TIME)/6; // 7 lines in total - 0 to 6
	
	private static SimpleDateFormat df = new SimpleDateFormat("mm:ss.SS");
	
	private final Sprite playerTime;
	private final Sprite playerHit;
	private final Sprite medalTime;
	private final Sprite medalHit;
	
	public ScoreDetails(int levelNum, boolean finite) {
		this.finite = finite;
		this.levelNum = levelNum;
		
		df.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
		
		int timeMedal = ScoreData.getMedal(finite, true, levelNum);
		int hitMedal = ScoreData.getMedal(finite, false, levelNum);
		playerTime = new Sprite(Assets.stars[timeMedal]);
		playerTime.setBounds(STAR_X, 7.8f, STAR_WIDTH, STAR_HEIGHT);
		playerHit =  new Sprite(Assets.stars[hitMedal]);
		playerHit.setBounds(STAR_X, 4.8f, STAR_WIDTH, STAR_HEIGHT);
		
		medalTime = timeMedal == 3 ? null : new Sprite(Assets.stars[timeMedal+1]);
		medalHit  = hitMedal  == 3 ? null : new Sprite(Assets.stars[hitMedal +1]);
		if (medalTime != null) {
			medalTime.setBounds(STAR_X, 6.8f, STAR_WIDTH, STAR_HEIGHT);
			medalTime.setColor(1,1,1,NEXTMEDAL_OPACITY);
		}
		if (medalHit != null) {
			medalHit.setColor(1,1,1,NEXTMEDAL_OPACITY);
			medalHit.setBounds(STAR_X, 3.8f, STAR_WIDTH, STAR_HEIGHT);
		}	
	}
	
	private boolean complete() {
		return finite ? ScoreData.playerFiniteScore.get(levelNum).completed : ScoreData.playerArcadeScore.get(levelNum).tried;
	}
	
	public void render(SpriteBatch batch, boolean batchingText, float stateTime, float alpha) {
		Assets.font.setColor(1, 1, 1, alpha);
		playerTime.setColor(1,1,1,alpha);
		playerHit.setColor(1,1,1,alpha);
		if (medalHit != null)
			medalHit.setColor(1,1,1,alpha*NEXTMEDAL_OPACITY);
		if (medalTime != null)
			medalTime.setColor(1,1,1,alpha*NEXTMEDAL_OPACITY);
		
		if (!batchingText) {
			if (complete()) {
				playerHit.setX(getX(STAR_X, stateTime, 5, batchingText));
				playerHit.draw(batch);
				playerTime.setX(getX(STAR_X, stateTime, 2, batchingText));
				playerTime.draw(batch);
				if (medalTime != null) {
					medalTime.setX(getX(STAR_X, stateTime, 3, batchingText));
					medalTime.draw(batch);
				}
				if (medalHit != null) {
					medalHit.setX(getX(STAR_X, stateTime, 6, batchingText));
					medalHit.draw(batch);
				}
			}
		} else {
			float y = 9.5f*40;
			if (complete()) {
				Assets.font.setScale(1.05f);
				Assets.font.draw(batch, "LEVEL "+(levelNum+1), getX(TEXT_X+SCORE_INDENT*0.8f, stateTime, 0, batchingText), 440);
				Assets.font.setScale(1);
				Assets.font.draw(batch, finite ? "TIME TAKEN" : "TIME SURVIVED", getX(TEXT_X, stateTime, 1, batchingText), y);
				y -= 40;
				float seconds = finite ? 	ScoreData.playerFiniteScore.get(levelNum).timeTaken :
											ScoreData.playerArcadeScore.get(levelNum).timeSurvived;
				Assets.font.draw(batch, formatSeconds(seconds), getX(TEXT_X+SCORE_INDENT, stateTime, 2, batchingText), y);
				y -= 40;
				int nextMedal = ScoreData.getMedal(finite, true, levelNum) + 1;
				if (nextMedal <= 3) {
					Assets.font.setColor(1, 1, 1, alpha*NEXTMEDAL_OPACITY);
					float medalTime = finite ? 	ScoreData.finiteMedals.get(levelNum)[nextMedal-1].timeTaken :
												ScoreData.arcadeMedals.get(levelNum)[nextMedal-1].timeSurvived;
					Assets.font.draw(batch, "(", getX(TEXT_X+30, stateTime, 3, batchingText), y);
					Assets.font.draw(batch, formatSeconds(medalTime)+")", getX(TEXT_X+SCORE_INDENT, stateTime, 3, batchingText), y);
					Assets.font.setColor(1, 1, 1, alpha);
				}
				y -= 40;
				Assets.font.draw(batch, finite ? "HEALTH LOST" : "ENEMIES KILLED", getX(TEXT_X, stateTime, 4, batchingText), y);
				y -= 40;
				Assets.font.draw(batch, (finite ? (int)ScoreData.playerFiniteScore.get(levelNum).hpLost :
													  ScoreData.playerArcadeScore.get(levelNum).enemiesKilled) + "", getX(TEXT_X+SCORE_INDENT,stateTime,5, batchingText), y);
				y -= 40;
				nextMedal = ScoreData.getMedal(finite, false, levelNum) + 1;
				if (nextMedal <= 3) {
					Assets.font.setColor(1, 1, 1, alpha*NEXTMEDAL_OPACITY);
					int medalKills = finite ?(int)ScoreData.finiteMedals.get(levelNum)[nextMedal-1].hpLost :
											 	  ScoreData.arcadeMedals.get(levelNum)[nextMedal-1].enemiesKilled;
					Assets.font.draw(batch, "(", getX(TEXT_X+30, stateTime, 6, batchingText), y);
					Assets.font.draw(batch, medalKills+")", getX(TEXT_X+SCORE_INDENT, stateTime, 6, batchingText), y);
					Assets.font.setColor(1, 1, 1, alpha);
				} 
			} else {
				Assets.font.setScale(1.05f);
				Assets.font.draw(batch, "LEVEL "+(levelNum+1), getX(TEXT_X+SCORE_INDENT*0.8f, stateTime, 0, batchingText), 310);
				Assets.font.draw(batch, finite ? "NOT COMPLETED" : "NOT TRIED YET", getX(TEXT_X, stateTime, 3, batchingText), 270);
			}
		}
	}
	
	private String formatSeconds(float s) {
		return df.format((int)(s*1000));
	}
	
	private float getX(float goal, float stateTime, int offset, boolean text) {
		stateTime -= offset*OFFSET_UNIT;
		stateTime /= LINE_APPEAR_TIME;
		stateTime = MathUtils.clamp(stateTime, 0, 1);
		return goal + DetailsPanel.interp.apply(1-stateTime)*10 * (text ? 40 : 1);
	}
	
	public void renderEntering(SpriteBatch batch, boolean text, float ready) {
		//Assets.font.setColor(Color.WHITE);
		render(batch, text, ready, 1);
	}

	public void renderFading(SpriteBatch batch, boolean text, float alpha) {
		
	}

}
