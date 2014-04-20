package tungus.games.elude.game.client;

import java.util.ArrayList;
import java.util.List;

import tungus.games.elude.Assets;
import tungus.games.elude.BaseScreen;
import tungus.games.elude.game.client.input.Controls;
import tungus.games.elude.game.client.input.KeyControls;
import tungus.games.elude.game.client.input.mobile.TapToTargetControls;
import tungus.games.elude.game.server.Vessel;
import tungus.games.elude.game.server.World;
import tungus.games.elude.levels.levelselect.LevelSelectScreen;
import tungus.games.elude.levels.loader.FiniteLevelLoader;
import tungus.games.elude.levels.loader.arcade.ArcadeLoaderBase;
import tungus.games.elude.menu.ingame.AbstractIngameMenu;
import tungus.games.elude.menu.ingame.GameOverMenu;
import tungus.games.elude.menu.ingame.LevelCompleteMenu;
import tungus.games.elude.menu.ingame.PauseMenu;
import tungus.games.elude.util.CamShaker;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureAdapter;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;

public class GameScreen extends BaseScreen {
	
	public static final int STATE_STARTING = 4;
	public static final int STATE_PLAYING = 0;
	public static final int STATE_PAUSED = 1;
	public static final int STATE_GAMEOVER = 2;
	public static final int STATE_WON = 3;
	private int state = STATE_STARTING;
	private float timeSinceStart = 0;
	
	public static final int MENU_NOCHANGE = -1;
	public static final int MENU_NEXTLEVEL = -2;
	public static final int MENU_QUIT = -3;
	public static final int MENU_RESTART = -4;
	
	private static final float START_TIME = 2f;
	
	private final AbstractIngameMenu[] menus;
	
	private World world;
	private WorldRenderer renderer;
	private SpriteBatch uiBatch;
	private OrthographicCamera uiCam;
	private float gameAlpha;
		
	private final Vector2 healthbarFromTopleft;
	private final float healthbarFullLength;
	private final float healthbarWidth;
	private final Rectangle pauseButton;
	
	private final float FRUSTUM_WIDTH;
	private final float FRUSTUM_HEIGHT;
	
	private long lastTime;
	
	private List<Controls> controls;
	private Vector2[] dirs;
	
	private Vector3 rawTap = new Vector3();
	private boolean unhandledTap = false;
	
	private final boolean finite;
	private final int levelNum;
	
	private InputAdapter inputListener = new InputAdapter() {
		@Override
		public boolean keyDown(int keycode) {
			if (keycode == Keys.BACK || keycode == Keys.ESCAPE) {
				if (state == STATE_PAUSED)
					((PauseMenu)menus[STATE_PAUSED-1]).unPause();
				else
					state = STATE_PAUSED;
				return true;
			}
			return false;
		}
	};
	private final GestureAdapter gestureListener = new GestureAdapter() {
		private final Vector3 touch3 = new Vector3();
		@Override
		public boolean tap(float x, float y, int count, int button) {
			rawTap.set(x, y, 0);
			if (state == STATE_PLAYING) {
				touch3.set(rawTap);
				uiCam.unproject(touch3);
				if (pauseButton.contains(touch3.x, touch3.y)) {
					state = STATE_PAUSED;
				}
			} else {
				unhandledTap = true;
			}
			return false;
		}
	};
	
