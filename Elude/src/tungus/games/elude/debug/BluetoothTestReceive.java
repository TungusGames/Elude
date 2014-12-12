package tungus.games.elude.debug;

import tungus.games.elude.BaseScreen;
import tungus.games.elude.game.multiplayer.Connection;
import tungus.games.elude.game.multiplayer.Connection.TransferData;
import tungus.games.elude.game.multiplayer.StreamConnection;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class BluetoothTestReceive extends BaseScreen {

	private Connection connection;
	private boolean received = true;
	private long startTime;
	
	public BluetoothTestReceive(Game game, StreamConnection c) {
		super(game);
		connection = c;
		c.startRead();
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (!received && connection.newest != null) {
			received = true;
			startTime = System.nanoTime();
		} else if (connection.newest.info == 0) {
			Gdx.app.log("TIME (recieve)", (System.nanoTime()-startTime)/1000000f + " ms");
		}
	}
}
