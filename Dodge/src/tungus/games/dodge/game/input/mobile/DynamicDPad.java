package tungus.games.dodge.game.input.mobile;

import tungus.games.dodge.game.input.Controls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class DynamicDPad implements Controls {
	
	private final OrthographicCamera interfaceCamera;
		
	private Vector2 center = new Vector2();
	private Vector2 v = new Vector2();
	private Vector3 v3 = new Vector3();
	
	private boolean touched = false;
	
	public DynamicDPad(OrthographicCamera cam, float frustumWidth, float frustumHeight) {
		this.interfaceCamera = cam;
	}

	@Override
	public Vector2 getDir() {
		if (!touched && Gdx.input.justTouched()) {
			v3.set((float)Gdx.input.getX(), (float)Gdx.input.getY(), 0f);
			interfaceCamera.unproject(v3);
			center.set(v3.x, v3.y);
			touched = true;
			return v.set(0,0);
		} else if (Gdx.input.isTouched()) {
			v3.set((float)Gdx.input.getX(), (float)Gdx.input.getY(), 0f);
			interfaceCamera.unproject(v3);
			v.set(v3.x, v3.y);
			touched = true;
			return v.sub(center).nor();
		} else {
			touched = false;
			return v.set(0,0);
		}
				
	}

	@Override
	public void draw(SpriteBatch batch) {
		// Do nothing
	}

}