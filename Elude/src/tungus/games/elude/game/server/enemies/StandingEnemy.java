package tungus.games.elude.game.server.enemies;

import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.rockets.Rocket.RocketType;

import com.badlogic.gdx.math.Vector2;

public class StandingEnemy extends StandingBase {
	
	private static final float DEFAULT_RELOAD = 2;
	private final float reload;
	
	public StandingEnemy(Vector2 pos, World w) {
		super(pos, EnemyType.STANDING, RocketType.SLOW_TURNING, w);
		reload = DEFAULT_RELOAD;
	}
	
	public StandingEnemy(Vector2 pos, EnemyType t, World w, RocketType type, float speed, float reload) {
		super(pos, EnemyType.STANDING, RocketType.SLOW_TURNING, w, StandingBase.DEFAULT_HP, speed, StandingBase.DEFAULT_COLLIDER_SIZE, true);
		this.reload = reload;
	}

	@Override
	protected boolean standingUpdate(float deltaTime) {
		if (timeSinceShot > reload) {
			shootRocket(world.vessels.get(0).pos.cpy().sub(pos));
		}
		return false;
	}

}
