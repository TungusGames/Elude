package tungus.games.elude.menu;

import tungus.games.elude.Assets;
import tungus.games.elude.BaseScreen;
import tungus.games.elude.menu.mainmenu.MainMenu;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class AboutScreen extends BaseScreen {
	
	private static final int STATE_FADEIN = 0;
	private static final int STATE_ACTIVE = 1;
	private static final int STATE_FADEOUT = 2;
	private static final float FADE_TIME = 0.6f;
	private int state = STATE_FADEIN;
	private float stateTime = 0;
	
	private final SpriteBatch batch;
	
	public AboutScreen(Game game) {
		super(game);
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setInputProcessor(new InputAdapter(){
			@Override
			public boolean keyDown(int keycode) {
				if (keycode == Keys.BACK || keycode == Keys.ESCAPE) {
					state = STATE_FADEOUT;
					stateTime = 0;
					return true;
				}
				return false;
			}
		});
		OrthographicCamera cam = new OrthographicCamera(800, 480);
		cam.position.set(400, 240, 0);
		cam.update();
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
		}
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		float alpha =
				state == STATE_FADEIN ? 	stateTime / FADE_TIME :
				state == STATE_FADEOUT ? 	1 - stateTime / FADE_TIME :
				state == STATE_ACTIVE ? 	1 
						: 1;
		Assets.font.setColor(1, 1, 0.35f, alpha);
		batch.begin();
		Assets.font.draw(batch, "WIP", 350, 260);
		batch.end();
	}

}
