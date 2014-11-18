package tungus.games.elude.game.client.worldrender;

import tungus.games.elude.Assets;
import tungus.games.elude.Assets.Sounds;
import tungus.games.elude.game.client.worldrender.Renderable.Effect;
import tungus.games.elude.util.LinkedPool;
import tungus.games.elude.util.LinkedPool.Poolable;

public class SoundEffect extends Poolable implements Effect {
	private static LinkedPool<SoundEffect> pool = new LinkedPool<SoundEffect>(SoundEffect.class, 15);
	public static Effect create(Sounds asset) {
		SoundEffect s = pool.obtain();
		s.id = asset.ordinal();
		return s;
	}
	private int id;
	public SoundEffect(LinkedPool<SoundEffect> p) {
		super(p);
	}
	
	@Override
	public void render(WorldRenderer wr) {
		Assets.Sounds.values()[id].s.play();
	}
	
	@Override
	public Renderable clone() {
		return create(Assets.Sounds.values()[id]);
	}
}
