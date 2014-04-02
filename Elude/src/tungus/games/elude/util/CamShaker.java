package tungus.games.elude.util;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class CamShaker {
	
	public static CamShaker INSTANCE;
	
	private final OrthographicCamera cam;
	private final SpriteBatch batch;
	
	private float timeSinceStart = 0;
	private float totalTime = -1;
	private float intensity = 0;
	
	private final Vector2 originalPos = new Vector2();
	private final Vector2 offsetPos = new Vector2();
	private final Vector2 temp = new Vector2();
	
	public CamShaker(OrthographicCamera cam, SpriteBatch batch) {
		this.cam = cam;
		this.batch = batch;
	}
	
	public void shake(float time, float newIntensity) {
		totalTime = time;
		timeSinceStart = 0;
		intensity = newIntensity;
		offsetPos.set(originalPos.set(cam.position.x, cam.position.y));
	}
	
	public void update(float deltaTime) {
		if (timeSinceStart < totalTime) {
			timeSinceStart += deltaTime;
			offsetPos.add(MathUtils.random(-1, 1)*intensity*deltaTime, MathUtils.random(-1, 1)*intensity*deltaTime);
			float partDone = timeSinceStart / totalTime;
			if (partDone < 0.5f)
				partDone *= 2;
			else
				partDone = (1-partDone)*2;
			cam.position.set(temp.set(originalPos).lerp(offsetPos, partDone), 0);
			cam.update();
			batch.setProjectionMatrix(cam.combined);
		} else if (totalTime != -1) {
			cam.position.set(originalPos, 0);
			cam.update();
			batch.setProjectionMatrix(cam.combined);
			totalTime = -1;
		}
		
	}
}
