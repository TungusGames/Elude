package tungus.games.elude.game.server;

import java.util.List;

import tungus.games.elude.game.client.worldrender.renderable.Renderable;

public abstract class Updatable {
	
	private static int nextID = 0; //Low values reserved for specials, e.g. vessels
	protected final int id;
	public boolean keepsWorldGoing = false;

	protected Updatable() {
		id = nextID++;
	}
	
	public abstract boolean update(float deltaTime);
	public void putRenderables(List<List<Renderable>> phases) {
		Renderable r = getRenderable();
		if (r != null) {
			phases.get(r.phase.ordinal()).add(r);
		}		
	}
	public abstract Renderable getRenderable();
	
	public static void reset() {
		nextID = 0;
		Vessel.nextVesselNumber = 0;
	}
	
}
