package tungus.games.elude.game.client.worldrender;

import tungus.games.elude.Assets;
import tungus.games.elude.Assets.Tex;
import tungus.games.elude.util.LinkedPool;

public class Sprite extends Renderable {
	
	private static LinkedPool<Sprite> pool = new LinkedPool<Sprite>(Sprite.class, 300);
	public static Renderable create(RenderPhase phase, Tex tex, float x, float y, float width, float height, float rot, float alpha) {
		Sprite s = pool.obtain();
		s.x = x; s.y = y; s.width = width; s.height = height; s.texID = tex.ordinal(); s.rot = rot; s.alpha = alpha;
		s.phase = phase;
		return s;
	}
	
	public float x, y;
	protected float width, height, rot, alpha;
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
		return create(phase, Assets.Tex.values()[texID], x, y, width, height, rot, alpha);
	}
}
