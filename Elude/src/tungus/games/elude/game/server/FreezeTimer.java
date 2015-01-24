package tungus.games.elude.game.server;

import tungus.games.elude.game.client.worldrender.renderable.FreezeRenderable;
import tungus.games.elude.game.client.worldrender.renderable.Renderable;

import com.badlogic.gdx.math.Vector2;

public class FreezeTimer extends Updatable {

	private float remainingTime = 0f;
	private Vector2 center = new Vector2();
	
	@Override
	public boolean update(float deltaTime) {
			if (remainingTime > 0)
				remainingTime -= deltaTime;
			return false;
	}

	public void freeze(float x, float y, float time) {
		if (!isFrozen()) {
			center.set(x, y);
		}		
		remainingTime = time;
	}
	
	public boolean isFrozen() {
		return remainingTime > 0f;
	}
	
	@Override
	public Renderable getRenderable() {
		return FreezeRenderable.create(remainingTime, center.x, center.y);
	}

}
