package tungus.games.elude.menu.mainmenu;

import tungus.games.elude.Assets;
import tungus.games.elude.BaseScreen;
import tungus.games.elude.Elude;
import tungus.games.elude.Assets.EludeMusic;
import tungus.games.elude.menu.AboutScreen;
import tungus.games.elude.menu.levelselect.LevelSelectScreen;
import tungus.games.elude.menu.settings.SettingsScreen;
import tungus.games.elude.util.ViewportHelper;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class MainMenu extends BaseScreen {
	
	private static final int STATE_FADEIN = 0;
	private static final int STATE_ACTIVE = 1;
	private static final int STATE_FADEOUT = 2;
	private static final float FADE_TIME = 0.6f;
	private int state = STATE_FADEIN;
	float stateTime = 0;
	
	private Sprite eludeOn = new Sprite(Assets.Tex.ELUDE_TITLE_ON.t);
	private final float FRUSTUM_WIDTH = 800;
	private final float FRUSTUM_HEIGHT = 480;
	private OrthographicCamera camera;
	private SpriteBatch spriteBatch;
	//private Array<Label> labels;
	private PlayButton playButton;
	private Sprite settingsButton;
	private Sprite multiplayerButton;
	private Sprite infoButton;
	
	private Screen nextScreen = null;

	public MainMenu(final Game game) {
		super(game);
		Gdx.input.setCatchBackKey(false);
		EludeMusic.set(EludeMusic.MENU);
		camera = ViewportHelper.newCamera(FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
		spriteBatch = new SpriteBatch();
		spriteBatch.setProjectionMatrix(camera.combined);
		float height = FRUSTUM_WIDTH * 0.28f;
		float y = FRUSTUM_HEIGHT - height; 
		eludeOn.setBounds(0, y, FRUSTUM_WIDTH, height);
		playButton = new PlayButton();
		playButton.setBounds(175, 35, 220, 220);
		settingsButton = new Sprite(Assets.Tex.SETTINGS_BUTTON.t);
		settingsButton.setBounds(405, 35, 105, 105);
		multiplayerButton = new Sprite(Assets.Tex.MULTIPLAYER_BUTTON.t);
		multiplayerButton.setBounds(405, 150, 220, 105);
		multiplayerButton.setColor(1, 1, 1, Elude.mpScreen == null ? 0.3f : 1f);
		infoButton = new Sprite(Assets.Tex.INFO_BUTTON.t);
		infoButton.setBounds(520, 35, 105, 105);
		Gdx.input.setInputProcessor(new InputAdapter(){
			private Vector3 touch = new Vector3();
			@Override
			public boolean touchUp (int screenX, int screenY, int pointer, int button) {
				camera.unproject(touch.set(screenX, screenY, 0));
				if (playButton.getBoundingRectangle().contains(touch.x, touch.y)) {
					int r = playButton.touchAt(touch.x, touch.y);
					if (r == PlayButton.RETURN_FINITE_LEVELS) {
						Assets.Sounds.MENU_BUTTON.s.play();
						toScreen(new LevelSelectScreen(game, true));
					} else if (r == PlayButton.RETURN_ARCADE_LEVELS) {
						Assets.Sounds.MENU_BUTTON.s.play();
						toScreen(new LevelSelectScreen(game, false));
					} else if (r == PlayButton.RETURN_DIVIDED) {
						Assets.Sounds.MENU_BUTTON.s.play();
					}
					return true;
				} else if (settingsButton.getBoundingRectangle().contains(touch.x, touch.y)) {
					Assets.Sounds.MENU_BUTTON.s.play();
					toScreen(new SettingsScreen(game));
					return true;
				} else if (infoButton.getBoundingRectangle().contains(touch.x, touch.y)) {
					Assets.Sounds.MENU_BUTTON.s.play();
					toScreen(new AboutScreen(game));
					return true;
				} else if (Elude.mpScreen != null && multiplayerButton.getBoundingRectangle().contains(touch.x, touch.y)) {
					try {
						Assets.Sounds.MENU_BUTTON.s.play();
						toScreen((Screen)(Elude.mpScreen.getConstructors()[0].newInstance(game)));
					} catch (Exception e) {
						Gdx.app.log("Net(?) MP", "Reflection magic failed");
						e.printStackTrace();
					}
				}
				return false;
			}
			private void toScreen(Screen s) {
				state = STATE_FADEOUT;
				stateTime = 0;
				nextScreen = s;
			}
		});
	}
	@Override
	public void render(float deltaTime) {
		playButton.update(deltaTime);
		stateTime += deltaTime;
		if (state == STATE_FADEIN && stateTime > FADE_TIME) {
			state = STATE_ACTIVE;
		}
		if (state == STATE_FADEOUT && stateTime > FADE_TIME) {
			game.setScreen(nextScreen);
			stateTime = FADE_TIME;
		}
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		float alpha =
				state == STATE_FADEIN ? 	stateTime / FADE_TIME :
				state == STATE_FADEOUT ? 	1 - stateTime / FADE_TIME :
				state == STATE_ACTIVE ? 	1 
						: 1;
		
		spriteBatch.begin();				
		eludeOn.draw(spriteBatch, alpha);
		playButton.render(spriteBatch, alpha);
		settingsButton.draw(spriteBatch, alpha);
		multiplayerButton.draw(spriteBatch, alpha);
		infoButton.draw(spriteBatch, alpha);
		spriteBatch.end();
	}
	
}
