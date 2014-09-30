package tungus.games.elude;

import java.util.Scanner;

import tungus.games.elude.game.client.FakeDebugClientScreen;
import tungus.games.elude.game.client.GameScreen;
import tungus.games.elude.game.multiplayer.Connection;
import tungus.games.elude.game.multiplayer.LocalConnection.LocalConnectionPair;
import tungus.games.elude.game.multiplayer.StreamConnection;
import tungus.games.elude.game.server.Server;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class NetMPScreen extends BaseScreen {
	
	private static final int levelNum = 4;
	private static final boolean finite = false;
	
	private static final int MODE_FAKE = 0;
	private static final int MODE_LISTEN = 1;
	private static final int MODE_CONNECT = 2;
	
	private static int mode = MODE_FAKE;
	private static int port = 8901;
	private static String IP = "25.???.??.???";
	
	public NetMPScreen(Game game) {
		super(game);
		try {
			Scanner sc = new Scanner(Gdx.files.internal("mpdebug.txt").read());
			String str = sc.next();
			if (str.equals("listen")) {
				mode = MODE_LISTEN;
			} else if (str.equals("connect")) {
				mode = MODE_CONNECT;
				IP = sc.next();
			} else {
				sc.close();
				return;
			}
			port = sc.nextInt();
			sc.close();
		} catch (Exception e) {
			Gdx.app.log("Net MP", "Failed to load MP command file");
			e.printStackTrace();
		}
	}
	
	@Override
	public void render(float deltaTime) {
		Socket s = null;
		if (mode == MODE_FAKE) {
			LocalConnectionPair p1 = new LocalConnectionPair();
			LocalConnectionPair p2 = new LocalConnectionPair();
			new Thread(new Server(levelNum, finite, new Connection[]{p1.c1, p2.c1})).start();
			new Thread(new FakeDebugClientScreen(game, levelNum, finite, p2.c2)).start();
			game.setScreen(new GameScreen(game, levelNum, finite, p1.c2, 0));
		} else if (mode == MODE_CONNECT) {
			Gdx.app.log("MODE", "CONNECT");
			//SocketHints hints = new SocketHints();
			//hints.
			s = Gdx.net.newClientSocket(Net.Protocol.TCP, IP, port, new SocketHints());
			game.setScreen(new GameScreen(game, levelNum, finite, new StreamConnection(s.getInputStream(), s.getOutputStream(), s), 1));
		} else if (mode == MODE_LISTEN) {
			Gdx.app.log("MODE", "LISTEN");
			try {
				ServerSocketHints hints = new ServerSocketHints();
				hints.acceptTimeout = 0;
				ServerSocket ss = Gdx.net.newServerSocket(Net.Protocol.TCP, port, hints); 
				s = ss.accept(new SocketHints());
				ss.dispose();
				LocalConnectionPair c = new LocalConnectionPair();
				new Thread(new Server(levelNum, finite, new Connection[] {c.c1, new StreamConnection(s.getInputStream(), s.getOutputStream(), s)})).start();
				game.setScreen(new GameScreen(game, levelNum, finite, c.c2, 0));
			} catch (GdxRuntimeException e) {
				Gdx.app.log("Net MP", "Socket accept timed out. Retrying...");
				e.printStackTrace();
			}
			
		}
	}

}
