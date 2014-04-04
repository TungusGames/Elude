package tungus.games.elude.game;

import java.util.ArrayList;
import java.util.List;

import tungus.games.elude.Assets;
import tungus.games.elude.BaseScreen;
import tungus.games.elude.WorldRenderer;
import tungus.games.elude.game.input.Controls;
import tungus.games.elude.game.input.KeyControls;
import tungus.games.elude.game.input.mobile.TapToTargetControls;
import tungus.games.elude.levels.levelselect.LevelSelectScreen;
import tungus.games.elude.util.CamShaker;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

public class GameScreen extends BaseScreen {
	
	private World world;
	private WorldRenderer renderer;
	private SpriteBatch interfaceBatch;
	private OrthographicCamera interfaceCamera;
		
	private final Vector2 healthbarFromTopleft /*= new Vector2(0.5f, 1f)*/;
	private final float healthbarFullLength;
	private final float healthbarWidth;
	
	private final float FRUSTUM_WIDTH;
	private final float FRUSTUM_HEIGHT;
	
	private long lastTime;
	
	private List<Controls> controls;
	private Vector2[] dirs;
	
	private float timeSinceOver = 0;
	private final boolean finite;
	
	private InputAdapter listener = new InputAdapter() {
		@Override
		public boolean keyDown(int keycode) {
			if (keycode == Keys.BACK || keycode == Keys.ESCAPE) {
				game.setScreen(new LevelSelectScreen(game, finite));
				return true;
			}
			return false;
		}
	};
	
	public GameScreen(Game game, int levelNum, boolean finite) {
		super(game);
		
		Gdx.input.setInputProcessor(listener);
		
		this.finite = finite;
		world = new World(levelNum, finite);
		renderer = new WorldRenderer(world);
		interfaceBatch = new SpriteBatch();
		FRUSTUM_WIDTH = (float)Gdx.graphics.getWidth() / Gdx.graphics.getPpcX();
		FRUSTUM_HEIGHT = (float)Gdx.graphics.getHeight() / Gdx.graphics.getPpcY();
		healthbarWidth = 0.25f + (float)Math.max(0, (FRUSTUM_HEIGHT-5)/32f);
		healthbarFromTopleft = new Vector2(healthbarWidth, 2*healthbarWidth);
		healthbarFullLength = FRUSTUM_WIDTH - 2*healthbarFromTopleft.x;
		interfaceCamera = new OrthographicCamera(FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
		interfaceCamera.position.set(FRUSTUM_WIDTH/2, FRUSTUM_HEIGHT/2, 0);
		interfaceCamera.update();
		interfaceBatch.setProjectionMatrix(interfaceCamera.combined);
		
		controls = new ArrayList<Controls>();
		dirs = new Vector2[world.vessels.size()];
		for (int i = 0; i < world.vessels.size(); i++) {
			/*if (Gdx.app.getType() == ApplicationType.Desktop || Gdx.app.getType() == ApplicationType.WebGL) {
				controls.add(new KeyControls(new int[] {Keys.W, Keys.A, Keys.S, Keys.D}));
			} else {*/
				controls.add(new TapToTargetControls(renderer.camera, world.vessels.get(i).pos));
			//}				
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
		
		long newTime = TimeUtils.millis();
		long diff = newTime-lastTime;
		if (diff > 10)
			Gdx.app.log("deltaTime - outside", "" + diff);
		lastTime = newTime;
		
		world.update(deltaTime, dirs);
		if (world.over) {
			timeSinceOver += deltaTime;
			if (timeSinceOver > 3)
				game.setScreen(new LevelSelectScreen(game, finite));
		}
		
		newTime = TimeUtils.millis();
		diff = newTime-lastTime;
		if (diff > 10)
			Gdx.app.log("deltaTime - update", "" + diff);
		lastTime = newTime;
		
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		renderer.render(deltaTime);
		interfaceBatch.begin();
		for (int i = 0; i < controls.size(); i++) {
			dirs[i] = controls.get(i).getDir();
			controls.get(i).draw(interfaceBatch);
		}
		if (world.vessels.get(0).hp > 0) {
			float hpPerMax = world.vessels.get(0).hp / Vessel.MAX_HP;
			interfaceBatch.setColor(1-hpPerMax, hpPerMax, 0, 0.8f);
			interfaceBatch.draw(Assets.whiteRectangle, healthbarFromTopleft.x, FRUSTUM_HEIGHT-healthbarFromTopleft.y, 
								hpPerMax * healthbarFullLength, healthbarWidth);
		}
		interfaceBatch.end();
		
		newTime = TimeUtils.millis();
		diff = newTime-lastTime;
		if (diff > 10)
			Gdx.app.log("deltaTime - render", ""+diff);
		lastTime = newTime;
		
	}	

}