package tungus.games.elude.game.multiplayer;

public abstract class Connection {

	public Object newest;
	public abstract void write(Object o);
}
