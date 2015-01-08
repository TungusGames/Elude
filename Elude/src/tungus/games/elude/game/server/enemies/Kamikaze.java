package tungus.games.elude.game.server.enemies;

import tungus.games.elude.game.client.worldrender.renderable.Renderable.Effect;
import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.rockets.Rocket.RocketType;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Kamikaze extends StandingBase {
	
	private static final float COLLIDER_SIZE = 0.7f;
	
	private static final float SPEED = 4f;
	private static final float STANDING_TIME = 3f;
	private static final int ROCKETS_SHOT = 7;
	
	private float timeStood = 0;
	
	public Kamikaze(Vector2 pos, World w) {
		super(pos, EnemyType.KAMIKAZE, RocketType.FAST_TURNING, w, EnemyType.KAMIKAZE.hp, SPEED, COLLIDER_SIZE);
		turnSpeed = 0;
	}
	
	@Override
	protected boolean standingUpdate(float deltaTime) {
		timeStood += deltaTime;
		if (timeStood > STANDING_TIME) {
			explode();
			return true;
		}
		return false;
	}

	private void explode() {
		world.waveLoader.onEnemyHurt(this, hp);
		killBy(null);
		
		Effect.addExplosion(world.effects, pos);
		
		for (int i = 0; i < ROCKETS_SHOT; i++) {
			shootRocket(new Vector2(1,0).rotate(MathUtils.random(360)));
		}
	}

}
