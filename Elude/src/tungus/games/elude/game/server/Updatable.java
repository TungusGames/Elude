package tungus.games.elude.game.server;

import tungus.games.elude.game.client.worldrender.renderable.Renderable;

public abstract class Updatable {
	
	private static int nextID = 0; //Low values reserved for specials, e.g. vessels
	protected final int id;
	public boolean keepsWorldGoing = false;

	protected Updatable() {
		id = nextID++;
	}
	
	public abstract boolean update(float deltaTime);
	public abstract Renderable getRenderable();
	
	public static void reset() {
		nextID = 0;
		Vessel.nextVesselNumber = 0;
	}
	
}
