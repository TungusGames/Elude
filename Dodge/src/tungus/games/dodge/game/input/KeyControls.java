package tungus.games.dodge.game.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class KeyControls implements Controls {
	
	private final int[] keys;
	private Vector2 dir = new Vector2();
	
	public KeyControls(int[] keys) {
		this.keys = keys;
	}

	@Override
	public Vector2 getDir() {
		if (Gdx.input.isKeyPressed(keys[0])) {
			dir.add(0, 1);
		}
		if (Gdx.input.isKeyPressed(keys[1])) {
			dir.add(-1, 0);
		}
		if (Gdx.input.isKeyPressed(keys[2])) {
			dir.add(0, -1);
		}
		if (Gdx.input.isKeyPressed(keys[3])) {
			dir.add(1, 0);
		}
		dir.nor();
		return dir;
	}

	@Override
	public void draw(SpriteBatch batch) {
		// Do nothing
	}

}
