package tungus.games.elude.menu.settings;

import tungus.games.elude.Assets;
import tungus.games.elude.BaseScreen;
import tungus.games.elude.Assets.EludeMusic;
import tungus.games.elude.menu.mainmenu.MainMenu;
import tungus.games.elude.menu.settings.Settings.MobileControlType;
import tungus.games.elude.util.ViewportHelper;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureAdapter;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class SettingsScreen extends BaseScreen {

	private static final float X = 225;
	private static final float TAB = 50;
	private static final float LINE_HEIGHT = 32f;
	private static final float SMALLGAP = 5; 		// Gap between title and content beneath
	private static final float BIGGAP = 20; 		// Gap between sections
	private static final float EMPTY_BELOW = (480-10*LINE_HEIGHT-4*SMALLGAP-3*BIGGAP) / 2;
	private static final float OFF_OFFSET = 150;

	private static final int STATE_ACTIVE = 1;
	private static final int STATE_FADEOUT = 2;
	private static final int STATE_FADEIN = 3;
	private static final float FADE_TIME = 0.6f;
	private int state = STATE_FADEIN;
	private float stateTime = 0;

	private final Chooser controlChoose;
	private final Chooser soundChoose;
	private final Chooser vibrateChoose;
        private final Chooser musicChoose;

	private final SpriteBatch batch;
	private final OrthographicCamera cam;

	private InputAdapter keyInput = new InputAdapter() {
		@Override
		public boolean keyDown(int keycode) {
			if (keycode == Keys.BACK || keycode == Keys.ESCAPE) {
				state = STATE_FADEOUT;
				stateTime = 0;
				Settings.INSTANCE.mobileControl = MobileControlType.values()[controlChoose.chosen];
				Settings.INSTANCE.soundOn = (soundChoose.chosen == 0);
				Settings.INSTANCE.vibrateOn = (vibrateChoose.chosen == 0);
                                Settings.INSTANCE.musicOn = (musicChoose.chosen == 0);                           
				Settings.INSTANCE.save();
				return true;
			}
			return false;
		}
	};

	private GestureDetector tapCheck = new GestureDetector(new GestureAdapter(){
		private final Vector3 t = new Vector3();
		@Override
		public boolean tap(float x, float y, int count, int button) {
			if (state == STATE_ACTIVE) {
				t.set(x, y, 0);
				cam.unproject(t);
				if (controlChoose.touch(t.x, t.y) ||
						soundChoose.touch(t.x, t.y) ||
						vibrateChoose.touch(t.x, t.y) ||
                                                musicChoose.touch(t.x, t.y)) {
					Assets.Sounds.MENU_BUTTON.play();
					return true;
				}
			}
			return false;
		}
	});

	public SettingsScreen(Game game) {
		super(game);
		Gdx.input.setCatchBackKey(true);
		EludeMusic.set(EludeMusic.MENU);
		Gdx.input.setInputProcessor(new InputMultiplexer(tapCheck, keyInput));
		cam = ViewportHelper.newCamera(800, 480);
		batch = new SpriteBatch(300);
		batch.setProjectionMatrix(cam.combined);
		controlChoose = new Chooser(
				new Rectangle[]{
						new Rectangle(X+TAB, EMPTY_BELOW+8*LINE_HEIGHT+3*SMALLGAP+3*BIGGAP, 200, LINE_HEIGHT),
						new Rectangle(X+TAB, EMPTY_BELOW+7*LINE_HEIGHT+3*SMALLGAP+3*BIGGAP, 200, LINE_HEIGHT),
						new Rectangle(X+TAB, EMPTY_BELOW+6*LINE_HEIGHT+3*SMALLGAP+3*BIGGAP, 200, LINE_HEIGHT)}, 
						new String[]{
						"TAP TO TARGET",
						"DPAD IN CORNER",
				"DPAD FROM TOUCH"}, 
				Settings.INSTANCE.mobileControl.ordinal());
		soundChoose = new Chooser(
				new Rectangle[]{
						new Rectangle(X+TAB, EMPTY_BELOW+4*LINE_HEIGHT+2*SMALLGAP+2*BIGGAP, 50, LINE_HEIGHT),
						new Rectangle(X+TAB+OFF_OFFSET, EMPTY_BELOW+4*LINE_HEIGHT+2*SMALLGAP+2*BIGGAP, 50, LINE_HEIGHT)}, 
						new String[]{"ON", "OFF"}, 
						Settings.INSTANCE.soundOn ? 0 : 1);
                musicChoose = new Chooser(
				new Rectangle[]{
						new Rectangle(X+TAB, EMPTY_BELOW+2*LINE_HEIGHT+SMALLGAP+BIGGAP, 50, LINE_HEIGHT),
						new Rectangle(X+TAB+OFF_OFFSET, EMPTY_BELOW+2*LINE_HEIGHT+SMALLGAP+BIGGAP, 50, LINE_HEIGHT)}, 
						new String[]{"ON", "OFF"}, 
						Settings.INSTANCE.musicOn ? 0 : 1);
		vibrateChoose = new Chooser(
				new Rectangle[]{
						new Rectangle(X+TAB, EMPTY_BELOW, 50, LINE_HEIGHT),
						new Rectangle(X+TAB+OFF_OFFSET, EMPTY_BELOW, 50, LINE_HEIGHT)}, 
						new String[]{"ON", "OFF"}, 
						Settings.INSTANCE.vibrateOn ? 0 : 1);
	}

	@Override
	public void render(float deltaTime) {
		stateTime += deltaTime;
                Assets.font.setScale(LINE_HEIGHT / 37.5f);
		if (state == STATE_FADEIN && stateTime > FADE_TIME) {
			state = STATE_ACTIVE;
		}
		if (state == STATE_FADEOUT && stateTime > FADE_TIME) {
			game.setScreen(new MainMenu(game));
			return;
		}
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		float alpha =
				state == STATE_FADEIN ? 	stateTime / FADE_TIME :
					state == STATE_FADEOUT ? 	1 - stateTime / FADE_TIME :
						state == STATE_ACTIVE ? 	1 
								: 1;
		batch.begin();
		Assets.font.setColor(1f, 1f, 0.35f, alpha);
		Assets.font.draw(batch, "CONTROLS",	X, EMPTY_BELOW+10*LINE_HEIGHT+4*SMALLGAP+3*BIGGAP);
		Assets.font.draw(batch, "SOUND",	X, EMPTY_BELOW+6*LINE_HEIGHT+3*SMALLGAP+2*BIGGAP);
                Assets.font.draw(batch, "MUSIC",	X, EMPTY_BELOW+4*LINE_HEIGHT+2*SMALLGAP+  BIGGAP);
		Assets.font.draw(batch, "VIBRATE",	X, EMPTY_BELOW+2*LINE_HEIGHT+  SMALLGAP);
		controlChoose.render(deltaTime, batch, alpha);
		soundChoose.render(deltaTime, batch, alpha);
		vibrateChoose.render(deltaTime, batch, alpha);
                musicChoose.render(deltaTime, batch, alpha);

		batch.end();
	}
}
