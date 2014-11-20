package tungus.games.elude.game.server;

import tungus.games.elude.game.client.worldrender.Renderable;

public abstract class Updatable {
	
	private static int nextID = 100; //Low values reserved for specials, e.g. vessels
	protected final int id;
	public boolean keepsWorldGoing = false;

	protected Updatable() {
		id = nextID++;
	}
	
	public abstract boolean update(float deltaTime);
	public abstract Renderable getRenderable();
	
}
