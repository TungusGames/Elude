package tungus.games.elude.game.client.worldrender;

import tungus.games.elude.Assets;
import tungus.games.elude.util.LinkedPool;
import tungus.games.elude.util.LinkedPool.Poolable;

public class Sprite extends Poolable implements Renderable {
	
	private static LinkedPool<Sprite> pool = new LinkedPool<Sprite>(Sprite.class, 300);
	public static Renderable create(int t, float x, float y, float w, float h, float r, float a) {
		Sprite s = pool.obtain();
		s.x = x; s.y = y; s.width = w; s.height = h; s.texID = t; s.rot = r; s.alpha = a;
		return s;
	}
	
	protected float x, y, width, height, rot, alpha;
	protected int texID;
	
	public Sprite(LinkedPool<Sprite> p) {
		super(p);
	}

	@Override
	public void render(WorldRenderer wr) {
		wr.batch.setColor(1, 1, 1, wr.batch.getColor().a * alpha);
		wr.batch.draw(Assets.Tex.values()[texID].t, x-width/2, y-height/2, width/2, height/2, width, height, 1, 1, rot);
		wr.batch.setColor(1, 1, 1, wr.batch.getColor().a / alpha);
	}
	
	@Override
	public Renderable clone() {
		return create(texID, x, y, width, height, rot, alpha);
	}
}
