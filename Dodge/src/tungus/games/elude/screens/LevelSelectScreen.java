package tungus.games.elude.screens;

import tungus.games.elude.Assets;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class LevelSelectScreen extends BaseScreen {
	
	private final float FRUSTUM_WIDTH;
	private final float FRUSTUM_HEIGHT;
	private final OrthographicCamera uiCam;
	private final SpriteBatch uiBatch;
	
	private final Rectangle level1Button;
	private final Rectangle level2Button;
	private final Rectangle level3Button;
	private final Rectangle survivalButton;
	
	private final Vector3 touch3 = new Vector3();
	private final Vector2 touch2 = new Vector2();
	
	public LevelSelectScreen(Game game) {
		super(game);
		FRUSTUM_WIDTH = (float)Gdx.graphics.getWidth() / Gdx.graphics.getPpcX();
		FRUSTUM_HEIGHT = (float)Gdx.graphics.getHeight() / Gdx.graphics.getPpcY();
		uiCam = new OrthographicCamera(FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
		uiCam.position.set(FRUSTUM_WIDTH/2, FRUSTUM_HEIGHT/2, 0);
		uiCam.update();
		uiBatch = new SpriteBatch();
		uiBatch.setProjectionMatrix(uiCam.combined);
		level1Button = new Rectangle(1, FRUSTUM_HEIGHT-2, 1, 1);
		level2Button = new Rectangle(3, FRUSTUM_HEIGHT-2, 1, 1);
		level3Button = new Rectangle(5, FRUSTUM_HEIGHT-2, 1, 1);
		survivalButton = new Rectangle(2, FRUSTUM_HEIGHT-4, 1, 1);
	}
	
	@Override
	public void render(float deltaTime) {
		if (Gdx.input.isTouched()) {
			uiCam.unproject(touch3.set(Gdx.input.getX(), Gdx.input.getY(), 0));
			if (level1Button.contains(touch2.set(touch3.x, touch3.y)))
				game.setScreen(new GameScreen(game, 1));
			else if (level2Button.contains(touch2))
				game.setScreen(new GameScreen(game, 2));
			else if (level3Button.contains(touch2))
				game.setScreen(new GameScreen(game, 3));
			else if (survivalButton.contains(touch2))
				game.setScreen(new GameScreen(game, 50));
		}
		
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		uiBatch.begin();
		uiBatch.draw(Assets.whiteRectangle, level1Button.x, level1Button.y, level1Button.width, level1Button.height);
		uiBatch.draw(Assets.whiteRectangle, level2Button.x, level2Button.y, level2Button.width, level2Button.height);
		uiBatch.draw(Assets.whiteRectangle, level3Button.x, level3Button.y, level3Button.width, level3Button.height);
		uiBatch.draw(Assets.whiteRectangle, survivalButton.x, survivalButton.y, survivalButton.width, survivalButton.height);
		uiBatch.end();
	}

}
