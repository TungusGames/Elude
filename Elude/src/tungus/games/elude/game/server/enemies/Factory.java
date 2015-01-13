package tungus.games.elude.game.server.enemies;

import tungus.games.elude.game.server.Vessel;
import tungus.games.elude.game.server.World;

import com.badlogic.gdx.math.Vector2;

public class Factory extends StandingBase {

	private static final float RELOAD = 2;
	private static final float SPEED = 1;
	private static final float RADIUS = 1f;
	private static final float TURNSPEED = 20;
	
	public Factory(Vector2 pos, World w) {
		super(pos, EnemyType.FACTORY, null, w, EnemyType.FACTORY.hp, SPEED, 2*RADIUS);
		turnSpeed = TURNSPEED;
	}
	
	@Override
	protected boolean aiUpdate(float delta) {
		for (Vessel v : world.vessels) {
			if (v.bounds.overlaps(collisionBounds)) {
				v.pos.sub(pos).nor().scl(collisionBounds.radius + v.bounds.radius).add(pos);
			}
		}
		return super.aiUpdate(delta);
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
	protected float calcTurnGoal() {
		return t.set(targetPlayer().pos).sub(pos).angle()-90;
	}
}
