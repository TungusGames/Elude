package tungus.games.elude.menu;

import tungus.games.elude.Assets;
import tungus.games.elude.BaseScreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class MainMenu extends BaseScreen {

	
	private float logoOffTime = 0f;
	private Sprite eludeOn = new Sprite(Assets.eludeTitleOn);
	private Sprite eludeOff = new Sprite(Assets.eludeTitleOff);
	private SpriteBatch spriteBatch = new SpriteBatch();
	private final float FRUSTUM_WIDTH = (float)Gdx.graphics.getWidth() / Gdx.graphics.getPpcX();
	private final float FRUSTUM_HEIGHT = (float)Gdx.graphics.getHeight() / Gdx.graphics.getPpcY();
	private OrthographicCamera camera = new OrthographicCamera(FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
	private Sound flicker= Gdx.audio.newSound(Assets.neonFlicker);
	//private Sound sound = Gdx.audio.newSound(Assets.neonSound);
	private boolean paused = false;
	
	public MainMenu(Game game) {
		super(game);
		Gdx.input.setCatchBackKey(false);
		camera.position.set(FRUSTUM_WIDTH/2, FRUSTUM_HEIGHT/2, 0);
		camera.update();
		spriteBatch.setProjectionMatrix(camera.combined);
		float height = FRUSTUM_WIDTH * 0.28f;
		float y = FRUSTUM_HEIGHT - height; 
		eludeOn.setBounds(0, y, FRUSTUM_WIDTH, height);
		eludeOff.setBounds(0, y, FRUSTUM_WIDTH, height);
		//sound.loop();
	}
	@Override
	public void render(float deltaTime) {
		if (!paused) {
			if (Gdx.input.isTouched()) {
				Screen next = new PlayMenu(game);
				//sound.stop();
				game.setScreen(next);
			}
			spriteBatch.begin();
			Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
			Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
			spriteBatch.begin();				
			if (logoOffTime <= 0f) {
				eludeOn.draw(spriteBatch);
				if (MathUtils.randomBoolean(0.01f)) {
					logoOffTime = 0.1f;
					//sound.stop();
					flicker.play();
				}
			} else {
				eludeOff.draw(spriteBatch);
				logoOffTime -= deltaTime;
				if (logoOffTime <= 0f) ;
					//sound.loop();
			}
			spriteBatch.end();
		}
		/*if (logoOffTime <= 0f)
			eludeOn.draw(spriteBatch);
		else eludeOff.draw(spriteBatch);*/
	}
	
	@Override
	public void pause() {
		//sound.pause();
		paused = true;
		super.pause();
	}
	
	@Override
	public void resume() {
		super.resume();
		paused = false;
		//sound.resume();
	}
}
