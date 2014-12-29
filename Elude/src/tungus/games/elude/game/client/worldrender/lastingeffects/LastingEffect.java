package tungus.games.elude.game.client.worldrender.lastingeffects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface LastingEffect {
	public void allowCompletion();
	public boolean isComplete();
	public void render(SpriteBatch batch, float delta);
}
