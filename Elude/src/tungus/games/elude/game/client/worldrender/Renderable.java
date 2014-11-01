package tungus.games.elude.game.client.worldrender;


public interface Renderable {
	public void render(WorldRenderer wr);
	public Renderable clone();
	
	/**
	 * Class for renderables which last one frame, i.e. renderables that shouldn't be cleared from an unhandled previous frame.
	 * Provides no extra functionality, just serves for separating these.
	 */
	public static interface Effect extends Renderable {}
}
