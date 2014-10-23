package tungus.games.elude.game.client;

import java.util.ArrayList;
import java.util.List;

import tungus.games.elude.Assets;
import tungus.games.elude.BaseScreen;
import tungus.games.elude.game.client.input.Controls;
import tungus.games.elude.game.client.input.KeyControls;
import tungus.games.elude.game.client.input.mobile.DynamicDPad;
import tungus.games.elude.game.client.input.mobile.StaticDPad;
import tungus.games.elude.game.client.input.mobile.TapToTargetControls;
import tungus.games.elude.game.multiplayer.Connection;
import tungus.games.elude.game.multiplayer.LocalConnection.LocalConnectionPair;
import tungus.games.elude.game.multiplayer.transfer.ArcadeScoreInfo;
import tungus.games.elude.game.multiplayer.transfer.FiniteScoreInfo;
import tungus.games.elude.game.multiplayer.transfer.RenderInfo;
import tungus.games.elude.game.multiplayer.transfer.UpdateInfo;
import tungus.games.elude.game.server.Server;
import tungus.games.elude.game.server.enemies.Enemy.EnemyType;
import tungus.games.elude.menu.ingame.AbstractIngameMenu;
import tungus.games.elude.menu.ingame.GameOverMenu;
import tungus.games.elude.menu.ingame.LevelCompleteMenu;
import tungus.games.elude.menu.ingame.PauseMenu;
import tungus.games.elude.menu.levelselect.LevelSelectScreen;
import tungus.games.elude.menu.settings.Settings;
import tungus.games.elude.util.CamShaker;
import tungus.games.elude.util.CustomInterpolations;
import tungus.games.elude.util.ViewportHelper;

import com.badlogic.gdx.Application.ApplicationType;
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
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;

public class GameScreen extends BaseScreen {
	
	public static final int STATE_STARTING = 4;
	public static final int STATE_READY = 5;
	public static final int STATE_PLAYING = 0;
	public static final int STATE_PAUSED = 1;
	public static final int STATE_LOST = 2;
	public static final int STATE_WON = 3;
	private int state = STATE_STARTING;
	private float timeSinceStart = 0;
	
	public static final int MENU_NOCHANGE = -1;
	public static final int MENU_NEXTLEVEL = -2;
	public static final int MENU_QUIT = -3;
	public static final int MENU_RESTART = -4;
	
	private static final float START_TIME = 2f;
	
	private static final Vector2 tmp = new Vector2();
	
	private final AbstractIngameMenu[] menus;
	
	private final Connection connection;
	private WorldRenderer renderer;
	private SpriteBatch uiBatch;
	private SpriteBatch fontBatch;
	private OrthographicCamera uiCam;
	private OrthographicCamera fontCam;
	private float gameAlpha;
		
	private final PlayerHealthbar healthbar;
	private final LevelProgressbar progressbar;
	private final Rectangle pauseButton;
	
	private final float FRUSTUM_WIDTH;
	private final float FRUSTUM_HEIGHT;
	
	private long lastTime;
	
	private List<Controls> controls;
	
	private Vector3 rawTap = new Vector3();
	private boolean unhandledTap = false;
	
	private final boolean finite;
	private final int levelNum;
	
	private final int vesselID;
	private RenderInfo render;
	private UpdateInfo update;
	
