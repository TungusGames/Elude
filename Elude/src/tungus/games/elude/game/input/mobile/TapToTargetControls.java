package tungus.games.elude.game.input.mobile;

import tungus.games.elude.game.input.Controls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class TapToTargetControls implements Controls {
	
	private static final float MIN_POS_DISTANCE = 0.2f;
	private static final float MIN_POS_DISTANCE_SQ = MIN_POS_DISTANCE*MIN_POS_DISTANCE;
	
	private final OrthographicCamera gameCam;
	
	private Vector2 playerPos;
	
	private Vector2 touch2 = new Vector2();
	private Vector3 touch3 = new Vector3();

	public TapToTargetControls(OrthographicCamera cam, Vector2 playerPos) {
		gameCam = cam;
		this.playerPos = playerPos;
	}

	@Override
	public Vector2 getDir() {
		if (Gdx.input.isTouched()) {
			touch3.set((float)Gdx.input.getX(), (float)Gdx.input.getY(), 0f);
			gameCam.unproject(touch3);
			if (touch2.set(touch3.x, touch3.y).sub(playerPos).len2() > MIN_POS_DISTANCE_SQ)
				return touch2.nor();
			
		}
		return touch2.set(0,0);
	}

	@Override
	public void draw(SpriteBatch batch) {
	}

}