	public GameScreen(Game game, int levelNum, boolean finite) {
		super(game);
		Gdx.input.setInputProcessor(new InputMultiplexer(inputListener, new GestureDetector(gestureListener)));
		this.finite = finite;
		this.levelNum = levelNum;
		menus = new AbstractIngameMenu[]{new PauseMenu(), new GameOverMenu(), new LevelCompleteMenu(levelNum, finite)};
		world = new World(levelNum, finite);
		renderer = new WorldRenderer(world);
		uiBatch = new SpriteBatch();
		FRUSTUM_WIDTH = (float)Gdx.graphics.getWidth() / Gdx.graphics.getPpcX();
		FRUSTUM_HEIGHT = (float)Gdx.graphics.getHeight() / Gdx.graphics.getPpcY();
		healthbarWidth = 0.25f + (float)Math.max(0, (FRUSTUM_HEIGHT-5)/32f);
		healthbarFromTopleft = new Vector2(healthbarWidth, 2*healthbarWidth);
		pauseButton = new Rectangle();
		pauseButton.width = pauseButton.height = 2.5f*healthbarWidth;
		pauseButton.y = FRUSTUM_HEIGHT - 0.25f - pauseButton.height;
		pauseButton.x = FRUSTUM_WIDTH - 0.25f - pauseButton.width;
		healthbarFullLength = FRUSTUM_WIDTH - 2*healthbarFromTopleft.x - 0.5f - pauseButton.width;
		uiCam = new OrthographicCamera(FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
		uiCam.position.set(FRUSTUM_WIDTH/2, FRUSTUM_HEIGHT/2, 0);
		uiCam.update();
		uiBatch.setProjectionMatrix(uiCam.combined);
		
		controls = new ArrayList<Controls>();
		dirs = new Vector2[world.vessels.size()];
		for (int i = 0; i < world.vessels.size(); i++) {
			if (Gdx.app.getType() == ApplicationType.Desktop || Gdx.app.getType() == ApplicationType.WebGL) {
				controls.add(new KeyControls(new int[] {Keys.W, Keys.A, Keys.S, Keys.D}));
			} else {
				controls.add(new TapToTargetControls(renderer.camera, world.vessels.get(i).pos));
			}				
			dirs[i] = controls.get(i).getDir();

		}
		lastTime = TimeUtils.millis();
	}
	

	@Override
	public void render(float deltaTime) {
		if (deltaTime > 0.05f) {
			Gdx.app.log("LagWarn", "DeltaTime: " + deltaTime);
			deltaTime = 0.05f;
		}
		CamShaker.INSTANCE.update(deltaTime);
		logTime("outside", 50);
		switch (state) {
		case STATE_STARTING:
			if (timeSinceStart > START_TIME) {
				state = STATE_PLAYING;
				gameAlpha = 1;
			} else {
				gameAlpha = Interpolation.fade.apply(timeSinceStart/START_TIME);
				timeSinceStart += deltaTime;
			}
			break;
		case STATE_PLAYING:
			world.update(deltaTime, dirs);
			if (world.state != World.STATE_PLAYING) {
				state = ((world.state == World.STATE_LOST && finite) ? STATE_GAMEOVER : STATE_WON);
				if (state == STATE_WON) {
					if (finite)
						((LevelCompleteMenu)menus[state-1]).setScore(((FiniteLevelLoader)world.waveLoader).getScore());
					else
						((LevelCompleteMenu)menus[state-1]).setScore(((ArcadeLoaderBase)world.waveLoader).getScore());
				}
				updateMenu(menus[state-1], deltaTime);	//update() must be called before render()
			}
			break;
		case STATE_WON:
		case STATE_PAUSED:
		case STATE_GAMEOVER:
			updateMenu(menus[state-1], deltaTime);
			break;
		}
		logTime("update", 50);
		
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		renderer.render(deltaTime, gameAlpha);
		uiBatch.begin();
		for (int i = 0; i < controls.size(); i++) {
			dirs[i] = controls.get(i).getDir();
			controls.get(i).draw(uiBatch, gameAlpha);
		}
		if (world.vessels.get(0).hp > 0) {
			float hpPerMax = world.vessels.get(0).hp / Vessel.MAX_HP;
			uiBatch.setColor(1-hpPerMax, hpPerMax, 0, 0.8f*gameAlpha);
			uiBatch.draw(Assets.whiteRectangle, healthbarFromTopleft.x, FRUSTUM_HEIGHT-healthbarFromTopleft.y, 
								hpPerMax * healthbarFullLength, healthbarWidth);
		}
		uiBatch.setColor(1,1,1,gameAlpha);
		uiBatch.draw(Assets.pause, pauseButton.x, pauseButton.y, pauseButton.width, pauseButton.height);
		uiBatch.end();
		switch (state) {
		case STATE_PAUSED:
		case STATE_GAMEOVER:
		case STATE_WON:
			renderMenu(menus[state-1]);
			break;
		}
		logTime("render", 50);
		
	}
	
	private void logTime(String message, long minToLog) {
		long newTime = TimeUtils.millis();
		long diff = newTime-lastTime;
		if (diff > minToLog)
			Gdx.app.log("delta - " + message, "" + diff);
		lastTime = newTime;
	}
	
	private void updateMenu(AbstractIngameMenu menu, float deltaTime) {
		menu.update(deltaTime, unhandledTap ? rawTap : null);
		unhandledTap = false;
	}
	private void renderMenu(AbstractIngameMenu menu) {
		int n = menu.render();
		if (n >= 0) {
			state = n;
		} else switch (n) {
		case MENU_RESTART:
			game.setScreen(new GameScreen(game, levelNum, finite));
			break;
		case MENU_NEXTLEVEL:
			game.setScreen(new GameScreen(game, levelNum+1, finite));
			break;
		case MENU_QUIT:
			game.setScreen(new LevelSelectScreen(game, finite));
			break;
		}
	}

}
