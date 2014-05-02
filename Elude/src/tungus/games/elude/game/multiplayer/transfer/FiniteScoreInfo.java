package tungus.games.elude.game.multiplayer.transfer;

import tungus.games.elude.game.multiplayer.Connection.TransferData;
import tungus.games.elude.levels.scoredata.ScoreData.FiniteLevelScore;

public class FiniteScoreInfo extends TransferData {
	private static final long serialVersionUID = -1165369684537281163L;
	public FiniteLevelScore score;
	@Override
	public TransferData copyTo(TransferData otherData) {
		FiniteScoreInfo other = null;
		if (otherData instanceof FiniteScoreInfo) {
			other = (FiniteScoreInfo)otherData;
			score.copyTo(other.score);
		} else {
			other = new FiniteScoreInfo(score);
		}
		super.copyTo(other);
		return other;
	}
	public FiniteScoreInfo(FiniteLevelScore sc) {
		score = sc;
	}
}
