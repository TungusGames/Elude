package tungus.games.elude.game.multiplayer;

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
		synchronized(other) {
			o.copyTo(other.newest);
		}
	}
}
