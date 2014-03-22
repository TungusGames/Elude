package tungus.games.elude.game.input.mobile;

import tungus.games.elude.game.input.Controls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class SwipeDirControl implements Controls {

	private final OrthographicCamera interfaceCamera;
	
	private Vector2 lastTouch = new Vector2();
	private Vector2 lastVchangeTouch = new Vector2();
	private Vector2 lastV = new Vector2();
	private Vector2 v = new Vector2();
	private Vector2 v2 = new Vector2();
	private Vector3 v3 = new Vector3();
	
	private boolean touched = false;
	
	//private static final float MAX_SQUARED_DIFF = 0.1f;
	private static final float MAX_SQUARED_DIFF2 = 1f;
	
	public SwipeDirControl(OrthographicCamera cam) {
		this.interfaceCamera = cam;
	}

	@Override
	public Vector2 getDir() {
		if (!touched && Gdx.input.isTouched()) {
			v3.set((float)Gdx.input.getX(), (float)Gdx.input.getY(), 0f);
			interfaceCamera.unproject(v3);
			touched = true;
			lastVchangeTouch.set(lastTouch.set(v3.x, v3.y));
			lastV.set(0, 0);
			return v.set(lastV);
		} else if (Gdx.input.isTouched()) {
			v3.set((float)Gdx.input.getX(), (float)Gdx.input.getY(), 0f);
			interfaceCamera.unproject(v3);
			v.set(v3.x, v3.y);
			v2.set(v).sub(lastTouch);
			lastTouch.set(v);
			touched = true;
			if (v2.len2() > MAX_SQUARED_DIFF2 || lastTouch.dst2(lastVchangeTouch) > MAX_SQUARED_DIFF2) {
				lastVchangeTouch.set(lastTouch);
				return v.set(lastV.set(v2.nor()));
			}
			else return v.set(lastV);
		} else {
			touched = false;
			lastV.set(0, 0);
			return v.set(lastV);
		}
				
	}
	
	@Override
	public void draw(SpriteBatch batch) {
	}
}
