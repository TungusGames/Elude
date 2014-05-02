package tungus.games.elude.game.multiplayer.transfer;

import tungus.games.elude.game.multiplayer.Connection.TransferData;
import tungus.games.elude.levels.scoredata.ScoreData.ArcadeLevelScore;

public class ArcadeScoreInfo extends TransferData {
	private static final long serialVersionUID = -390600641003851568L;
	public ArcadeLevelScore score;
	@Override
	public void copyTo(TransferData otherData) {
		super.copyTo(otherData);
		ArcadeScoreInfo other = (ArcadeScoreInfo)otherData;
		score.copyTo(other.score);
	}
	public ArcadeScoreInfo(ArcadeLevelScore sc) {
		score = sc;
	}
}
