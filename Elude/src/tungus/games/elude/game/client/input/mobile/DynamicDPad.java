package tungus.games.elude.game.client.input.mobile;

import tungus.games.elude.Assets;
import tungus.games.elude.game.client.input.Controls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class DynamicDPad extends Sprite implements Controls {
	
	private final OrthographicCamera gameCamera;
		
	private Vector2 center = new Vector2();
	private Vector2 v = new Vector2();
	private Vector3 v3 = new Vector3();
	
	private boolean touched = false;
	
	private static final float SIZE = 0.2f;
	
	public DynamicDPad(OrthographicCamera cam, float frustumWidth, float frustumHeight) {
		super(Assets.smallCircle);
		this.gameCamera = cam;
		setSize(SIZE, SIZE);
	}

	@Override
	public Vector2 getDir(Vector2 vessel) {
		if (!touched && Gdx.input.isTouched()) {
			v3.set((float)Gdx.input.getX(), (float)Gdx.input.getY(), 0f);
			gameCamera.unproject(v3);
			center.set(v3.x, v3.y);
			setPosition(center.x-SIZE/2, center.y-SIZE/2);
			touched = true;
			return v.set(0,0);
		} else if (Gdx.input.isTouched()) {
			v3.set((float)Gdx.input.getX(), (float)Gdx.input.getY(), 0f);
			gameCamera.unproject(v3);
			v.set(v3.x, v3.y);
			touched = true;
			return v.sub(center).nor();
		} else {
			touched = false;
			return v.set(0,0);
		}
				
	}
	
	@Override
	public void draw(SpriteBatch batch, float alpha) {
		if (touched)
			super.draw(batch, alpha);
	}

}
