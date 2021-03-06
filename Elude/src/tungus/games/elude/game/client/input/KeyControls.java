package tungus.games.elude.game.client.input;

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
	public Vector2 getDir(Vector2 vessel, float deltaTime) {
		dir.set(0,0);
		if (isKeyPressed(keys[0])) {
			dir.add(0, 1);
		}
		if (isKeyPressed(keys[1])) {
			dir.add(-1, 0);
		}
		if (isKeyPressed(keys[2])) {
			dir.add(0, -1);
		}
		if (isKeyPressed(keys[3])) {
			dir.add(1, 0);
		}
		dir.nor();
		return dir;
	}

	@Override
	public void draw(SpriteBatch batch, float alpha) {
		// Do nothing
	}
	
	synchronized private static boolean isKeyPressed(int k) {
		return Gdx.input.isKeyPressed(k);
	}

}
