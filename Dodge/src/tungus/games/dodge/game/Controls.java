package tungus.games.dodge.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;

public class Controls {

	private Vector2 dir;
	private int[] keys;
	
	public Controls(int[] keys) {
		dir = new Vector2(0, 0);
		this.keys = keys;
	}
	
	public Controls () {
		dir = new Vector2(0, 0);
		this.keys = new int[]{Keys.W, Keys.A, Keys.S, Keys.D};
	}
	
	public Vector2 getDirection() {
		dir.set(0, 0);
		
		switch (Gdx.app.getType()) {
		case Desktop:
		case WebGL:
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
			break;
		case Android:
		case iOS:			
			break;
		default:
			break;
		}
		return dir;
	}

}
