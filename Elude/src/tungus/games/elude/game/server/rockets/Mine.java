package tungus.games.elude.game.server.rockets;

import tungus.games.elude.game.server.Vessel;
import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.enemies.Enemy;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

public class Mine extends Rocket {

	private static final float DAMAGE = 20f;
	private static final float SIZE = 2f;
	public static final float LIFETIME = 10f;
	public final Circle coreBounds;
	
	public Mine(Enemy origin, Vector2 pos, Vector2 dir, World world, Vessel target) {
		super(origin, RocketType.MINE, pos, dir, world, target, DAMAGE, LIFETIME, ROCKET_SIZE, SIZE);
		coreBounds = new Circle(pos, 0.1f);
		vel = new Vector2();
	}
	@Override
	protected void aiUpdate(float deltaTime) {}

}
