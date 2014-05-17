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
	
	private static final float SIZE = 1.2f;
	private static final float FADE_TIME = 0.3f;
	
	private static final int STATE_IN = 0;
	private static final int STATE_ACTIVE = 1;
	private static final int STATE_OUT = 2;
	
	private final OrthographicCamera gameCamera;
	private final OrthographicCamera uiCamera;
		
	private Vector2 center = new Vector2();
	private Vector2 v = new Vector2();
	private Vector3 v3 = new Vector3();
	
	private boolean touched = false;
	private int state = STATE_OUT;
	private float stateTime = FADE_TIME;
	
	public DynamicDPad(OrthographicCamera gameCam, OrthographicCamera uiCam, float frustumWidth, float frustumHeight) {
		super(Assets.virtualDPadPerimeter);
		this.gameCamera = gameCam;
		this.uiCamera = uiCam;
		setSize(SIZE, SIZE);
		
	}

	@Override
	public Vector2 getDir(Vector2 vessel, float deltaTime) {
		stateTime += deltaTime;
		if (!touched && Gdx.input.isTouched()) {
			// Get the dir on game cam
			v3.set((float)Gdx.input.getX(), (float)Gdx.input.getY(), 0f);
			gameCamera.unproject(v3);
			center.set(v3.x, v3.y);
			touched = true;
			// Get the pos on UI cam
			v3.set((float)Gdx.input.getX(), (float)Gdx.input.getY(), 0f);
			uiCamera.unproject(v3);
			setPosition(v3.x-SIZE/2, v3.y-SIZE/2);
			
			state = STATE_IN;
			stateTime = 0;
			return v.set(0,0);
		} else if (Gdx.input.isTouched()) {
			if (stateTime > FADE_TIME) {
				state = STATE_ACTIVE;
				stateTime = 0;
			}
			v3.set((float)Gdx.input.getX(), (float)Gdx.input.getY(), 0f);
			gameCamera.unproject(v3);
			v.set(v3.x, v3.y);
			touched = true;
			return v.sub(center).nor();
		} else {
			touched = false;
			if (state != STATE_OUT) {
				state = STATE_OUT;
				stateTime = 0;
			}
			if (stateTime > FADE_TIME) {
				stateTime = FADE_TIME;
			}
			return v.set(0,0);
		}
				
	}
	
	@Override
	public void draw(SpriteBatch batch, float alpha) {
		setColor(1, 1, 1, 	state == STATE_IN ? stateTime/FADE_TIME :
			state == STATE_OUT ? 1-stateTime/FADE_TIME :
			1);
		super.draw(batch, alpha);
	}

}
