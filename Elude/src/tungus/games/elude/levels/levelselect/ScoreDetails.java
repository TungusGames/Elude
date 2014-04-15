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
	
	private final float starX, starWidth, starHeight;
	private final float textX;
	private final float scale;
	private final boolean modAlpha;
	private final float xSource;
	
	public ScoreDetails(int levelNum, boolean finite, float x, float scale, boolean modAlpha, float xSource) {
		this.finite = finite;
		this.levelNum = levelNum;
		
		starX = x;
		textX = x*40/scale-40;
		this.scale = scale;
		starWidth = STAR_WIDTH*scale;
		starHeight = starWidth * 0.95f;
		this.modAlpha = modAlpha;
		this.xSource = xSource;
		
		df.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
		
		int timeMedal = ScoreData.getMedal(finite, true, levelNum);
		int hitMedal = ScoreData.getMedal(finite, false, levelNum);
		playerTime = new Sprite(Assets.stars[timeMedal]);
		playerTime.setBounds(STAR_X, 7.8f*scale, starWidth, starHeight);
		playerHit =  new Sprite(Assets.stars[hitMedal]);
		playerHit.setBounds(STAR_X, 4.8f*scale, starWidth, starHeight);
		
		medalTime = timeMedal == 3 ? null : new Sprite(Assets.stars[timeMedal+1]);
		medalHit  = hitMedal  == 3 ? null : new Sprite(Assets.stars[hitMedal +1]);
		if (medalTime != null) {
			medalTime.setBounds(STAR_X, 6.8f*scale, starWidth, starHeight);
			medalTime.setColor(1,1,1,NEXTMEDAL_OPACITY);
		}
		if (medalHit != null) {
			medalHit.setColor(1,1,1,NEXTMEDAL_OPACITY);
			medalHit.setBounds(STAR_X, 3.8f*scale, starWidth, starHeight);
		}	
	}
	
	private boolean complete() {
		return finite ? ScoreData.playerFiniteScore.get(levelNum).completed : ScoreData.playerArcadeScore.get(levelNum).tried;
	}
	
	public void render(SpriteBatch batch, boolean batchingText, float stateTime, float alpha) {
		Assets.font.setColor(1,1,1,alpha);
		if (!batchingText) {
			if (complete()) {				
				playerTime.setX(offsetXPos(starX, stateTime, 2, batchingText));
				playerTime.setColor(1,1,1,offsetAlpha(stateTime, 2, alpha));
				playerTime.draw(batch);
				playerHit.setX(offsetXPos(starX, stateTime, 5, batchingText));
				playerHit.setColor(1,1,1,offsetAlpha(stateTime, 5, alpha));
				playerHit.draw(batch);
				if (medalTime != null) {
					medalTime.setX(offsetXPos(starX, stateTime, 3, batchingText));
					medalTime.setColor(1,1,1,offsetAlpha(stateTime, 3, alpha)*NEXTMEDAL_OPACITY);
					medalTime.draw(batch);
				}
				if (medalHit != null) {
					medalHit.setX(offsetXPos(starX, stateTime, 6, batchingText));
					medalHit.setColor(1,1,1,offsetAlpha(stateTime, 6, alpha) * NEXTMEDAL_OPACITY);
					medalHit.draw(batch);
				}
			}
		} else {
			float y = 9.5f*40;
			if (complete()) {
				Assets.font.setScale(1.05f);
				offsetAlpha(stateTime, 0, alpha);
				Assets.font.draw(batch, "LEVEL "+(levelNum+1), offsetXPos(textX+SCORE_INDENT*0.8f, stateTime, 0, batchingText), 440);
				Assets.font.setScale(1);
				offsetAlpha(stateTime, 1, alpha);
				Assets.font.draw(batch, finite ? "TIME TAKEN" : "TIME SURVIVED", offsetXPos(textX, stateTime, 1, batchingText), y);
				y -= 40;
				float seconds = finite ? 	ScoreData.playerFiniteScore.get(levelNum).timeTaken :
											ScoreData.playerArcadeScore.get(levelNum).timeSurvived;
				offsetAlpha(stateTime, 2, alpha);
				Assets.font.draw(batch, formatSeconds(seconds), offsetXPos(textX+SCORE_INDENT, stateTime, 2, batchingText), y);
				y -= 40;
				int nextMedal = ScoreData.getMedal(finite, true, levelNum) + 1;
				if (nextMedal <= 3) {
					Assets.font.setColor(1, 1, 1, alpha*NEXTMEDAL_OPACITY);
					float medalTime = finite ? 	ScoreData.finiteMedals.get(levelNum)[nextMedal-1].timeTaken :
												ScoreData.arcadeMedals.get(levelNum)[nextMedal-1].timeSurvived;
					offsetAlpha(stateTime, 3, alpha);
					Assets.font.draw(batch, "(", offsetXPos(textX+30, stateTime, 3, batchingText), y);
					Assets.font.draw(batch, formatSeconds(medalTime)+")", offsetXPos(textX+SCORE_INDENT, stateTime, 3, batchingText), y);
					Assets.font.setColor(1, 1, 1, alpha);
				}
				y -= 40;
				offsetAlpha(stateTime, 4, alpha);
				Assets.font.draw(batch, finite ? "HEALTH LOST" : "ENEMIES KILLED", offsetXPos(textX, stateTime, 4, batchingText), y);
				y -= 40;
				offsetAlpha(stateTime, 5, alpha);
				Assets.font.draw(batch, (finite ? (int)ScoreData.playerFiniteScore.get(levelNum).hpLost :
													  ScoreData.playerArcadeScore.get(levelNum).enemiesKilled) + "", offsetXPos(textX+SCORE_INDENT,stateTime,5, batchingText), y);
				y -= 40;
				nextMedal = ScoreData.getMedal(finite, false, levelNum) + 1;
				if (nextMedal <= 3) {
					Assets.font.setColor(1, 1, 1, alpha*NEXTMEDAL_OPACITY);
					int medalKills = finite ?(int)ScoreData.finiteMedals.get(levelNum)[nextMedal-1].hpLost :
											 	  ScoreData.arcadeMedals.get(levelNum)[nextMedal-1].enemiesKilled;
					offsetAlpha(stateTime, 6, alpha);
					Assets.font.draw(batch, "(", offsetXPos(textX+30, stateTime, 6, batchingText), y);
					Assets.font.draw(batch, medalKills+")", offsetXPos(textX+SCORE_INDENT, stateTime, 6, batchingText), y);
					Assets.font.setColor(1, 1, 1, alpha);
				} 
			} else {
				Assets.font.setScale(1.05f);
				Assets.font.draw(batch, "LEVEL "+(levelNum+1), offsetXPos(textX+SCORE_INDENT*0.8f, stateTime, 0, batchingText), 310);
				Assets.font.draw(batch, finite ? "NOT COMPLETED" : "NOT TRIED YET", offsetXPos(textX, stateTime, 3, batchingText), 270);
			}
			Assets.font.setScale(1);
		}
	}
	
	private String formatSeconds(float s) {
		return df.format((int)(s*1000));
	}
	
	private float offsetXPos(float goal, float stateTime, int offsetCount, boolean text) {
		return goal + offset(stateTime, offsetCount)*xSource * (text ? 40 : scale);
	}
	
	private float offsetAlpha(float stateTime, int offsetCount, float baseAlpha) {
		float f = modAlpha ? 1-offset(stateTime, offsetCount)*baseAlpha : baseAlpha;
		Assets.font.setColor(1, 1, 1, f);
		return f;
	}
	
	private float offset(float stateTime, int offsetCount) {
		stateTime -= offsetCount*OFFSET_UNIT;
		stateTime /= LINE_APPEAR_TIME;
		stateTime = MathUtils.clamp(stateTime, 0, 1);
		return DetailsPanel.interp.apply(1-stateTime);
	}
}
