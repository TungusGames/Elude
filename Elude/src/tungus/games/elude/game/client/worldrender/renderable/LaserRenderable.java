package tungus.games.elude.game.client.worldrender.renderable;

import tungus.games.elude.Assets.Tex;
import tungus.games.elude.game.client.worldrender.WorldRenderer;
import tungus.games.elude.game.client.worldrender.phases.RenderPhase;
import tungus.games.elude.util.LinkedPool;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class LaserRenderable extends Renderable {
	
	private static LinkedPool<LaserRenderable> pool = new LinkedPool<LaserRenderable>(LaserRenderable.class, 150);
	public static Renderable create(Vector2 start, Vector2 end) {
		//LaserRenderable laser = pool.obtain();
		//return laser;
		return Sprite.create(RenderPhase.ROCKET, Tex.LASER, 
							 (start.x + end.x) / 2, (start.y + end.y) / 2, 
							 start.dst(end)+0.5f, 0.5f,
							 (float)Math.atan2(end.y - start.y, end.x - start.x) * MathUtils.radiansToDegrees, 
							 1);
	}
	
	public LaserRenderable(LinkedPool<LaserRenderable> p) {
		super(p);
	}

	@Override
	public void render(WorldRenderer wr) {
		
	}
	
	@Override
	public Renderable clone() {
		return null;
	}
	
}
