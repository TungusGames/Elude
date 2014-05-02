package tungus.games.elude.game.multiplayer.transfer;

import tungus.games.elude.game.multiplayer.Connection.TransferData;
import tungus.games.elude.levels.scoredata.ScoreData.FiniteLevelScore;

public class FiniteScoreInfo extends TransferData {
	private static final long serialVersionUID = -1165369684537281163L;
	public FiniteLevelScore score;
	@Override
	public void copyTo(TransferData otherData) {
		super.copyTo(otherData);
		FiniteScoreInfo other = (FiniteScoreInfo)otherData;
		score.copyTo(other.score);
	}
	public FiniteScoreInfo(FiniteLevelScore sc) {
		score = sc;
	}
}