	private InputAdapter inputListener = new InputAdapter() {
		@Override
		public boolean keyDown(int keycode) {
			if (keycode == Keys.BACK || keycode == Keys.ESCAPE) {
				if (state == STATE_PLAYING)
					state = STATE_PAUSED;
				else if (state != STATE_STARTING && state != STATE_READY)
					//If in an ingame menu, call its onBackKey()
					//PauseMenu - unpause, others - exit to menu
					menus[state - 1].onBackKey();
				//If starting, does nothing
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
	
	public static GameScreen newSinglePlayer(Game game, int levelNum, boolean finite) {
		LocalConnectionPair c = new LocalConnectionPair();
		new Thread(new Server(levelNum, finite, new Connection[] {c.c1})).start();
		return new GameScreen(game, levelNum, finite, c.c2, 0);
	}
	
	public GameScreen(Game game, int levelNum, boolean finite, Connection connection, int clientID) {
		super(game);
		ViewportHelper.setWorldSizeFromArea();
		Gdx.input.setInputProcessor(new InputMultiplexer(inputListener, new GestureDetector(gestureListener)));
		
		this.finite = finite;
		this.levelNum = levelNum;
		this.connection = connection;
		this.vesselID = clientID;
		
		menus = new AbstractIngameMenu[]{new PauseMenu(), new GameOverMenu(), new LevelCompleteMenu(levelNum, finite)};
		renderer = new WorldRenderer(clientID);
		uiBatch = new SpriteBatch();
		
		FRUSTUM_WIDTH = (float)Gdx.graphics.getWidth() / Gdx.graphics.getPpcX();
		FRUSTUM_HEIGHT = (float)Gdx.graphics.getHeight() / Gdx.graphics.getPpcY();
		
		Rectangle hb = new Rectangle();
		hb.height = 0.25f + (float)Math.max(0, (FRUSTUM_HEIGHT-5)/32f);
		hb.x = hb.height;
		hb.y = FRUSTUM_HEIGHT - 2 * hb.height;
		
		pauseButton = new Rectangle();
		pauseButton.width = pauseButton.height = 2.5f*hb.height;
		pauseButton.y = FRUSTUM_HEIGHT - 0.25f - pauseButton.height;
		pauseButton.x = FRUSTUM_WIDTH - 0.25f - pauseButton.width;
		
		hb.width = FRUSTUM_WIDTH - 2*hb.x - 0.5f - pauseButton.width;
		if (finite) {
			hb.y += hb.height/2;
			hb.height *= 0.85f;
			healthbar = new PlayerHealthbar(hb, FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
			Rectangle pb = new Rectangle(hb);
			pb.y -= FRUSTUM_HEIGHT - hb.y;
			progressbar = new LevelProgressbar(pb, FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
		} else {
			healthbar = new PlayerHealthbar(hb, FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
			progressbar = null;
		}
		
		
		uiCam = new OrthographicCamera(FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
		uiCam.position.set(FRUSTUM_WIDTH/2, FRUSTUM_HEIGHT/2, 0);
		uiCam.update();
		uiBatch.setProjectionMatrix(uiCam.combined);
		fontCam = ViewportHelper.newCamera(800, 480);
		fontCam.position.set(400, 240, 0);
		fontCam.update();
		fontBatch = new SpriteBatch(10);
		fontBatch.setProjectionMatrix(fontCam.combined);
		
		controls = new ArrayList<Controls>();
		update = new UpdateInfo();
		update.directions = new Vector2[1];
		for (int i = 0; i < update.directions.length; i++) {
			if (Gdx.app.getType() == ApplicationType.Desktop || Gdx.app.getType() == ApplicationType.WebGL) {
				controls.add(new KeyControls(new int[] {Keys.W, Keys.A, Keys.S, Keys.D}));
			} else {
				switch (Settings.INSTANCE.mobileControl) {
				case TAP_TO_TARGET:
					controls.add(new TapToTargetControls(renderer.camera));
					break;
				case DYNAMIC_DPAD:
					controls.add(new DynamicDPad(renderer.camera, uiCam, FRUSTUM_WIDTH, FRUSTUM_HEIGHT));
					break;
				case STATIC_DPAD:
					controls.add(new StaticDPad(uiCam, FRUSTUM_WIDTH, FRUSTUM_HEIGHT));
					break;
				}				
			}				
			update.directions[i] = controls.get(i).getDir(tmp.set(0,0), 0);
		}
		lastTime = TimeUtils.millis();
		render = new RenderInfo();
		connection.newest = new RenderInfo();
	}
	

	@Override
	public void render(float deltaTime) {
		if (deltaTime > 0.05f) {
			Gdx.app.log("LagWarn", "DeltaTime: " + deltaTime);
			deltaTime = 0.05f;
		}
		CamShaker.INSTANCE.update(deltaTime);
		logTime("outside", 50);
		synchronized(connection) {
			if (!connection.newest.handled) {
				switch(connection.newest.info) {
				case STATE_STARTING:
				case STATE_READY:
					connection.newest.copyTo(render);
					break;
				case STATE_PLAYING:
					if (state == STATE_READY) {
						state = STATE_PLAYING;
					}
					connection.newest.copyTo(render);
					break;
				case STATE_WON:
					state = STATE_WON;
					if (connection.newest instanceof FiniteScoreInfo) {
						((LevelCompleteMenu)menus[state-1]).setScore(((FiniteScoreInfo)connection.newest).score);
					} else {
						((LevelCompleteMenu)menus[state-1]).setScore(((ArcadeScoreInfo)connection.newest).score);
					}
					break;
				case STATE_LOST:
					state = STATE_LOST;
					break;
				default:
					throw new IllegalArgumentException("Unexpected transferdata info value: " + connection.newest.info);
				}
				connection.newest.handled = true;
			}
		}
		
		switch (state) {
		case STATE_STARTING:
			if (timeSinceStart > START_TIME) {
				state = STATE_READY;
				gameAlpha = 1;
			} else {
				gameAlpha = Interpolation.fade.apply(timeSinceStart/START_TIME);
				timeSinceStart += deltaTime;
			}
			update.info = Server.STATE_WAITING_START;
			break;
		case STATE_READY:
			update.info = Server.STATE_RUNNING;
			break;
		case STATE_PLAYING:
			for (int i = 0; i < update.directions.length; i++) {
				update.directions[i] = controls.get(i).getDir(tmp.set(render.vessels.get(i).x, render.vessels.get(i).y), deltaTime);
			}
			update.info = Server.STATE_RUNNING;
			break;
		case STATE_PAUSED:
			updateMenu(menus[state-1], deltaTime);
			update.info = Server.STATE_PAUSED;
			break;
		case STATE_WON:
		case STATE_LOST:
			updateMenu(menus[state-1], deltaTime);
			update.info = Server.STATE_OVER;
			break;
		}
		connection.write(update);
		logTime("update", 50);
		
		// RENDER
		renderGraphics(deltaTime);
	}
	
	private void renderGraphics(float deltaTime) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		renderer.render(deltaTime, gameAlpha, render, state == STATE_PLAYING);
		
		uiBatch.begin();
		for (int i = 0; i < controls.size(); i++) {
			controls.get(i).draw(uiBatch, gameAlpha);
		}
		if (render.hp != null && render.hp.length > 0) {
			healthbar.drawBar(uiBatch, render.hp[vesselID], deltaTime, gameAlpha);
		} else {
			healthbar.drawBar(uiBatch, 1, deltaTime, gameAlpha);
		}
		if (progressbar != null) {
			if (render != null && render.progress > -1) {
				progressbar.drawBar(uiBatch, render.progress, deltaTime, gameAlpha);
			} else {
				progressbar.drawBar(uiBatch, 0, deltaTime, gameAlpha);
			}
		}
		uiBatch.draw(Assets.pause, pauseButton.x, pauseButton.y, pauseButton.width, pauseButton.height);
		uiBatch.end();
		fontBatch.begin();
		healthbar.drawText(fontBatch, gameAlpha);
		if (progressbar != null) {
			progressbar.drawText(fontBatch, gameAlpha);
		}
		fontBatch.end();
		
		if (state == STATE_STARTING) {
			fontBatch.begin();
			Assets.font.setColor(1, 1, 1, 1);
			Assets.font.draw(fontBatch, ((finite ? "STAGE" : Assets.Strings.endless) + " ") + (levelNum + 1), 
					890-1100*CustomInterpolations.FLOAT_THROUGH.apply(timeSinceStart/START_TIME), 480/2);
			fontBatch.end();
		}
		
		switch (state) {
		case STATE_PAUSED:
		case STATE_LOST:
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
			game.setScreen(newSinglePlayer(game, levelNum, finite));
			update.info = Server.STATE_OVER;
			connection.write(update);
			connection.close();
			break;
		case MENU_NEXTLEVEL:
			game.setScreen(newSinglePlayer(game, levelNum + 1, finite));
			update.info = Server.STATE_OVER;
			connection.write(update);
			connection.close();
			break;
		case MENU_QUIT:
			game.setScreen(new LevelSelectScreen(game, finite));
			update.info = Server.STATE_OVER;
			connection.write(update);
			connection.close();
			break;
		}
	}

	@Override
	public void pause() {
		if (state == STATE_PLAYING)
			state = STATE_PAUSED;
	}
}