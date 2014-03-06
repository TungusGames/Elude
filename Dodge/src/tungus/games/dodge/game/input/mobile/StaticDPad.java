package tungus.games.dodge.game.input.mobile;

import tungus.games.dodge.Assets;
import tungus.games.dodge.game.input.Controls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class StaticDPad extends Sprite implements Controls {
	
	private final OrthographicCamera interfaceCamera;
	
	private static final float SIZE = 1.7f;
	private static final float DISTANCE_FROM_EDGE = 0.35f;
	
	private final Circle circle; 
	private Vector2 v = new Vector2();
	private Vector3 v3 = new Vector3();
	private boolean pressed = false; // Has been pressed inside the circle
	
	public StaticDPad(OrthographicCamera cam, float frustumWidth, float frustumHeight) {
		super(Assets.virtualDPadPerimeter);
		circle = new Circle(frustumWidth - SIZE/2 - DISTANCE_FROM_EDGE, DISTANCE_FROM_EDGE + SIZE/2, SIZE/2);
		setBounds(frustumWidth - SIZE - DISTANCE_FROM_EDGE, DISTANCE_FROM_EDGE, SIZE, SIZE);
		this.interfaceCamera = cam;
	}

	@Override
	public Vector2 getDir() {
		v.set(0,0);
		if (Gdx.input.justTouched()) {
			v3.set((float)Gdx.input.getX(), (float)Gdx.input.getY(), 0f);
			interfaceCamera.unproject(v3);
			v.set(v3.x, v3.y);
			if (circle.contains(v)) {
				pressed = true;
			}
		}
		if (pressed && Gdx.input.isTouched()) {
			if (!Gdx.input.justTouched()) {
				v3.set((float)Gdx.input.getX(), (float)Gdx.input.getY(), 0f);
				interfaceCamera.unproject(v3);
				v.set(v3.x, v3.y);
			}
			pressed = true;
			return v.sub(circle.x, circle.y).nor();
		} else {
			pressed = false;
		}
		return v.set(0, 0);
	}

}
