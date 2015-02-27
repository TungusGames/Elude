package tungus.games.elude.menu;

import tungus.games.elude.Assets;
import tungus.games.elude.Assets.EludeMusic;
import tungus.games.elude.BaseScreen;
import tungus.games.elude.Elude;
import tungus.games.elude.menu.mainmenu.MainMenu;
import tungus.games.elude.util.ViewportHelper;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class AboutScreen extends BaseScreen {
	
	private static final float LARGE_LINE_HEIGHT = 36f;
	private static final float SHORT_LINE_HEIGHT = 30f;
	private static final float SMALLGAP = 7; 		// Gap between title and content beneath
	private static final float BIGGAP = 20; 		// Gap between sections
	private static final float EMPTY_BELOW = (480-7*LARGE_LINE_HEIGHT-4*SHORT_LINE_HEIGHT - 3*SMALLGAP-2*BIGGAP) / 2;
	
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
	
	public AboutScreen(Game game) {
		super(game);
		Gdx.input.setCatchBackKey(true);
		EludeMusic.set(EludeMusic.MENU);
		Gdx.input.setInputProcessor(keyExit);
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
		Assets.font.drawWrapped(batch, "PROGRAMMING BY", 0, 7*LARGE_LINE_HEIGHT + 4*SHORT_LINE_HEIGHT + 3*SMALLGAP + 2*BIGGAP + EMPTY_BELOW, 800, HAlignment.CENTER);
		Assets.font.drawWrapped(batch, "MUSIC BY", 0, 3*LARGE_LINE_HEIGHT + 4*SHORT_LINE_HEIGHT + 2*SMALLGAP + BIGGAP + EMPTY_BELOW, 800, HAlignment.CENTER);
		Assets.font.drawWrapped(batch, "SOME ASSETS USED FROM", 0, 1*LARGE_LINE_HEIGHT + 4*SHORT_LINE_HEIGHT + SMALLGAP + EMPTY_BELOW, 800, HAlignment.CENTER);
		
		
		Assets.font.setColor(1, 1, 1, alpha);
		Assets.font.drawWrapped(batch, "PETER MERNYEI",   0, 6*LARGE_LINE_HEIGHT + 4*SHORT_LINE_HEIGHT +  2*SMALLGAP + 2*BIGGAP + EMPTY_BELOW, 800, HAlignment.CENTER);
		Assets.font.drawWrapped(batch, "BENEDEK STADLER", 0, 5*LARGE_LINE_HEIGHT + 4*SHORT_LINE_HEIGHT +  2*SMALLGAP + 2*BIGGAP + EMPTY_BELOW, 800, HAlignment.CENTER);	// <-- an awesome guy
		Assets.font.drawWrapped(batch, "TAMAS TARDOS",    0, 4*LARGE_LINE_HEIGHT + 4*SHORT_LINE_HEIGHT +  2*SMALLGAP + 2*BIGGAP + EMPTY_BELOW, 800, HAlignment.CENTER);
		
		Assets.font.drawWrapped(batch, "KID2WILL",    0, 2*LARGE_LINE_HEIGHT + 4*SHORT_LINE_HEIGHT +  SMALLGAP + BIGGAP + EMPTY_BELOW, 800, HAlignment.CENTER);
		
		Assets.font.setScale(SHORT_LINE_HEIGHT / LARGE_LINE_HEIGHT);
		
		Assets.font.drawWrapped(batch, "SARGE4267", 		0, 4*SHORT_LINE_HEIGHT + EMPTY_BELOW, 800, HAlignment.CENTER);
		Assets.font.drawWrapped(batch, "SOUNDSLIKEWILLEM ", 0, 3*SHORT_LINE_HEIGHT + EMPTY_BELOW, 800, HAlignment.CENTER);
		Assets.font.drawWrapped(batch, "FREEPIK", 			0, 2*SHORT_LINE_HEIGHT + EMPTY_BELOW, 800, HAlignment.CENTER);
		Assets.font.drawWrapped(batch, "BLAMBOT",	 		0,   SHORT_LINE_HEIGHT + EMPTY_BELOW, 800, HAlignment.CENTER);
		
		Assets.font.setScale(1);
		
		Assets.font.draw(batch, Elude.VERSION, 		790-Assets.font.getBounds(Elude.VERSION).width,   470);
		batch.end();
	}

}
