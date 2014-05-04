package tungus.games.elude.menu;

import tungus.games.elude.Assets;
import tungus.games.elude.BaseScreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Array;

public class MainMenu extends BaseScreen {

	
	private float logoOffTime = 0f;
	private Sprite eludeOn = new Sprite(Assets.eludeTitleOn);
	private Sprite eludeOff = new Sprite(Assets.eludeTitleOff);
	private final float FRUSTUM_WIDTH = Gdx.graphics.getWidth();
	private final float FRUSTUM_HEIGHT = Gdx.graphics.getHeight();
	private OrthographicCamera camera;
	private SpriteBatch spriteBatch;
	private boolean paused = false;
	private Array<Label> labels;
	private Array<Rectangle> buttonBounds;
	
	public MainMenu(final Game game) {
		super(game);
		Gdx.input.setCatchBackKey(false);
		camera = new OrthographicCamera(FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
		camera.position.set(FRUSTUM_WIDTH/2, FRUSTUM_HEIGHT/2, 0);
		camera.update();
		spriteBatch = new SpriteBatch();
		spriteBatch.setProjectionMatrix(camera.combined);
		float height = FRUSTUM_WIDTH * 0.28f;
		float y = FRUSTUM_HEIGHT - height; 
		eludeOn.setBounds(0, y, FRUSTUM_WIDTH, height);
		eludeOff.setBounds(0, y, FRUSTUM_WIDTH, height);
		initButtons(y);		
		Gdx.input.setInputProcessor(new InputAdapter(){
			@Override
			public boolean touchUp (int screenX, int screenY, int pointer, int button) {
				float x = (float)screenX;
				float y = FRUSTUM_HEIGHT - (float)screenY;
				Gdx.app.log("pointX", x + "");
				Gdx.app.log("pointY", y + "");
				if (buttonBounds.get(0).contains(x, y)) {
					game.setScreen(new PlayMenu(game));
					return true;
				}
				if (buttonBounds.get(1).contains(x, y)) {
					game.setScreen(new PlayMenu(game));
					return true;
				}
				if (buttonBounds.get(2).contains(x, y)) {
					game.setScreen(new PlayMenu(game));
					return true;
				}
				return false;
			}
		});
		//Assets.neonSound.loop();
	}
	@Override
	public void render(float deltaTime) {
		if (!paused) {
			Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
			spriteBatch.begin();				
			if (logoOffTime <= 0f) {
				eludeOn.draw(spriteBatch);
				if (MathUtils.randomBoolean(0.01f)) {
					logoOffTime = 0.1f;
					//Assets.neonSound.stop();
					Assets.neonFlicker.play();
				}
			} else {
				eludeOff.draw(spriteBatch);
				logoOffTime -= deltaTime;
				if (logoOffTime <= 0f) ;
					//Assets.neonSound.loop();
			}
			for (Rectangle r : buttonBounds) 
				Assets.frame9p.draw(spriteBatch, r.x, r.y, r.width, r.height);
			for (Label l : labels)
				l.draw(spriteBatch, 1);
			spriteBatch.end();
		}
		/*if (logoOffTime <= 0f)
			eludeOn.draw(spriteBatch);
		else eludeOff.draw(spriteBatch);*/
	}
	
	@Override
	public void pause() {
		//Assets.neonSound.pause();
		paused = true;
		super.pause();
	}
	
	@Override
	public void resume() {
		super.resume();
		paused = false;
		//Assets.neonSound.resume();
	}
	
	private void initButtons(float heightRemainder) {
		labels = new Array<Label>();
		buttonBounds = new Array<Rectangle>();
		labels.add(new Label("PLAY", new LabelStyle(Assets.font, null)));
		labels.add(new Label("SETTINGS", new LabelStyle(Assets.font, null)));
		labels.add(new Label("HELP", new LabelStyle(Assets.font, null)));
		for (int i = 0; i < 3; i++) { //Center the buttons
			float buttonWidth = labels.get(i).getPrefWidth() + 30;
			float buttonHeight = labels.get(i).getPrefHeight() + 30;
			float buttonX = (FRUSTUM_WIDTH - buttonWidth) / 2;
			float buttonY = heightRemainder - buttonHeight;
			labels.get(i).setPosition(buttonX + 15, buttonY + 15);
			buttonBounds.add(new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight));
			heightRemainder -= buttonHeight;
		}
	}
}
