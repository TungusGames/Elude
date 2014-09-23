package tungus.games.elude;

import java.util.Scanner;

import tungus.games.elude.game.client.GameScreen;
import tungus.games.elude.game.multiplayer.Connection;
import tungus.games.elude.game.multiplayer.LocalConnection.LocalConnectionPair;
import tungus.games.elude.game.multiplayer.StreamConnection;
import tungus.games.elude.game.server.Server;
import tungus.games.elude.menu.mainmenu.MainMenu;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class NetMPScreen extends BaseScreen {
	
	private static final int levelNum = 1;
	private static final boolean finite = false;
	
	public static boolean ready = true;
	private static boolean listen = true;
	private static int port = 8901;
	private static String IP = "25.???.??.???";
	
	public NetMPScreen(Game game) {
		super(game);
		try {
			Scanner sc = new Scanner(Gdx.files.internal("mpdebug.txt").read());
			if (sc.next().equals("listen")) {
				listen = true;
			} else if (sc.next().equals("connect")) {
				listen = false;
				IP = sc.next();
			}
			port = sc.nextInt();
			ready = true;
			sc.close();
		} catch (Exception e) {
			Gdx.app.log("Net MP", "Failed to load MP command file");
			e.printStackTrace();
		}
	}
	
	@Override
	public void render(float deltaTime) {
		Socket s = null;
		if (!ready) {
			game.setScreen(new MainMenu(game));
		} else if (!listen) {
			s = Gdx.net.newClientSocket(Net.Protocol.TCP, IP, port, new SocketHints());
			game.setScreen(new GameScreen(game, levelNum, finite, new StreamConnection(s.getInputStream(), s.getOutputStream()), 0));
		} else {
			try {
				s = Gdx.net.newServerSocket(Net.Protocol.TCP, port, new ServerSocketHints()).accept(new SocketHints());
				LocalConnectionPair c = new LocalConnectionPair();
				new Thread(new Server(levelNum, finite, new Connection[] {c.c1, new StreamConnection(s.getInputStream(), s.getOutputStream())})).start();
				game.setScreen(new GameScreen(game, levelNum, finite, c.c2, 0));
			} catch (GdxRuntimeException e) {
				Gdx.app.log("Net MP", "Socket accept timed out. Retrying...");
			}
			
		}
	}

}
