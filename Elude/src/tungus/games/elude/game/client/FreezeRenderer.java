package tungus.games.elude.game.client;

import tungus.games.elude.Assets;
import tungus.games.elude.game.server.World;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;

public class FreezeRenderer {
	
	private static final float MAX_ALPHA = 0.75f;
	private static final float MAX_SIZE = 40f; // Radius
	private static final float FADE_TIME = 0.25f; 
	private static final float SIZE_SPEED = MAX_SIZE / FADE_TIME; // Enlarge in the same time as fadein
	private static final Color color = new Color(0, 1f, 1, 1);
	
	private float timeLeft = 0;
	private float alpha = 0;
	private float size = 0;
	private Vector2 center = new Vector2();
	private final SpriteBatch batch;
	public boolean active = false;
	public ShaderProgram enemyShader = Assets.freezeEnemy;
	
	public FreezeRenderer() {
		batch = new SpriteBatch(1);
		OrthographicCamera cam = new OrthographicCamera(World.WIDTH, World.HEIGHT);
		cam.position.set(World.WIDTH/2, World.HEIGHT/2, 0);
		cam.update();
		batch.setProjectionMatrix(cam.combined);
	}
	
	public void turnOn(float x, float y, float time) {
		if (timeLeft <= 0) {
			center.set(x, y);
			active = true;
		}
		timeLeft = time;
	}
	
	public void render(float delta) {
		timeLeft -= delta;
		if (timeLeft < 0) {
			alpha = size = 0;
			active = false;
			return;
		}
		
		size = Math.min(MAX_SIZE, size + SIZE_SPEED * delta);
		if (timeLeft < FADE_TIME) {
			alpha = timeLeft / FADE_TIME * MAX_ALPHA;
		} else {
			alpha = Math.min(MAX_ALPHA, alpha + delta / FADE_TIME);
		}
		enemyShader.begin();
		enemyShader.setUniformf("a", alpha/MAX_ALPHA);
		enemyShader.end();
		
		batch.setColor(color.r, color.g, color.b, alpha);
		batch.begin();
		batch.draw(Assets.linearGradientSpot, center.x - size/2, center.y - size/2, size, size);
		batch.end();
	}
	
	
}
