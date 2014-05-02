package tungus.games.elude.game.multiplayer.transfer;

import tungus.games.elude.game.multiplayer.Connection.TransferData;

import com.badlogic.gdx.math.Vector2;

public class UpdateInfo extends TransferData {
	private static final long serialVersionUID = 3306977025518046441L;
	public Vector2[] directions;
	
	@Override
	public TransferData copyTo(TransferData otherData) {
		UpdateInfo other = null;
		if (otherData instanceof UpdateInfo) {
			other = (UpdateInfo)otherData;
		} else {
			other = new UpdateInfo();
		}
		super.copyTo(other);
		int s = directions.length;
		if (other.directions == null) {
			other.directions = new Vector2[s];
			for (int i = 0; i < s; i++)
				other.directions[i] = new Vector2(directions[i]);
		} else {
			if (other.directions.length < s) {
				other.directions = new Vector2[s];
			}
			for (int i = 0; i < s; i++)
				other.directions[i].set(directions[i]);
		}
		return other;
	}
}
