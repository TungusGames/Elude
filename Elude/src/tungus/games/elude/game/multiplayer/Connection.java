package tungus.games.elude.game.multiplayer;

import java.io.Serializable;

public abstract class Connection {
	public static class TransferData implements Serializable {
		private static final long serialVersionUID = -6746553115064408427L;
		public void copyTo(TransferData other) {
			other.info = info;
			other.handled = false;
		}
		public boolean handled = true;
		public int info;
		public TransferData(int i) {
			info = i;
		}
		public TransferData() {
			this(-1);
		}
	}
	public TransferData newest;
	public abstract void write(TransferData o);
}
