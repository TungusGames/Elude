package tungus.games.elude.debug;

import java.util.Arrays;

import tungus.games.elude.Assets;
import tungus.games.elude.BaseScreen;
import tungus.games.elude.util.ViewportHelper;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;

public class ShaderTestScreen extends BaseScreen {
	
	private final ShaderProgram shader;
	private final SpriteBatch batch;
	private final OrthographicCamera cam;
	
	private float timeSinceInc = 0;
	
	private float[] time = new float[]{0.1f, 0.2f, 1.5f, 1.56f};
	private float[] centers = new float[]{10, 6, 12, 10, 4, 8, 16, 5};
	private static final int MAX_SIZE = 100;
	
	public ShaderTestScreen(Game game) {
		super(game);
		shader = new ShaderProgram(Gdx.files.internal("shaders/minevertex"),
								   Gdx.files.internal("shaders/minefragment"));
		Gdx.app.log("Shader", ""+shader.getLog());
		shader.begin();
		shader.setUniformf("worldSize", 20, 12);
		shader.setUniformf("viewportSize", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		shader.setUniformf("center", 10, 6);
		shader.setUniformf("R", 5);
		shader.setUniform2fv("center[0]", centers, 0, centers.length);
		shader.setUniform1fv("time[0]", time, 0, time.length);
		shader.setUniformi("L", 4);
		shader.end();
		cam = ViewportHelper.newCamera(20, 12);
		batch = new SpriteBatch();
		batch.setProjectionMatrix(cam.combined);
		batch.setColor(1, 1, 1, 1);
		batch.setShader(shader);
	}
	
	@Override
	public void render(float deltaTime)
	{
		for (int i = 0; i < time.length; i++) {
			time[i] += deltaTime;
		}
		timeSinceInc += deltaTime;
		batch.begin();
		if (timeSinceInc > 0.3f && time.length < MAX_SIZE || time.length < 80) {
			timeSinceInc = 0;
			addSpot();
		}
		shader.setUniform1fv("time[0]", time, 0, time.length);
		batch.draw(Assets.whiteRectangle, 0, 0, 20, 12);
		batch.end();
	}

	private void addSpot() {
		time = Arrays.copyOf(time, time.length+1);
		time[time.length-1] = 0;
		centers = Arrays.copyOf(centers, centers.length+2);
		centers[centers.length-2] = MathUtils.random(3, 17);
		centers[centers.length-1] = MathUtils.random(3, 9);
		shader.setUniform2fv("center[0]", centers, 0, centers.length);
		shader.setUniform1fv("time[0]", time, 0, time.length);
		shader.setUniformi("L", time.length);
		Gdx.app.log("Spots", ""+time.length);
	}
}