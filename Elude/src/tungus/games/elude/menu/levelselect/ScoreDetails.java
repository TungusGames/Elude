package tungus.games.elude.menu.levelselect;

import java.text.SimpleDateFormat;

import tungus.games.elude.Assets;
import tungus.games.elude.levels.scoredata.ScoreData;
import tungus.games.elude.levels.scoredata.ScoreData.ArcadeLevelScore;
import tungus.games.elude.levels.scoredata.ScoreData.FiniteLevelScore;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class ScoreDetails {
	
	private final boolean finite;
	private final int levelNum;
	
	private static final float STAR_X = 12.5f;
	private static final float STAR_WIDTH = 0.8f;
	//private static final float STAR_HEIGHT = STAR_WIDTH*0.95f;
	private static final float SCORE_INDENT = 80;
	private static final float NEXTMEDAL_OPACITY = 0.6f;
	
	private static final float LINE_APPEAR_PART = 0.2f; 			// How long compared to the whole appear process
	private static final float OFFSET_UNIT = (1-LINE_APPEAR_PART)/6; // 7 lines in total - 0 to 6
	
	private static SimpleDateFormat df = new SimpleDateFormat("mm:ss.SS");
	
	private final Sprite playerTime;
	private final Sprite playerHit;
	private final Sprite medalTime;
	private final Sprite medalHit;
	private final Sprite completition;
	
	private final float starX, starWidth, starHeight;
	private final float textX;
	private final float scale;
	private final boolean modAlpha;
	private final float xSource;
	private final float yTop;
	private final String title;
	private final boolean hasTimeMedal;
	private final boolean hasHitMedal;
	private boolean highScoreTime;
	private boolean highScoreHit;
	private final boolean open;
	private FiniteLevelScore fScore;
	private ArcadeLevelScore aScore;
	
	private ScoreDetails(String title, int levelNum, boolean finite, float x, float y, float scale, boolean modAlpha, float xSource, boolean open) {
		this.finite = finite;	
		this.levelNum = levelNum;
		this.title = title;
		this.open = open;
		starX = x;
		yTop = y;
		textX = x*40/scale-40;
		this.scale = scale;
		starWidth = STAR_WIDTH*scale;
		starHeight = starWidth * 0.95f;
		this.modAlpha = modAlpha;
		this.xSource = xSource;
		
		df.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
		
		hasTimeMedal = //ScoreData.hasMedal(finite, true, levelNum);
				false; // Always displaying the medal limits might be better
		hasHitMedal = //ScoreData.hasMedal(finite, false, levelNum);
				false; // Always displaying the medal limits might be better
		playerTime = new Sprite(Assets.stars[(finite ? fScore.timeTaken <= ScoreData.finiteMedals.get(levelNum).timeTaken :
													   aScore.timeSurvived >= ScoreData.arcadeMedals.get(levelNum).timeSurvived) ? 3 : 0]);
		playerTime.setBounds(STAR_X, (yTop*scale/40f-10.75f+6.6f+(!finite?1:0))*scale, starWidth, starHeight);
		playerHit =  new Sprite(Assets.stars[(finite ? fScore.hpLost <= ScoreData.finiteMedals.get(levelNum).hpLost : 
		                                               aScore.enemiesKilled >= ScoreData.arcadeMedals.get(levelNum).enemiesKilled) ? 3 : 0]);
		playerHit.setBounds(STAR_X, (yTop*scale/40f-10.75f+3.6f+(!finite?1:0))*scale, starWidth, starHeight);
		if (finite && complete()) {
			completition = new Sprite(Assets.stars[3]);
			completition.setBounds(STAR_X, (yTop*scale/40f-10.75f+8.6f)*scale, starWidth, starHeight);
		} else if (!finite && !open) {
			completition = new Sprite(Assets.stars[3]);
			completition.setBounds(textX+25, (yTop*scale/40f-10.75f+6.35f)*scale, starWidth, starHeight);
		} else {
			completition = null;
		}
		
		
		medalTime = hasTimeMedal ? null : new Sprite(Assets.stars[3]);
		medalHit  = hasHitMedal  ? null : new Sprite(Assets.stars[3]);
		if (medalTime != null) {
			medalTime.setBounds(STAR_X, (yTop*scale/40f-10.75f+5.6f+(!finite?1:0))*scale, starWidth, starHeight);
			medalTime.setColor(1,1,1,NEXTMEDAL_OPACITY);
		}
		if (medalHit != null) {
			medalHit.setColor(1,1,1,NEXTMEDAL_OPACITY);
			medalHit.setBounds(STAR_X, (yTop*scale/40f-10.75f+2.6f+(!finite?1:0))*scale, starWidth, starHeight);
		}	
	}
	
	public ScoreDetails(String title, int levelNum, float x, float y, float scale, boolean modAlpha, float xSource, FiniteLevelScore score, 
			boolean allowHS, boolean open) {
		this(title, levelNum, true, x, y, scale, modAlpha, xSource, open);
		aScore = null;
		fScore = score;
		highScoreTime = allowHS && (fScore.timeTaken <= ScoreData.playerFiniteScore.get(levelNum).timeTaken);
		highScoreHit = allowHS && (fScore.hpLost <= ScoreData.playerFiniteScore.get(levelNum).hpLost);
	}
	
	public ScoreDetails(String title, int levelNum, float x, float y, float scale, boolean modAlpha, float xSource, ArcadeLevelScore score, 
			boolean allowHS, boolean open) {
		this(title, levelNum, false, x, y, scale, modAlpha, xSource, open);
		aScore = score;
		fScore = null;
		highScoreTime = allowHS && (aScore.timeSurvived >= ScoreData.playerArcadeScore.get(levelNum).timeSurvived);
		highScoreHit = allowHS && (aScore.enemiesKilled >= ScoreData.playerArcadeScore.get(levelNum).enemiesKilled);
	}
	
	private boolean complete() {
		return finite ? ScoreData.playerFiniteScore.get(levelNum).completed : ScoreData.playerArcadeScore.get(levelNum).tried;
	}
	
	public void render(SpriteBatch batch, boolean batchingText, float stateTime, float alpha) {
		Assets.font.setColor(1,1,1,alpha);
		if (open) {
			if (!batchingText) {
				if (complete()) {
					if (finite) {
						completition.setX(offsetXPos(starX, stateTime, 0, batchingText));
						completition.setColor(1,1,1,offsetAlpha(stateTime, 0, alpha));
						completition.draw(batch);
					}
					playerTime.setX(offsetXPos(starX, stateTime, 2, batchingText));
					playerTime.setColor(1,1,1,offsetAlpha(stateTime, 2, alpha));
					playerTime.draw(batch);
					playerHit.setX(offsetXPos(starX, stateTime, 5, batchingText));
					playerHit.setColor(1,1,1,offsetAlpha(stateTime, 5, alpha));
					playerHit.draw(batch);
				}
			} else {
				float y = yTop*scale;
				if (complete()) {
					Assets.font.setScale(1.05f);
					Assets.font.setColor(1f, 1f, 0.35f, Assets.font.getColor().a);
					offsetAlpha(stateTime, 0, alpha);
					Assets.font.draw(batch, title, offsetXPos(textX+SCORE_INDENT*0.8f, stateTime, 0, batchingText)+100-title.length()*15, y);
					Assets.font.setScale(1);
					Assets.font.setColor(1, 1, 1, Assets.font.getColor().a);
					y -= 60;
					if (finite) {
						Assets.font.draw(batch, "COMPLETED", offsetXPos(textX+SCORE_INDENT, stateTime, 0, batchingText), y);
						y -= 40;
					}
					offsetAlpha(stateTime, 1, alpha);
					Assets.font.setColor(1f, 1f, 0.35f, Assets.font.getColor().a);
					Assets.font.draw(batch, finite ? "TIME TAKEN" : "TIME SURVIVED", offsetXPos(textX, stateTime, 1, batchingText), y);
					Assets.font.setColor(1, 1, 1, Assets.font.getColor().a);
					y -= 40;
					float seconds = finite ? 	fScore.timeTaken :
												aScore.timeSurvived;
					offsetAlpha(stateTime, 2, alpha);
					Assets.font.draw(batch, formatSeconds(seconds*stateTime), offsetXPos(textX+SCORE_INDENT, stateTime, 2, batchingText), y);
					if (highScoreTime) {
						Assets.font.setColor(1, 1, 0.55f, Assets.font.getColor().a*stateTime);
						Assets.font.draw(batch, "HIGH SCORE!", offsetXPos(textX+SCORE_INDENT+200, stateTime, 2, batchingText), y);
						Assets.font.setColor(1, 1, 0.55f, Assets.font.getColor().a/stateTime);
					}				
					y -= 40;
					if (!hasTimeMedal) {
						Assets.font.setColor(1, 1, 1, alpha*NEXTMEDAL_OPACITY);
						float medalTime = finite ? 	ScoreData.finiteMedals.get(levelNum).timeTaken :
													ScoreData.arcadeMedals.get(levelNum).timeSurvived;
						offsetAlpha(stateTime, 3, alpha);
						//Assets.font.draw(batch, "(", offsetXPos(textX+30, stateTime, 3, batchingText), y);
						Assets.font.draw(batch, "(GOAL: "+formatSeconds(medalTime)+")", offsetXPos(textX+SCORE_INDENT-60, stateTime, 3, batchingText), y);
						Assets.font.setColor(1, 1, 1, alpha);
					}
					y -= 40;
					offsetAlpha(stateTime, 4, alpha);
					Assets.font.setColor(1f, 1f, 0.35f, Assets.font.getColor().a);
					Assets.font.draw(batch, finite ? "HEALTH LOST" : "ENEMIES KILLED", offsetXPos(textX, stateTime, 4, batchingText), y);
					Assets.font.setColor(1, 1, 1, Assets.font.getColor().a);
					y -= 40;
					offsetAlpha(stateTime, 5, alpha);
					Assets.font.draw(batch, (int)((finite ? (int)fScore.hpLost :
														  aScore.enemiesKilled)*stateTime) + "", offsetXPos(textX+SCORE_INDENT,stateTime,5, batchingText), y);
					if (highScoreHit) {
						Assets.font.setColor(1, 1, 0.55f, Assets.font.getColor().a*stateTime);
						Assets.font.draw(batch, "HIGH SCORE!", textX+SCORE_INDENT+200, y);
						Assets.font.setColor(1, 1, 0.55f, Assets.font.getColor().a/stateTime);
					}
					y -= 40;
					if (!hasHitMedal) {
						Assets.font.setColor(1, 1, 1, alpha*NEXTMEDAL_OPACITY);
						int medalKills = (finite ?(int)ScoreData.finiteMedals.get(levelNum).hpLost :
												 	  ScoreData.arcadeMedals.get(levelNum).enemiesKilled);
						offsetAlpha(stateTime, 6, alpha);
						//Assets.font.draw(batch, "(", offsetXPos(textX+30, stateTime, 6, batchingText), y);
						Assets.font.draw(batch, "(GOAL: "+medalKills+")", offsetXPos(textX+SCORE_INDENT-60, stateTime, 6, batchingText), y);
						Assets.font.setColor(1, 1, 1, alpha);
					} 
				} else {
					Assets.font.setScale(1.05f);
					Assets.font.draw(batch, "LEVEL "+(levelNum+1), offsetXPos(textX+SCORE_INDENT*0.8f, stateTime, 0, batchingText), 310);
					Assets.font.draw(batch, finite ? "NOT COMPLETED" : "NOT TRIED YET", offsetXPos(textX, stateTime, 3, batchingText), 270);
				}
				Assets.font.setScale(1);
			}
		} else {
			if (batchingText) {
				Assets.font.setScale(1);
				if (finite) {
					Assets.font.draw(batch, "LOCKED", offsetXPos(textX+70, stateTime, 3, batchingText), 290);
				} else {
					Assets.font.draw(batch, (levelNum+1)*10+"", offsetXPos(textX+35-Assets.font.getBounds(""+(levelNum+1)).width, 
							stateTime, 3, batchingText), 290);
					Assets.font.draw(batch, "TO UNLOCK", offsetXPos(textX+105, stateTime, 3, batchingText), 290);
				}
			} else if (!finite) {
				completition.setX(offsetXPos(13f, stateTime, 3, batchingText));
				completition.setColor(1,1,1,offsetAlpha(stateTime, 3, alpha));
				completition.draw(batch);
			}
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
		Color c = Assets.font.getColor();
		c.a = f;
		Assets.font.setColor(c);
		return f;
	}
	
	private float offset(float stateTime, int offsetCount) {
		stateTime -= offsetCount*OFFSET_UNIT;
		stateTime /= LINE_APPEAR_PART;
		stateTime = MathUtils.clamp(stateTime, 0, 1);
		return DetailsPanel.interp.apply(1-stateTime);
	}
}
