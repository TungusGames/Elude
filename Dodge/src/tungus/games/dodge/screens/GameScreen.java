package tungus.games.dodge.screens;

import java.util.ArrayList;
import java.util.List;

import tungus.games.dodge.WorldRenderer;
import tungus.games.dodge.game.Controls;
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
	
	private List<Controls> controls;
	
	private Vector2[] dirs;

	public GameScreen(Game game) {
		super(game);
		world = World.INSTANCE = new World();
		renderer = new WorldRenderer(world);
		interfaceBatch = new SpriteBatch();
		interfaceCamera = new OrthographicCamera(World.WIDTH, World.HEIGHT);
		interfaceCamera.position.set(World.WIDTH/2, World.HEIGHT/2, 0);
		interfaceCamera.update();
		interfaceBatch.setProjectionMatrix(interfaceCamera.combined);
		
		controls = new ArrayList<Controls>();
		dirs = new Vector2[world.vessels.size()];
		for (int i = 0; i < world.vessels.size(); i++) {
			/*if (Gdx.app.getType() == ApplicationType.Desktop || Gdx.app.getType() == ApplicationType.WebGL) {
				controls.add(new Controls(new int[] {Keys.W, Keys.A, Keys.S, Keys.D}));
			} else {*/
				controls.add(new Controls(interfaceCamera));
			//}				
			dirs[i] = new Vector2(0, 0);

		}
	}
	

	@Override
	public void render(float deltaTime) {
		deltaTime = Math.min(deltaTime, 0.05f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		interfaceBatch.begin();
		for (int i = 0; i < controls.size(); i++) {
			dirs[i] = controls.get(i).getDirection(deltaTime);
			//if (Gdx.app.getType() == ApplicationType.Android || Gdx.app.getType() == ApplicationType.iOS) {
				controls.get(i).renderDPad(interfaceBatch);
			//}
		}
		interfaceBatch.end();
		world.update(deltaTime, dirs);
		renderer.render();
		
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}
	
	

}
