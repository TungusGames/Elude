package tungus.games.dodge.screens;

import java.util.ArrayList;
import java.util.List;

import tungus.games.dodge.Assets;
import tungus.games.dodge.WorldRenderer;
import tungus.games.dodge.game.Controls;
import tungus.games.dodge.game.Vessel;
import tungus.games.dodge.game.World;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class GameScreen extends BaseScreen {
	
	private World world;
	private WorldRenderer renderer;
	private SpriteBatch interfaceBatch;
	private OrthographicCamera interfaceCamera;
	
	private final Vector2 HEALTHBAR_BOTTOMLEFT = new Vector2(1, 10.5f);
	private final float HEALTHBAR_FULL_LENGTH;
	private final float HEALTHBAR_HEIGHT = 0.5f;
	
	private final float FRUSTUM_WIDTH;
	private final float FRUSTUM_HEIGHT;
	
	private List<Controls> controls;
	
	private Vector2[] dirs;

	public GameScreen(Game game) {
		super(game);
		world = World.INSTANCE = new World();
		renderer = new WorldRenderer(world);
		interfaceBatch = new SpriteBatch();
		FRUSTUM_WIDTH = World.WIDTH;
		FRUSTUM_HEIGHT = FRUSTUM_WIDTH * ((float)Gdx.graphics.getHeight()/Gdx.graphics.getWidth());
		HEALTHBAR_FULL_LENGTH = FRUSTUM_WIDTH - 2*HEALTHBAR_BOTTOMLEFT.x;
		interfaceCamera = new OrthographicCamera(FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
		interfaceCamera.position.set(FRUSTUM_WIDTH/2, FRUSTUM_HEIGHT/2, 0);
		interfaceCamera.update();
		interfaceBatch.setProjectionMatrix(interfaceCamera.combined);
		
		controls = new ArrayList<Controls>();
		dirs = new Vector2[world.vessels.size()];
		for (int i = 0; i < world.vessels.size(); i++) {
			if (Gdx.app.getType() == ApplicationType.Desktop || Gdx.app.getType() == ApplicationType.WebGL) {
				controls.add(new Controls(new int[] {Keys.W, Keys.A, Keys.S, Keys.D}));
			} else {
				controls.add(new Controls(interfaceCamera, FRUSTUM_WIDTH, FRUSTUM_HEIGHT));
			}				
			dirs[i] = new Vector2(0, 0);

		}
	}
	

	@Override
	public void render(float deltaTime) {
		deltaTime = Math.min(deltaTime, 0.05f);
		world.update(deltaTime, dirs);
		
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		renderer.render();
		
		interfaceBatch.begin();
		for (int i = 0; i < controls.size(); i++) {
			dirs[i] = controls.get(i).getDirection(deltaTime);
			if (Gdx.app.getType() == ApplicationType.Android || Gdx.app.getType() == ApplicationType.iOS) {
				controls.get(i).renderDPad(interfaceBatch);
			}
		}
		if (world.vessels.get(0).hp > 0) {
			float hpPerMax = world.vessels.get(0).hp / Vessel.MAX_HP;
			interfaceBatch.setColor(1-hpPerMax, hpPerMax, 0, 0.8f);
			interfaceBatch.draw(Assets.whiteRectangle, HEALTHBAR_BOTTOMLEFT.x, HEALTHBAR_BOTTOMLEFT.y, 
								hpPerMax * HEALTHBAR_FULL_LENGTH, HEALTHBAR_HEIGHT);
		}
		interfaceBatch.end();
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}
	
	

}
