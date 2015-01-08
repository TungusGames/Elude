package tungus.games.elude.game.client.worldrender.renderable;

import tungus.games.elude.Assets.Tex;
import tungus.games.elude.game.client.worldrender.WorldRenderer;
import tungus.games.elude.game.client.worldrender.lastingeffects.EnemyHealthbar;
import tungus.games.elude.game.client.worldrender.phases.RenderPhase;
import tungus.games.elude.util.LinkedPool;

public class EnemyRenderable extends Sprite {
	
	private static LinkedPool<Sprite> pool = new LinkedPool<Sprite>(EnemyRenderable.class, 2);
	public static Renderable create(int id, float hp, Tex tex, float x, float y, float width, float height, float rot) {
		EnemyRenderable e = (EnemyRenderable)pool.obtain();
		e.x = x; e.y = y; e.height = height; e.width = width; e.rot = rot; e.texID = tex.ordinal(); 
		e.id = id; e.hp = hp; e.alpha = 1; e.phase = RenderPhase.ENEMY;
		return e;
	}
	
	private int id;
	private float hp;
	
	public EnemyRenderable(LinkedPool<Sprite> p) {
		super(p);
	}
	
	@Override
	public void render(WorldRenderer wr) {
		super.render(wr);
		EnemyHealthbar healthbar = (EnemyHealthbar)(wr.lastingEffects.getFirst(id));
		if (healthbar == null) {
			healthbar = new EnemyHealthbar();
			wr.lastingEffects.put(id, healthbar);
		}
		healthbar.exactHP = hp;
		healthbar.enemyPos.set(x, y);
		healthbar.enemyInNewFrame = true;
	}
	
	@Override
	public Renderable clone() {
		return create(id, hp, Tex.values()[texID], x, y, width, height, rot);
	}

}
