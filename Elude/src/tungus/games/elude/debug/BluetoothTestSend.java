package tungus.games.elude.debug;

import tungus.games.elude.BaseScreen;
import tungus.games.elude.game.multiplayer.Connection;
import tungus.games.elude.game.multiplayer.Connection.TransferData;
import tungus.games.elude.game.multiplayer.StreamConnection;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class BluetoothTestSend extends BaseScreen {

	private Connection connection;
	private boolean first = true;
	
	public BluetoothTestSend(Game game, StreamConnection c) {
		super(game);
		connection = c;	
	}
	
	private static class BigDump extends TransferData {
		public int[] t = new int[25600];
		public BigDump() {
			super();
		}
		public BigDump(int i) {
			super(i);
		}
	}
	
	private Thread sender = new Thread() {
		@Override
		public void run() {
			long time1 = System.nanoTime();
			long prev = time1;
			for (int i = 0; i < 10000; i++) {
				//if (i % 100 == 0) {
					Gdx.app.log("Send", ""+i);
				long time2 = System.nanoTime();
				Gdx.app.log("Send", "delta: "+(time2-prev)/1000000f + " ms");
				prev = time2;
				//}
				connection.write(new BigDump());
			}
			connection.write(new BigDump(0));
			Gdx.app.log("TIME", (System.nanoTime()-time1)/1000000f+" ms");
		}
	};
	
	@Override
	public void render(float delta) {
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (first) {
			first = false;
			sender.start();
		}
	}
	
}
