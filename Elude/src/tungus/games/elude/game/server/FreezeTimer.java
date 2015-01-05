package tungus.games.elude.game.server;

import tungus.games.elude.game.client.worldrender.renderable.Renderable;

public class FreezeTimer extends Updatable {

	private float remainingTime = 0f;
	
	@Override
	public boolean update(float deltaTime) {
			if (remainingTime > 0)
				remainingTime -= deltaTime;
			return false;
	}

	public void freeze(float time) {
		remainingTime = time;
	}
	
	public boolean isFrozen() {
		return remainingTime > 0f;
	}
	
	@Override
	public Renderable getRenderable() {
		return null;
	}

}
