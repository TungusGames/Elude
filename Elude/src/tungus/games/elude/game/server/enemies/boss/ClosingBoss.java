package tungus.games.elude.game.server.enemies.boss;

import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.enemies.Enemy;
import tungus.games.elude.game.server.laser.RotatingLaser;
import tungus.games.elude.game.server.rockets.Rocket.RocketType;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;

public class ClosingBoss extends Enemy {

	private static final float RADIUS = 1f;

	private static final int STATE_ENTER = 0;
	private static final int STATE_IN = 1;

	private static final float LASER_START_SPEED = 20; // Degrees per sec
	private static final float LASER_END_SPEED = 90;

	private static final float SPEED = 3f;

	private static final int SHOTS_START = 3;
	private static final int SHOTS_END = 10;
	private static final float RELOAD_START = 5f;
	private static final float RELOAD_END = 2.5f;
	private static final float SHORT_RELOAD = 0.25f;

	private int shotsAtOnce = SHOTS_START;
	private float shortReload = SHORT_RELOAD;
	private float longReload = RELOAD_START;
	
	private int shotsFiredInVolley = 0;
	private float timeSinceShot = 0;
	private int state = STATE_ENTER;

	private RotatingLaser laser;


	public ClosingBoss(Vector2 v, World w) {
		super(v.set(-5, World.HEIGHT / 2),
				EnemyType.CLOSING_BOSS,
				2 * RADIUS,
				w,
				RocketType.FAST_TURNING);
		vel.set(3, 0);
		super.solid = true;
	}

	@Override
	protected boolean aiUpdate(float deltaTime) {
		if (state == STATE_ENTER && pos.dst2(World.WIDTH/2, World.HEIGHT/2) < vel.len2()*deltaTime*deltaTime) {
			state = STATE_IN;            
			laser = new RotatingLaser(world, pos, new Vector2(1, 0), RADIUS, LASER_END_SPEED - (LASER_END_SPEED - LASER_START_SPEED) * (hp / maxHp));
			world.addNextFrame.add(laser);
		}
		if (state == STATE_IN) {
			vel.set(world.vessels.get(0).pos).sub(pos).nor().scl(SPEED);
			timeSinceShot += deltaTime;
			if ((shotsFiredInVolley == 0 && timeSinceShot >= longReload) || (shotsFiredInVolley > 0 && timeSinceShot >= shortReload)) {
				shootRocket();
				shotsFiredInVolley++;
				timeSinceShot = 0;
				if (shotsFiredInVolley == shotsAtOnce) {
					shotsFiredInVolley = 0;
				}
			}
		}
		return false;
	}

	@Override
	protected void takeDamage(float dmg) {
		super.takeDamage(dmg);
		if (laser != null) {
			// Interpolate angular velocity from START to END as hp goes from maxHp to 0
			laser.angularVelocity = Interpolation.linear.apply(LASER_START_SPEED, LASER_END_SPEED, 1 - hp / maxHp);
		}
		shotsAtOnce = (int)Interpolation.linear.apply(SHOTS_START, SHOTS_END+1, 1 - hp / maxHp);
		longReload = Interpolation.linear.apply(RELOAD_START, RELOAD_END, 1 - hp / maxHp);
	}

	@Override
	public void killBy(Circle hitter) {
		super.killBy(hitter);
		laser.stop();
	}

}
