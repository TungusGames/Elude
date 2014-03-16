package tungus.games.elude.levels.levelselect;

import tungus.games.elude.Assets;
import tungus.games.elude.BaseScreen;

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
	private final Vector2 touch2 = new Vector2();
	
	private final GridPanel grid;
	private final DetailsPanel details = new DetailsPanel();
	
	private final GestureAdapter listener = new GestureAdapter() {
		@Override
		public boolean tap(float x, float y, int count, int button) {
			touch3.set(x, y, 0);
			uiCam.unproject(touch3);
			grid.tapped(touch3.x, touch3.y);
			return true;
		}
		
		@Override
		public boolean pan(float x, float y, float deltaX, float deltaY) {
			touch3.set(x, y, 0);
			uiCam.unproject(touch3);
			grid.pan(touch3.x,touch3.y);
			return false;
		}
		
		@Override
		public boolean panStop(float x, float y, int pointer, int button) {
			touch3.set(x, y, 0);
			uiCam.unproject(touch3);
			grid.panStop(touch3.x, touch3.y);
			return false;
		}
		
		@Override
		public boolean fling(float velocityX, float velocityY, int button) {
			touch3.set(velocityX, velocityY, 0);
			if (velocityY < 0)
				velocityY *= 1.5f; // Downwards flings report as weaker than they feel
			uiCam.unproject(touch3);
			grid.fling(touch3.y);
			return false;
		}
		
		@Override
		public boolean touchDown(float x, float y, int pointer, int button) {
			grid.stopFling();
			return false;
		}
	};
	
	public LevelSelectScreen(Game game, boolean finiteLevels) {
		super(game);
		//Assets.font.setScale(1/Assets.font.getXHeight());
		FRUSTUM_WIDTH = 20;
		FRUSTUM_HEIGHT = 12;
		uiCam = new OrthographicCamera(FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
		uiCam.position.set(FRUSTUM_WIDTH/2, FRUSTUM_HEIGHT/2, 0);
		uiCam.update();
		uiBatch = new SpriteBatch();
		uiBatch.setProjectionMatrix(uiCam.combined);
		Gdx.input.setInputProcessor(new GestureDetector(listener));
		grid = new GridPanel(50, finiteLevels);
		fontCam = new OrthographicCamera(800f,480f);
		fontCam.position.set(400f, 240f, 0);
		fontCam.update();
		fontBatch = new SpriteBatch();
		fontBatch.setProjectionMatrix(fontCam.combined);
	}
	
	@Override
	public void render(float deltaTime) {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		uiBatch.begin();
		grid.render(uiBatch, deltaTime, false);
		
		uiBatch.end();
		fontBatch.begin();
		grid.render(fontBatch, deltaTime, true);
		fontBatch.end();
	}
}
