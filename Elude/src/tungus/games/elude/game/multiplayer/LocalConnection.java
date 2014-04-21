package tungus.games.elude.game.multiplayer;

public class LocalConnection extends Connection {

	private LocalConnection other;
	
	private LocalConnection() {}
	
	/**
	 * Use this to create a pair of LocalConnections
	 */
	public static void createPair(Connection c1, Connection c2) {
		c1 = new LocalConnection();
		c2 = new LocalConnection();
		
		((LocalConnection)c1).pair((LocalConnection)c2);
		((LocalConnection)c2).pair((LocalConnection)c1);
	} 
	
	private void pair(LocalConnection other) {
		this.other = other;
	}
	
	@Override
	public void write(Object o) {
		synchronized(other) {
			other.newest = o;
		}
	}
}
