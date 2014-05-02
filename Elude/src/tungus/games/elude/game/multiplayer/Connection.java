package tungus.games.elude.game.multiplayer;

import java.io.Serializable;

public abstract class Connection {
	public static class TransferData implements Serializable {
		private static final long serialVersionUID = -6746553115064408427L;
		/**
		 * @param other The object to copy into, if possible
		 * @return The modified other, or a new copy of the object if other is not the same subclass.<br>
		 * Intended use: {@code b = a.copyTo(b);}
		 */
		public TransferData copyTo(TransferData other) {
			other.info = info;
			other.handled = false;
			return other;
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
