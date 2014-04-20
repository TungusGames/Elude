package tungus.games.elude.game.multiplayer;

public class LocalConnection extends Connection {

	private LocalConnection other;
	
	public void pair(LocalConnection other) {
		this.other = other;
	}
	
	@Override
	public void write(Object o) {
		synchronized(other) {
			other.newest = o;
		}
	}
}
