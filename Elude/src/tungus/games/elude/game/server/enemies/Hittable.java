package tungus.games.elude.game.server.enemies;

import com.badlogic.gdx.math.Circle;

public interface Hittable {
	/**
	 * Test collision with a given circle and handle taking the given damage if hit
	 * @param c The bounds of the hitter
	 * @param damage The damage to take if hit
	 * @return True if hit, false if not 
	 */
	public boolean isHitBy(Circle c, float damage);
}
