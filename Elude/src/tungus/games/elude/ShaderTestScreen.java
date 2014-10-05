package tungus.games.elude;

import tungus.games.elude.util.ViewportHelper;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class ShaderTestScreen extends BaseScreen {
	
	private final ShaderProgram shader;
	private final SpriteBatch batch;
	private final OrthographicCamera cam;
	
	
	private float[] time = new float[]{0.1f, 0.2f, 1.5f, 1.56f};
	private float[] centers = new float[]{10, 6, 12, 10, 4, 8, 16, 5};
	
	public ShaderTestScreen(Game game) {
		super(game);
		shader = new ShaderProgram(Gdx.files.internal("shaders/minevertex"),
								   Gdx.files.internal("shaders/minefragment"));
		Gdx.app.log("Shader", ""+shader.getLog());
		shader.begin();
		shader.setUniformf("worldSize", 20, 12);
		shader.setUniformf("viewportSize", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		shader.setUniformf("center", 10, 6);
		shader.setUniformf("R", 2);
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
		batch.begin();
		//shader.setUniformf("time", time);
		shader.setUniform1fv("time[0]", time, 0, time.length);
		batch.draw(Assets.whiteRectangle, 0, 0, 20, 12);
		batch.end();
	}
}