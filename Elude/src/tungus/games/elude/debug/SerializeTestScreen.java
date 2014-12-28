package tungus.games.elude.debug;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import tungus.games.elude.BaseScreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
/**
 * Android 2.2 appearently can't serialize ArrayDeque (or ArrayList..).
 * Verfying this..
 */
public class SerializeTestScreen extends BaseScreen {
	public SerializeTestScreen(Game game) {
		super(game);
	}
	
	private static class SimpleData implements Serializable {

		private static final long serialVersionUID = 356153061465452245L;
		int x;
	}
	private static class WithList implements Serializable {

		private static final long serialVersionUID = -466013580650750735L;
		int x;
		List<String> l = null;
	}
	private static class WithDeque implements Serializable {

		private static final long serialVersionUID = 7225829220211048961L;
		int x;
		Deque<String> q = null;
	}
	
	private boolean loaded = false;
	private FileHandle file = Gdx.files.local("serializetest");
	
	@Override
	public void render(float deltaTime) {
		if (!loaded) {
			loaded = true;
			try {
				ObjectOutputStream out = new ObjectOutputStream(file.write(false));
				SimpleData s = new SimpleData();
				s.x = 5;
				out.writeObject(s);
				out.close();
				Gdx.app.log("SERIALIZETEST", "Simple written");
				
				ObjectInputStream in = new ObjectInputStream(file.read());
				SimpleData s2 = (SimpleData)(in.readObject());
				in.close();
				Gdx.app.log("SERIALIZETEST", "Simple read - " + s2.x);
								
				out = new ObjectOutputStream(file.write(false));
				WithList l = new WithList();
				l.x = 6;
				l.l = new ArrayList<String>();
				l.l.add("OK");
				out.writeObject(l);
				out.close();
				Gdx.app.log("SERIALIZETEST", "List written");
				
				in = new ObjectInputStream(file.read());
				WithList l2 = (WithList)(in.readObject());
				in.close();
				Gdx.app.log("SERIALIZETEST", "List read - " + l2.x + l2.l.get(0));
				
				out = new ObjectOutputStream(file.write(false));
				WithDeque q = new WithDeque();
				q.x = 7;
				q.q = new ArrayDeque<String>();
				q.q.add("OK");
				out.writeObject(q);
				out.close();
				Gdx.app.log("SERIALIZETEST", "Deque written");
				
				in = new ObjectInputStream(file.read());
				WithDeque q2 = (WithDeque)(in.readObject());
				in.close();
				Gdx.app.log("SERIALIZETEST", "Deque read - " + q2.x + q2.q.getFirst());
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
		}
	}
}
