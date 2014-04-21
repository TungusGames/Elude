package tungus.games.elude.game.server;

import java.io.Serializable;

import com.badlogic.gdx.math.Vector2;

public class UpdateInfo implements Serializable {
	private static final long serialVersionUID = 3306977025518046441L;
	public Vector2[] directions;
	public int info;
}
