package tungus.games.elude.game.multiplayer.transfer;

import tungus.games.elude.game.multiplayer.Connection.TransferData;
import tungus.games.elude.levels.scoredata.ScoreData.ArcadeLevelScore;

public class ArcadeScoreInfo extends TransferData {
	private static final long serialVersionUID = -390600641003851568L;
	public ArcadeLevelScore score;
	@Override
	public TransferData copyTo(TransferData otherData) {
		ArcadeScoreInfo other = null;
		if (otherData instanceof FiniteScoreInfo) {
			other = (ArcadeScoreInfo)otherData;
			score.copyTo(other.score);
		} else {
			other = new ArcadeScoreInfo(score);
		}
		super.copyTo(other);
		return other;
	}
	public ArcadeScoreInfo(ArcadeLevelScore sc) {
		score = sc;
	}
}
