package tungus.games.elude.game.client.worldrender;

import tungus.games.elude.game.client.worldrender.Renderable.Effect;
import tungus.games.elude.menu.settings.Settings;
import tungus.games.elude.util.LinkedPool;

import com.badlogic.gdx.Gdx;

public class CamShake extends Effect {
	private static LinkedPool<CamShake> pool = new LinkedPool<CamShake>(CamShake.class, 5);
	public static Effect create() {
		CamShake p = pool.obtain();
		return p;
	}
	
	public CamShake(LinkedPool<CamShake> p) {
		super(p);
	}

	@Override
	public void render(WorldRenderer wr) {
		if (Settings.INSTANCE.vibrateOn) {
			Gdx.input.vibrate(100);
		}
		wr.camShaker.shake(0.65f, 10f);
	}
	
	@Override
	public Renderable clone() {
		return create();
	}
}
