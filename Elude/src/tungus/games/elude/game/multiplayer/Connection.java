package tungus.games.elude.game.multiplayer;

public abstract class Connection {
	public interface TransferData {
		void copyTo(TransferData other);
	}
	public TransferData newest;
	public abstract void write(TransferData o);
}
