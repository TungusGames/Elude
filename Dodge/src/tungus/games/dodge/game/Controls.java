package tungus.games.dodge.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Controls {

	private Vector2 dir;
	
	private int[] keys = null;							//Desktop and WebGL
	
	private VirtualDPad dPad = null;					//Android and iOS
	private OrthographicCamera interfaceCamera = null;
	
	/*
	 * For Desktop and WebGL only
	 */
	public Controls(int[] keys) {
		dir = new Vector2(0, 0);
		this.keys = keys;
	}
	
	/*
	 * For Android and iOS only
	 */
	public Controls(OrthographicCamera interfaceCamera) {
		dir = new Vector2(0, 0);
		dPad = new VirtualDPad();
		this.interfaceCamera = interfaceCamera; 
	}
	
	public Vector2 getDirection(Float deltaTime) {
		dir.set(0, 0);
		
		switch (Gdx.app.getType()) {
		case Desktop:
		case WebGL:
			/*if (Gdx.input.isKeyPressed(keys[0])) {
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
		case iOS:*/
			dir = dPad.getDirections(interfaceCamera);
			break;
		default:
			break;
		}
		return dir;
	}
	
	/*
	 * For Android and iOS only
	 */
	public void renderDPad(SpriteBatch batch) {
		dPad.draw(batch);
	}

}
