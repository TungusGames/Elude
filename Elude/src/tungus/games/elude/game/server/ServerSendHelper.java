package tungus.games.elude.game.server;

import com.badlogic.gdx.Gdx;

import tungus.games.elude.game.client.GameScreen;
import tungus.games.elude.game.multiplayer.Connection;
import tungus.games.elude.game.multiplayer.Connection.TransferData;
import tungus.games.elude.game.multiplayer.transfer.RenderInfo;
import tungus.games.elude.util.log.AverageLogger;

public class ServerSendHelper implements Runnable {

	private final Connection[] clients;
	private final Connection serverConnection;
	private TransferData data = new RenderInfo();
	private final AverageLogger logger = new AverageLogger("SendLogger", "Average send time: ");
	private final Object invoker;

	public ServerSendHelper(Connection server, Connection[] clients, Object invoker) {
		this.clients = clients;
		this.serverConnection = server;
		this.invoker = invoker;
	}

	@Override
	public void run() {
		while (true) {
			try {
				synchronized (invoker) {
					invoker.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			synchronized(serverConnection) {
				if (!serverConnection.newest.handled) {
					data = serverConnection.newest.copyTo(data);
					serverConnection.newest.handled = true;
				}
			}
			long t1 = System.nanoTime();
			for (Connection c : clients) {
				c.write(data);
			}
			data.handled = true;
			long delta = System.nanoTime()-t1; 
			logger.log(delta/1000000f);
			/*if (delta > 5000000) {
				Gdx.app.log("SendLogger", "High send time (ms): " + delta/1000000f);
			}*/
		}
	}

}
