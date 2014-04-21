package tungus.games.elude.game.server;

import java.io.Serializable;

import tungus.games.elude.game.multiplayer.Connection.TransferData;

import com.badlogic.gdx.math.Vector2;

public class UpdateInfo implements Serializable, TransferData {
	private static final long serialVersionUID = 3306977025518046441L;
	public Vector2[] directions;
	public int info;
	public boolean handled = true;
	@Override
	public void copyTo(TransferData otherData) {
		UpdateInfo other = (UpdateInfo)otherData;
		int s = directions.length;
		if (other.directions == null) {
			other.directions = new Vector2[s];
			for (int i = 0; i < s; i++)
				other.directions[i] = new Vector2(directions[i]);
		} else {
			if (other.directions.length < s) {
				other.directions = new Vector2[s];
			}
			other.info = info;
			for (int i = 0; i < s; i++)
				other.directions[i].set(directions[i]);
		}
		other.handled = false;
	}
}
