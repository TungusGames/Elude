package tungus.games.elude.game.client.worldrender.phases;

import tungus.games.elude.Assets;
import tungus.games.elude.game.client.worldrender.WorldRenderer;
import tungus.games.elude.game.client.worldrender.renderable.MineRenderable;
import tungus.games.elude.game.client.worldrender.renderable.Renderable;
import tungus.games.elude.game.server.rockets.Mine;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Entries;

public class MineRenderer extends PhaseRenderer {
	
	private static class MineEffect {
		float x, y;
		float a = 0;
		boolean handled = true;
		MineEffect(float x, float y) {
			this.x = x; this.y = y;
		}
	}
	
	private static final float DRAW_R = Mine.RADIUS * 1.35f;
	private static final float FADE_TIME = 0.4f;
	
	private float time = 0;
	private float delta = 0;
	
	private final IntMap<MineEffect> mines;
	
	private final ShaderProgram shader;
	
	public MineRenderer() {
		mines = new IntMap<MineEffect>(20);
		shader = Assets.Shaders.MINE.s;
		shader.begin();
		shader.setUniformf("R", DRAW_R);
		shader.setUniformf("time", 0);
		shader.end();
	}
	
	@Override
	public void render(Renderable r) {
		MineRenderable mine = (MineRenderable)r;
		MineEffect e = mines.get(mine.adderID);
		if (e != null) {
			e.handled = true;
			e.a = Math.min(1, e.a + 1/FADE_TIME * delta);
		} else {
			mines.put(mine.adderID, new MineEffect(mine.x, mine.y));
		}
	}
	
	@Override
	public void begin(RenderPhase phase, WorldRenderer wr, float delta) {
		super.begin(phase, wr, delta);
		Entries<MineEffect> e = mines.entries();
		while (e.hasNext) {
			e.next().value.handled = false;
		}
		this.delta = delta;
	}
	
	@Override
	public void end() {
	
		time += delta;
		
		shader.setUniformf("time", time);
		Entries<MineEffect> e = mines.entries();
		float originalAlpha = wr.batch.getColor().a;
		while (e.hasNext) {
			MineEffect m = e.next().value; 
			if (!m.handled && (m.a -= (1/FADE_TIME * delta)) < 0) {
				e.remove();
			} else {
				wr.batch.setColor(1, 1, 1, m.a * originalAlpha);
				wr.batch.draw(Assets.Tex.MINEHELP.t, m.x - DRAW_R, m.y - DRAW_R, 2*DRAW_R, 2*DRAW_R);
			}
		}		
		wr.batch.setColor(1, 1, 1, originalAlpha);
	}
	
	@Override
	public void resetContext() {
		shader.begin();
		shader.setUniformf("R", DRAW_R);
		shader.setUniformf("time", 0);
		shader.end();
	}
}
