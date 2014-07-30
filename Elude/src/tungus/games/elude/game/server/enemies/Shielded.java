package tungus.games.elude.game.server.enemies;

import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.rockets.Rocket;
import tungus.games.elude.game.server.rockets.Rocket.RocketType;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Shielded extends StandingBase {
	private static final float RELOAD = 2;
	private static final float OUTER_R = 0.575f;
	private static final float INNER_R = OUTER_R / 2;
	private static final float TURNSPEED = 100; // Degrees per second
	
	private static final Vector2 t = new Vector2();
	
	private final Circle in, out;
	
	public Shielded(Vector2 pos, World w) {
		super(pos, EnemyType.STANDING, RocketType.SLOW_TURNING, w);
		turnSpeed = TURNSPEED; // Modify value that is only used for graphics for other enemy types
		in = new Circle(pos, INNER_R);
		out = collisionBounds;
	}
	
	@Override
	public boolean update(float deltaTime) {
		boolean b = super.update(deltaTime);
		in.x = out.x; 
		in.y = out.y;
		return b;
	}

	@Override
	protected boolean standingUpdate(float deltaTime) {
		if (timeSinceShot > RELOAD) {
			shootRocket(world.vessels.get(0).pos.cpy().sub(pos));
		}
		return false;
	}
	
	@Override
	public boolean isHitBy(Rocket r) {
		if (!out.overlaps(r.bounds))
			return false;
		if (in.overlaps(r.bounds)) {
			killByRocket(r);
			return true;
		}
		t.set(in.x, in.y).add(MathUtils.cosDeg(rot+90), MathUtils.sinDeg(rot+90));
		if (Intersector.pointLineSide(in.x, in.y, t.x, t.y, r.pos.x, r.pos.y) != -1) {
			return true;
		}
		if (Intersector.distanceLinePoint(in.x, in.y, t.x, t.y, r.pos.x, r.pos.y) <= r.bounds.radius) {
			return true;
		}
		return false;
	}
}
