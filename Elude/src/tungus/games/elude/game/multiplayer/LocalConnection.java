package tungus.games.elude.game.multiplayer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.TimeUtils;

import tungus.games.elude.game.server.UpdateInfo;


public class LocalConnection extends Connection {
	
	/**
	 * Use this to create a pair of LocalConnections
	 */
	public static class LocalConnectionPair {
		public Connection c1;
		public Connection c2;
		public LocalConnectionPair() {
			LocalConnection l1 = new LocalConnection();
			LocalConnection l2 = new LocalConnection();
			l1.pair(l2);
			l2.pair(l1);
			c1 = l1; c2 = l2;
		}
	}
	
	private LocalConnection other;
	
	private LocalConnection() {}
	
	private void pair(LocalConnection other) {
		this.other = other;
	}
	
	@Override
	public void write(TransferData o) {
		Gdx.app.log("Want to send from "+ (o instanceof UpdateInfo ? "client to server" : "server to client"), ""+TimeUtils.millis());
		synchronized(other) {
			Gdx.app.log("Starting send from"+ (o instanceof UpdateInfo ? "client to server" : "server to client"), ""+TimeUtils.millis());
			o.copyTo(other.newest);
			Gdx.app.log("Finished send from"+ (o instanceof UpdateInfo ? "client to server" : "server to client"), ""+TimeUtils.millis());
		}
	}
}
