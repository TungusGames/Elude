package tungus.games.elude.game.server.enemies;

import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.rockets.Rocket;

import com.badlogic.gdx.math.Vector2;

public class Factory extends StandingBase {

	private static final float RELOAD = 2;
	private static final float SPEED = 1;
	private static final float RADIUS = 1f;
	private static final float TURNSPEED = 20;
	
	public Factory(Vector2 pos, World w) {
		super(pos, EnemyType.FACTORY, null, w, EnemyType.FACTORY.hp, SPEED, 0);
		turnSpeed = TURNSPEED;
	}

	@Override
	protected boolean standingUpdate(float deltaTime) {
		if (timeSinceShot > RELOAD) {
			world.addEnemy(new Minion(pos.cpy(), rot+90, this, world));
			timeSinceShot = 0;
			turnSpeed = 0;
		}
		return false;
	}
	
	@Override
	public void killByRocket(Rocket r) {
		super.killByRocket(r);
	}
	
	@Override
	protected float calcTurnGoal() {
		return t.set(targetPlayer().pos).sub(pos).angle()-90;
	}
	
	@Override
	public boolean hitBy(Rocket r) {
		if (pos.dst2(r.pos) < (RADIUS+r.boundsForEnemy.radius)*(RADIUS+r.boundsForEnemy.radius)) {
			takeDamage(r.dmg);
			if (hp <= 0) {
				killByRocket(r);
			}
			return true;
		}
		return false;
	}

}
