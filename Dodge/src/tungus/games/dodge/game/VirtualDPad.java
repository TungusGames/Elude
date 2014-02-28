package tungus.games.dodge.game;

import tungus.games.dodge.Assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class VirtualDPad extends Sprite {
	
	private static final float SIZE = 4f;
	private static final float DISTANCE_FROM_EDGE = 1f;
	
	private final Circle circle; 
	
	private boolean pressed = false; // Has been pressed inside the circle
	
	public VirtualDPad(float frustumWidth, float frustumHeight) {
		super(Assets.virtualDPadPerimeter);
		circle = new Circle(frustumWidth - SIZE/2 - DISTANCE_FROM_EDGE, DISTANCE_FROM_EDGE + SIZE/2, SIZE/2);
		setBounds(frustumWidth - SIZE - DISTANCE_FROM_EDGE, DISTANCE_FROM_EDGE, SIZE, SIZE);
	}


	public Vector2 getDirections(OrthographicCamera interfaceCamera) {
		Vector2 v = new Vector2(0, 0);
		if (Gdx.input.justTouched()) {
			Vector3 dir = new Vector3((float)Gdx.input.getX(), (float)Gdx.input.getY(), 0f);
			interfaceCamera.unproject(dir);
			v.set(dir.x, dir.y);
			if (circle.contains(v)) {
				pressed = true;
			}
		}
		if (pressed && Gdx.input.isTouched()) {
			if (!Gdx.input.justTouched()) {
				Vector3 dir = new Vector3((float)Gdx.input.getX(), (float)Gdx.input.getY(), 0f);
				interfaceCamera.unproject(dir);
				v.set(dir.x, dir.y);
			}
			pressed = true;
			return v.sub(circle.x, circle.y).clamp(0, 1);
		} else {
			pressed = false;
		}
		return v.set(0, 0);
	}

}
