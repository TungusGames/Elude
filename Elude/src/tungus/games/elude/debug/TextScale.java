package tungus.games.elude.debug;

import tungus.games.elude.Assets;
import tungus.games.elude.BaseScreen;
import tungus.games.elude.util.ViewportHelper;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class TextScale extends BaseScreen {
	private static String text = "HOW WELL DOES IT SCALE?";
	private float time = 0;
	private SpriteBatch batch;
	
	public TextScale(Game game) {
		super(game);
		OrthographicCamera cam = ViewportHelper.newCamera(800, 480);
		batch = new SpriteBatch();
		batch.setProjectionMatrix(cam.combined);
	}
	
	@Override
	public void render(float deltaTime) {
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		time += deltaTime;
		float scale = (MathUtils.sin(time)+1)*3/4+0.5f;
		batch.begin();
		Assets.font.setScale(scale);
		TextBounds b = Assets.font.getBounds(text);
		Assets.font.draw(batch, text, 400-b.width/2, 240+b.height/2);
		batch.end();
	}
}
