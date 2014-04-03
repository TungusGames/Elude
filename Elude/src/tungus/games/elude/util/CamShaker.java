package tungus.games.elude.util;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

public class CamShaker {
	
	public static CamShaker INSTANCE;
	
	private static final float MAX_OFFSET = 0.5f;
	
	private final SpriteBatch batch;
	
	private float timeSinceStart = 0;
	private float totalTime = -1;
	private float intensity = 0;
	private float partDone = 0;
	private float beginPartDone = 0;
	
	private final Vector2 interpOffset = new Vector2();
	private final Vector2 offset = new Vector2();
	
	private final Matrix4 mat = new Matrix4();
	
	public CamShaker(SpriteBatch batch) {
		this.batch = batch;
	}
	
	public void shake(float time, float newIntensity) {
		totalTime = time;
		timeSinceStart = 0;
		intensity = newIntensity;
		beginPartDone = partDone;
	}
	
	public void update(float deltaTime) {
		if (timeSinceStart < totalTime) {
			timeSinceStart += deltaTime;
			float xMod = offset.x / MAX_OFFSET;
			float yMod = offset.y / MAX_OFFSET;
			offset.add(MathUtils.random(-1-xMod, 1-xMod)*intensity*deltaTime, MathUtils.random(-1-yMod, 1-yMod)*intensity*deltaTime);
			
			partDone = timeSinceStart / totalTime;
			if (partDone < 0.5f) {
				partDone = beginPartDone + (1-beginPartDone)*partDone*2;
			}
			else {
				partDone = (1-partDone)*2;
			}
			interpOffset.set(0,0).lerp(offset, partDone);
			updateGL();
			
		} else if (totalTime != -1) {
			interpOffset.set(0,0);
			updateGL();
			totalTime = -1;
			partDone = 0;
			offset.set(0,0);
		}
	}
	
	private void updateGL() {
		//cam.position.set(temp, 0);
		//cam.update();
		//batch.setProjectionMatrix(cam.combined);
		batch.setTransformMatrix(mat.setToTranslation(interpOffset.x, interpOffset.y, 0));
	}
}
