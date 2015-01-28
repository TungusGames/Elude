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
		s.id = asset.ordinal(); s.stop = false;
		return s;
	}
		
	public static Effect stop(Sounds asset) {
		SoundEffect s = pool.obtain();
		s.id = asset.ordinal(); s.stop = true;
		return s;
	}
	
	private int id;
	private boolean stop;
	
	public SoundEffect(LinkedPool<SoundEffect> p) {
		super(p);
	}
	
	@Override
	public void render(WorldRenderer wr) {
		if (stop) {
			Assets.Sounds.values()[id].s.stop();
		} else {
			Assets.Sounds.values()[id].play();
		}
		
	}
	
	@Override
	public Renderable clone() {
		return create(Assets.Sounds.values()[id]);
	}
}
