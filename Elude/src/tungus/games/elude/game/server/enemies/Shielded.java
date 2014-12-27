package tungus.games.elude.game.server.enemies;

import java.util.Iterator;

import tungus.games.elude.game.server.Updatable;
import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.rockets.Rocket;
import tungus.games.elude.game.server.rockets.Rocket.RocketType;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Shielded extends StandingBase {
	private static final float RELOAD = 2;
	private static final float OUTER_R = EnemyType.SHIELDED.halfWidth * 12/13f;
	private static final float INNER_R = EnemyType.SHIELDED.height - OUTER_R;
	private static final float POS_FROM_CIRCLE_MIDDLE = OUTER_R / 4;
	private static final float TURNSPEED = 35; // Degrees per second
	private static final float ARRIVE_DIST_FROM_EDGE = 4;
	
	private static final Vector2 t = new Vector2();
	
	private final Circle in, out;
	
	public Shielded(Vector2 pos, World w) {
		//super(pos, EnemyType.SHIELDED, RocketType.SLOW_TURNING, w);
		super(pos, EnemyType.SHIELDED, RocketType.SLOW_TURNING, w, EnemyType.SHIELDED.hp, StandingBase.DEFAULT_SPEED, OUTER_R*2, ARRIVE_DIST_FROM_EDGE);
		turnSpeed = TURNSPEED; // Modify value that is only used for graphics for other enemy types, more important here
		in = new Circle(pos, INNER_R);
		out = new Circle(pos, OUTER_R);
	}
	
	@Override
	public boolean update(float deltaTime) {
		boolean b = super.update(deltaTime);
		out.x = in.x = pos.x - MathUtils.cosDeg(rot+90) * POS_FROM_CIRCLE_MIDDLE;
		out.y = in.y = pos.y - MathUtils.sinDeg(rot+90) * POS_FROM_CIRCLE_MIDDLE;
		return b;
	}

	@Override
	protected boolean standingUpdate(float deltaTime) {
		if (timeSinceShot > RELOAD) {
			shootRocket();
		}
		return false;
	}
	
	@Override
	public boolean isHitBy(Rocket r) {
		if (!out.overlaps(r.boundsForEnemy))
			return false;
		if (in.overlaps(r.boundsForEnemy)) {
			takeDamage(r.dmg);
			if (hp <= 0) {
				killByRocket(r);
			}
			return true;
		}
		t.set(in.x, in.y).add(MathUtils.cosDeg(rot/*+90*/), MathUtils.sinDeg(rot/*+90*/));
		if (Intersector.pointLineSide(in.x, in.y, t.x, t.y, r.pos.x, r.pos.y) != -1) {
			return true;
		}
		if (Intersector.distanceLinePoint(in.x, in.y, t.x, t.y, r.pos.x, r.pos.y) <= r.boundsForEnemy.radius) {
			return true;
		}
		return false;
	}
	
	@Override
	protected float calcTurnGoal() {
		return closestRocketAngle() - 90;
	}
	
	private float closestRocketAngle() {
		Iterator<Updatable> it = world.updatables.iterator();
		float closestDist2 = World.WIDTH*World.WIDTH;
		float angle = t.set(world.vessels.get(0).pos).sub(pos).angle();
		while(it.hasNext()) {
			Updatable u = it.next();
			if (!(u instanceof Rocket))
				continue;
			Rocket r = (Rocket)u;
			float d2 = t.dst2(pos);
			if (d2 < closestDist2) {
				closestDist2 = d2;
				angle = t.set(r.pos).sub(pos).angle();
			}
		}
		return angle;
	}
}
