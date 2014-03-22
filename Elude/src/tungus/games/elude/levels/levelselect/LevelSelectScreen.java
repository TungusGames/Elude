package tungus.games.elude.levels.levelselect;

import tungus.games.elude.Assets;
import tungus.games.elude.BaseScreen;
import tungus.games.elude.game.GameScreen;
import tungus.games.elude.levels.scoredata.ScoreData;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
	private static final int STATE_END = 2;
	
	private static final float BEGIN_DURATION = 2f;
	private static final float END_DURATION = 2f;
	
	private final GestureAdapter listener = new GestureAdapter() {
		@Override
		public boolean tap(float x, float y, int count, int button) {
			if (state == STATE_WORKING) {
				touch3.set(x, y, 0);
				uiCam.unproject(touch3);
				if (grid.tapped(touch3.x, touch3.y)) {
					details.switchTo(grid.selected);
				} else if (details.tapped(touch3.x, touch3.y)) {
					state = STATE_END;
					stateTime = 0;
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
	private final boolean finite;
	private int state = STATE_BEGIN;
	private float stateTime = 0;
	
	public LevelSelectScreen(Game game, boolean finiteLevels) {
		super(game);
		finite = finiteLevels;
		FRUSTUM_WIDTH = 20;
		FRUSTUM_HEIGHT = 12;
		uiCam = new OrthographicCamera(FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
		uiCam.position.set(FRUSTUM_WIDTH/2, FRUSTUM_HEIGHT/2, 0);
		uiCam.update();
		uiBatch = new SpriteBatch();
		uiBatch.setProjectionMatrix(uiCam.combined);
		Gdx.input.setInputProcessor(new GestureDetector(listener));
		grid = new GridPanel(finiteLevels ? ScoreData.playerFiniteScore.size() : ScoreData.playerArcadeScore.size(), finiteLevels);
		details = new DetailsPanel(finiteLevels);
		fontCam = new OrthographicCamera(800f,480f);
		fontCam.position.set(400f, 240f, 0);
		fontCam.update();
		fontBatch = new SpriteBatch();
		fontBatch.setProjectionMatrix(fontCam.combined);
	}
	
	@Override
	public void render(float deltaTime) {
		stateTime += deltaTime;
		if (state == STATE_BEGIN && stateTime > BEGIN_DURATION) {
			state = STATE_WORKING;
			stateTime = 0;
		} else if (state == STATE_END && stateTime > END_DURATION) {
			game.setScreen(new GameScreen(game, grid.selected, finite));
			return;
		}
		
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		uiBatch.begin();
		switch (state) {
		case STATE_BEGIN:
			grid.renderLoading(uiBatch, deltaTime, false, stateTime/BEGIN_DURATION);
			break;
		case STATE_WORKING:
			grid.render(uiBatch, deltaTime, false);
			details.render(deltaTime, uiBatch, false, 1);
			break;
		case STATE_END:
			grid.renderEnding(uiBatch, deltaTime, false, stateTime/END_DURATION);
			details.render(deltaTime, uiBatch, false, 1-stateTime/END_DURATION);
			break;
		}
		uiBatch.end();
		
		fontBatch.begin();
		switch (state) {
		case STATE_BEGIN:
			grid.renderLoading(fontBatch, deltaTime, true, stateTime/BEGIN_DURATION);
			break;
		case STATE_WORKING:
			grid.render(fontBatch, deltaTime, true);
			details.render(deltaTime, fontBatch, true, 1);
			break;
		case STATE_END:
			grid.render(fontBatch, deltaTime, true);
			details.render(deltaTime, fontBatch, true, 1-stateTime/END_DURATION);
		}		
		fontBatch.end();
	}
}
