package tungus.games.elude.debug;

import tungus.games.elude.BaseScreen;
import tungus.games.elude.game.client.worldrender.MineRenderer;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class MineEffectTestScreen extends BaseScreen {
	
	private MineRenderer mines = new MineRenderer();
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
			mines.add(0, 6, 8, delta);
		} else if (time < 12) {
			time = time + 0;
		} else if (time < 16) {
			mines.add(1, 8, 8, delta);
			mines.add(2, 16, 4, delta);
		} else if (time < 20) {
			mines.add(2, 16, 4, delta);
			mines.add(3, 2, 7, delta);
			mines.add(4, 5, 10, delta);
		} else if (time < 24) {
			mines.add(4, 5, 10, delta);
		} else if (time < 28) {
			;
		} else {
			mines.add(5, 6, 7, delta);
		}
		mines.render(delta, 1);
	}
}
