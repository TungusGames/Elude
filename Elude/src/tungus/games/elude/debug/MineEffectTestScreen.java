package tungus.games.elude.debug;

import tungus.games.elude.BaseScreen;
import tungus.games.elude.game.client.MineEffects;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class MineEffectTestScreen extends BaseScreen {
	
	private MineEffects mines = new MineEffects(100);
	private float time = 0;
	
	public MineEffectTestScreen(Game game) {
		super(game);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		time += delta;
		mines.clear();
		if (time < 4) {
			;
		} else if (time < 8) {
			mines.add(0, 6, 8);
		} else if (time < 12) {
			;
		} else if (time < 16) {
			mines.add(1, 8, 8);
			mines.add(2, 16, 4);
		} else if (time < 20) {
			mines.add(2, 16, 4);
			mines.add(3, 2, 7);
			mines.add(4, 5, 10);
		} else if (time < 24) {
			mines.add(4, 5, 10);
		} else if (time < 28) {
			;
		} else {
			mines.add(5, 6, 7);
		}
		mines.render(delta, 1);
	}
}
