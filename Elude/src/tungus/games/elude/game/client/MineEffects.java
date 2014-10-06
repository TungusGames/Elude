package tungus.games.elude.game.client;

import tungus.games.elude.Assets;
import tungus.games.elude.game.server.World;
import tungus.games.elude.util.ViewportHelper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.IntIntMap.Entries;
import com.badlogic.gdx.utils.IntIntMap.Entry;
import com.badlogic.gdx.utils.TimeUtils;

public class MineEffects {
	
	private final int MAX;
	
	private float[] times;
	private float[] coords;
	private float[] timesOld;
	private float[] coordsOld;
	
	private IntIntMap prevFrameIDs;
	private IntIntMap newFrameIDs;
	
	private int N = 0;
	private int verified = 0;
	
	private final ShaderProgram shader;
	private final SpriteBatch batch;
	
	public MineEffects(int s) {
		MAX = s;
		times = new float[s];
		timesOld = new float[s];
		coords = new float[2*s];
		coordsOld = new float[2*s];
		prevFrameIDs = new IntIntMap((int)(s/0.8f));
		newFrameIDs = new IntIntMap((int)(s/0.8f));
		
		shader = new ShaderProgram(Gdx.files.internal("shaders/minevertex"),
				   Gdx.files.internal("shaders/minefragment"));
		Gdx.app.log("Shader", ""+shader.getLog());
		shader.begin();
		shader.setUniformf("worldSize", World.WIDTH, World.HEIGHT);
		shader.setUniformf("viewportSize", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		shader.setUniformf("R", 3);
		shader.setUniformi("L", 0);
		shader.end();
		OrthographicCamera cam = ViewportHelper.newCamera(20, 12);
		batch = new SpriteBatch();
		batch.setProjectionMatrix(cam.combined);
		batch.setColor(1, 1, 1, 1);
		batch.setShader(shader);
	}
	
	public void add(int id, float x, float y) {
		if (!prevFrameIDs.containsKey(id)) {
			if (N < MAX) {
				times[N] = 0;
				coords[2*N] = x; coords[2*N+1] = y;
				newFrameIDs.put(id, N);
				++N;
				++verified;
			}
		} else {
			++verified;
			newFrameIDs.put(id, prevFrameIDs.get(id, 0));
		}
		
	}
	
	public void clear() {
		verified = 0;
		IntIntMap temp = newFrameIDs;
		newFrameIDs = prevFrameIDs;
		prevFrameIDs = temp;
		newFrameIDs.clear();
	}
	
	public void render(float delta, float alpha) {
		batch.begin();
		if (verified < N) {
			regenArrays();
			shader.setUniform2fv("center[0]", coords, 0, N*2);
			shader.setUniformi("L", N);
		} else if (newFrameIDs.size > prevFrameIDs.size) {
			shader.setUniform2fv("center[0]", coords, 0, N*2);
			shader.setUniformi("L", N);
		}
		for (int i = 0; i < N; i++) {
			times[i] += delta;
			Gdx.app.log("time sent", ""+times[0]);
		}
		shader.setUniform1fv("time[0]", times, 0, N);
		batch.draw(Assets.whiteRectangle, 0, 0, World.WIDTH, World.HEIGHT);
		batch.end();
	}

	private void regenArrays() {
		Entries ent = newFrameIDs.entries();
		float[] temp = times;
		times = timesOld;
		timesOld = temp;
		temp = coords;
		coords = coordsOld;
		coordsOld = temp;
		int i = 0;
		while(ent.hasNext) {
			Entry e = ent.next();
			times[i] = timesOld[e.value];
			coords[2*i] = coordsOld[e.value*2];
			coords[2*i+1] = coordsOld[e.value*2+1];
			newFrameIDs.put(e.key, i);
			N = times.length;
		}
	}
}
