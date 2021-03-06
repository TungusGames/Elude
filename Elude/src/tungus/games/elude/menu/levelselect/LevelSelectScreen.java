package tungus.games.elude.menu.levelselect;

import tungus.games.elude.Assets;
import tungus.games.elude.BaseScreen;
import tungus.games.elude.Assets.EludeMusic;
import tungus.games.elude.game.client.GameScreen;
import tungus.games.elude.levels.scoredata.ScoreData;
import tungus.games.elude.menu.mainmenu.MainMenu;
import tungus.games.elude.util.ViewportHelper;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class LevelSelectScreen extends BaseScreen {
	
	private final float FRUSTUM_WIDTH;
	private final float FRUSTUM_HEIGHT;
	private final OrthographicCamera uiCam;
	private final OrthographicCamera fontCam;
	private final SpriteBatch uiBatch;
	private final SpriteBatch fontBatch;
	
	private final Vector3 touch3 = new Vector3();
	
	private final GridPanel grid;
	private final DetailsPanel details;
	
	private static final int STATE_BEGIN = 0;
	private static final int STATE_WORKING = 1;
	private static final int STATE_STARTING_LEVEL = 2;
	private static final int STATE_EXITING = 3;
	
	private static final float BEGIN_DURATION = 2f;
	private static final float LEVELSTART_DURATION = 2f;
	private static final float EXIT_DURATION = 1f;
	private float exitingFromRow = -1; //Used in exit state - the position before starting exit
	
	private final GestureAdapter listener = new GestureAdapter() {
		@Override
		public boolean tap(float x, float y, int count, int button) {
			if (state == STATE_WORKING) {
				touch3.set(x, y, 0);
				uiCam.unproject(touch3);
				if (grid.tapped(touch3.x, touch3.y)) {
					details.switchTo(grid.selected, grid.isOpen(grid.selected));
					Assets.Sounds.MENU_BUTTON.play();
				} else if (details.tapped(touch3.x, touch3.y)) {
					state = STATE_STARTING_LEVEL;
					stateTime = 0;
					Assets.Sounds.MENU_BUTTON.play();
				}
			}
			return false;
		}
		
		@Override
		public boolean pan(float x, float y, float deltaX, float deltaY) {
			if (state == STATE_WORKING) {
				touch3.set(x, y, 0);
				uiCam.unproject(touch3);
				grid.pan(touch3.x,touch3.y);
			}
			return false;
		}
		
		@Override
		public boolean panStop(float x, float y, int pointer, int button) {
			if (state == STATE_WORKING) {
				touch3.set(x, y, 0);
				uiCam.unproject(touch3);
				grid.panStop(touch3.x, touch3.y);
			}
			return false;
		}
		
		@Override
		public boolean fling(float velocityX, float velocityY, int button) {
			if (state == STATE_WORKING) {
				touch3.set(velocityX, velocityY, 0);
				if (velocityY < 0)
					velocityY *= 1.5f; // Downwards flings report as weaker than they feel
				uiCam.unproject(touch3);
				grid.fling(touch3.y);
			}
			return false;
		}
		
		@Override
		public boolean touchDown(float x, float y, int pointer, int button) {
			if (state == STATE_WORKING) {
				grid.stopFling();
			}
			return false;
		}
	};
	private final InputAdapter otherInput = new InputAdapter() {
		@Override
		public boolean keyDown(int keycode) {
			if ((keycode == Keys.BACK || keycode == Keys.ESCAPE) && (state == STATE_WORKING || state == STATE_BEGIN)) {
				state = STATE_EXITING;
				stateTime = 0;
				exitingFromRow = grid.middleRow;
				return true;
			}
			return false;
		}
		
		@Override
		public boolean scrolled(int amount) {
			if (state == STATE_WORKING) {
				grid.panStop(3, 5);
				grid.fling(amount*30);
			}
			return true;
		}
	};
	private final boolean finite;
	private final String starInfo;
	private final Vector2 stringPos;
	private final Sprite star;
	private int state = STATE_BEGIN;
	private float stateTime = 0;
	
	public LevelSelectScreen(Game game, boolean finiteLevels) {
		super(game);
		Gdx.input.setCatchBackKey(true);
		EludeMusic.set(EludeMusic.MENU);
		finite = finiteLevels;
		FRUSTUM_WIDTH = 20;
		FRUSTUM_HEIGHT = 12;
		uiCam = ViewportHelper.newCamera(FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
		uiBatch = new SpriteBatch();
		uiBatch.setProjectionMatrix(uiCam.combined);
		Gdx.input.setInputProcessor(new InputMultiplexer(new GestureDetector(listener), otherInput));
		grid = new GridPanel(finiteLevels ? ScoreData.playerFiniteScore.size() : ScoreData.playerArcadeScore.size(), finiteLevels);
		details = new DetailsPanel(finiteLevels);
		fontCam = ViewportHelper.newCamera(800f,480f);
		fontBatch = new SpriteBatch();
		fontBatch.setProjectionMatrix(fontCam.combined);
		
		starInfo = ScoreData.starsEarned + " / " + ScoreData.starsMax;
		stringPos = new Vector2(790 - Assets.font.getBounds(starInfo).width, 470);
		star = new Sprite(Assets.Tex.STAR_ON.t);
		star.setBounds(stringPos.x/40-1f, stringPos.y/40-0.7f, 0.8f, 0.8f*0.95f);
	}
	
	@Override
	public void render(float deltaTime) {
		stateTime += deltaTime;
		if (state == STATE_BEGIN && stateTime > BEGIN_DURATION) {
			state = STATE_WORKING;
			stateTime = 0;
		} else if (state == STATE_STARTING_LEVEL && stateTime > LEVELSTART_DURATION) {
			game.setScreen(GameScreen.newSinglePlayer(game, grid.selected, finite));
			return;
		} else if (state == STATE_EXITING && stateTime > EXIT_DURATION) {
			game.setScreen(new MainMenu(game));
			return;
		}
		
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		uiBatch.begin();
		switch (state) {
		case STATE_BEGIN:
			grid.renderLoading(uiBatch, deltaTime, false, stateTime/BEGIN_DURATION);
			star.draw(uiBatch, stateTime/BEGIN_DURATION);
			break;
		case STATE_WORKING:
			grid.render(uiBatch, deltaTime, false);
			details.render(deltaTime, uiBatch, false, 1);
			star.draw(uiBatch, 1);
			break;
		case STATE_STARTING_LEVEL:
			grid.renderEnding(uiBatch, deltaTime, false, stateTime/LEVELSTART_DURATION);
			details.render(deltaTime, uiBatch, false, 1-stateTime/LEVELSTART_DURATION);
			star.draw(uiBatch, 1-stateTime/LEVELSTART_DURATION);
			break;
		case STATE_EXITING:
			grid.renderLoading(uiBatch, deltaTime, false, 1-stateTime/EXIT_DURATION, exitingFromRow);
			details.render(deltaTime, uiBatch, false, 1-stateTime/EXIT_DURATION);
			star.draw(uiBatch, 1-stateTime/EXIT_DURATION);
			break;
		}
		uiBatch.end();
		
		fontBatch.begin();
		switch (state) {
		case STATE_BEGIN:
			grid.renderLoading(fontBatch, deltaTime, true, stateTime/BEGIN_DURATION);
			Assets.font.setColor(1, 1, 1, stateTime/BEGIN_DURATION);
			break;
		case STATE_WORKING:
			grid.render(fontBatch, deltaTime, true);
			details.render(deltaTime, fontBatch, true, 1);
			Assets.font.setColor(1, 1, 1, 1);
			break;
		case STATE_STARTING_LEVEL:
			grid.render(fontBatch, deltaTime, true);
			details.render(deltaTime, fontBatch, true, 1-stateTime/LEVELSTART_DURATION);
			Assets.font.setColor(1, 1, 1, 1-stateTime/LEVELSTART_DURATION);
			break;
		case STATE_EXITING:
			grid.renderLoading(fontBatch, deltaTime, true, 1-stateTime/EXIT_DURATION, exitingFromRow);
			details.render(deltaTime, fontBatch, true, 1-stateTime/EXIT_DURATION);
			Assets.font.setColor(1, 1, 1, 1-stateTime/EXIT_DURATION);
			break;
		}
		Assets.font.setScale(1);
		Assets.font.draw(fontBatch, starInfo, stringPos.x, stringPos.y);
		fontBatch.end();
	}
}
