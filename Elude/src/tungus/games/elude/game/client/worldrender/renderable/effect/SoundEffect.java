package tungus.games.elude.game.client.worldrender.renderable.effect;

import tungus.games.elude.Assets;
import tungus.games.elude.Assets.Sounds;
import tungus.games.elude.game.client.worldrender.WorldRenderer;
import tungus.games.elude.game.client.worldrender.renderable.Renderable;
import tungus.games.elude.game.client.worldrender.renderable.Renderable.Effect;
import tungus.games.elude.util.LinkedPool;

public class SoundEffect extends Effect {
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
