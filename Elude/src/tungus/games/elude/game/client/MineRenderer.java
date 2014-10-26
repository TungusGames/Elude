package tungus.games.elude.game.client;

import tungus.games.elude.Assets;
import tungus.games.elude.game.server.rockets.Mine;
import tungus.games.elude.util.ViewportHelper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Entries;

public class MineRenderer {
	
	private static class MineEffect {
		float x, y;
		float a = 0;
		boolean handled = true;
		MineEffect(float x, float y) {
			this.x = x; this.y = y;
		}
	}
	
	private static final float DRAW_R = Mine.SIZE / 2 * 1.35f;
	private static final float FADE_TIME = 0.4f;
	
	//private float[] times;
	private float time = 0;
	
	private final IntMap<MineEffect> mines;
	
	private final ShaderProgram shader;
	private final SpriteBatch batch;
	
	public MineRenderer(int s) {
		mines = new IntMap<MineEffect>(20);
		shader = new ShaderProgram(Gdx.files.internal("shaders/minevertex"),
				   Gdx.files.internal("shaders/minefragment"));
		Gdx.app.log("Shader", ""+shader.getLog());
		shader.begin();
		shader.setUniformf("R", DRAW_R);
		shader.setUniformf("time", 0);
		shader.end();
		
		OrthographicCamera cam = ViewportHelper.newCamera(20, 12);
		batch = new SpriteBatch();
		batch.setProjectionMatrix(cam.combined);
		batch.setColor(1, 1, 1, 1);
		batch.setShader(shader);
	}
	
	public void add(int id, float x, float y, float delta) {
		MineEffect e = mines.get(id);
		if (e != null) {
			e.handled = true;
			e.a = Math.min(1, e.a + 1/FADE_TIME * delta);
		} else {
			mines.put(id, new MineEffect(x, y));
		}
	}
	
	public void clear() {
		Entries<MineEffect> e = mines.entries();
		while (e.hasNext) {
			e.next().value.handled = false;
		}
	}
	
	public void render(float delta, float alpha) {
	
		time += delta;
		
		batch.begin();
		shader.setUniformf("time", time);
		Entries<MineEffect> e = mines.entries();
		while (e.hasNext) {
			MineEffect m = e.next().value; 
			if (!m.handled && (m.a -= (1/FADE_TIME * delta)) < 0) {
				e.remove();
			} else {
				batch.setColor(1, 1, 1, m.a);
				batch.draw(Assets.mineHelp, m.x - DRAW_R, m.y - DRAW_R, 2*DRAW_R, 2*DRAW_R);
			}
		}		
		batch.end();
		batch.setColor(1, 1, 1, 1);
	}
}
