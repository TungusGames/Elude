package tungus.games.elude.menu;

import tungus.games.elude.Assets;
import tungus.games.elude.BaseScreen;
import tungus.games.elude.Elude;
import tungus.games.elude.menu.mainmenu.MainMenu;
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

public class AboutScreen extends BaseScreen {
	
	private static final float X = 225;
	private static final float NAME_X = 275;
	private static final float ATTRIBUTE_X = 275;
	private static final float LINE_HEIGHT = 37.5f;
	private static final float SMALLGAP = 10; 		// Gap between title and content beneath
	private static final float BIGGAP = 40; 		// Gap between sections
	private static final float EMPTY_BELOW = (480-9*LINE_HEIGHT-2*SMALLGAP-BIGGAP) / 2;
	private final Rectangle[] linkRects = new Rectangle[] {
			new Rectangle(ATTRIBUTE_X, 3*LINE_HEIGHT + EMPTY_BELOW, 220, LINE_HEIGHT),
			new Rectangle(ATTRIBUTE_X, 2*LINE_HEIGHT + EMPTY_BELOW, 400, LINE_HEIGHT),
			new Rectangle(ATTRIBUTE_X,   LINE_HEIGHT + EMPTY_BELOW, 200, LINE_HEIGHT),
			new Rectangle(ATTRIBUTE_X,                 EMPTY_BELOW, 200, LINE_HEIGHT)};
	
	private static final int STATE_FADEIN = 0;
	private static final int STATE_ACTIVE = 1;
	private static final int STATE_FADEOUT = 2;
	private static final float FADE_TIME = 0.6f;
	private int state = STATE_FADEIN;
	private float stateTime = 0;
	
	private final OrthographicCamera cam;
	private final SpriteBatch batch;
	
	private InputAdapter keyExit = new InputAdapter() {
		@Override
		public boolean keyDown(int keycode) {
			if (keycode == Keys.BACK || keycode == Keys.ESCAPE) {
				state = STATE_FADEOUT;
				stateTime = 0;
				return true;
			}
			return false;
		}
	};
	
	private GestureDetector tapCheck = new GestureDetector(new GestureAdapter(){
		private final Vector3 t = new Vector3();
		@Override
		public boolean tap(float x, float y, int count, int button) {
			cam.unproject(t.set(x, y, 0));
			if (linkRects[0].contains(t.x, t.y)) {
				Gdx.net.openURI("http://www.freesound.org/people/sarge4267/sounds/102720/");
			} else if (linkRects[1].contains(t.x, t.y)) {
				Gdx.net.openURI("http://www.freesound.org/people/soundslikewillem/sounds/190707/");
			} else if (linkRects[2].contains(t.x, t.y)) {
				Gdx.net.openURI("http://www.flaticon.com/free-icon/settings-gear-ios-7-interface-symbol_17214");
			} else if (linkRects[2].contains(t.x, t.y)) {				
				Gdx.net.openURI("http://www.fontspace.com/blambot/bulletproof-bb");
			}
			return false;
		}
	});
	
	public AboutScreen(Game game) {
		super(game);
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setInputProcessor(new InputMultiplexer(keyExit, tapCheck));
		cam = ViewportHelper.newCamera(800, 480);
		batch = new SpriteBatch(300);
		batch.setProjectionMatrix(cam.combined);
	}
	
	@Override
	public void render(float deltaTime) {
		stateTime += deltaTime;
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
		Assets.font.setColor(1, 1, 0.35f, alpha);
		Assets.font.draw(batch, "PROGRAMMING BY", X, 9*LINE_HEIGHT + 2*SMALLGAP + BIGGAP + EMPTY_BELOW);
		Assets.font.draw(batch, "SOME ASSETS USED FROM", X, 5*LINE_HEIGHT + SMALLGAP + EMPTY_BELOW);
		
		Assets.font.setColor(1, 1, 1, alpha);
		Assets.font.draw(batch, "MERNYEI PETER",  NAME_X, 8*LINE_HEIGHT +   SMALLGAP + BIGGAP + EMPTY_BELOW);
		Assets.font.draw(batch, "STADLER BENEDEK",NAME_X, 7*LINE_HEIGHT +   SMALLGAP + BIGGAP + EMPTY_BELOW);	// <-- an awesome guy
		Assets.font.draw(batch, "TARDOS TAMAS",   NAME_X, 6*LINE_HEIGHT +   SMALLGAP + BIGGAP + EMPTY_BELOW);

		Assets.font.draw(batch, "SARGE4267", 		ATTRIBUTE_X, 4*LINE_HEIGHT + EMPTY_BELOW);
		Assets.font.draw(batch, "SOUNDSLIKEWILLEM ",ATTRIBUTE_X, 3*LINE_HEIGHT + EMPTY_BELOW);
		Assets.font.draw(batch, "FREEPIK", 			ATTRIBUTE_X, 2*LINE_HEIGHT + EMPTY_BELOW);
		Assets.font.draw(batch, "BLAMBOT",	 		ATTRIBUTE_X,   LINE_HEIGHT + EMPTY_BELOW);
		
		Assets.font.draw(batch, Elude.VERSION, 		790-Assets.font.getBounds(Elude.VERSION).width,   470);
		batch.end();
	}

}
