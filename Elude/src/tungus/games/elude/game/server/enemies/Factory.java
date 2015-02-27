package tungus.games.elude.game.server.enemies;

import tungus.games.elude.Assets.Particles;
import tungus.games.elude.game.client.worldrender.renderable.effect.DebrisAdder;
import tungus.games.elude.game.client.worldrender.renderable.effect.ParticleAdder;
import tungus.games.elude.game.server.World;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

public class Factory extends StandingBase {

	private static final float RELOAD = 1.5f;
	private static final float SPEED = 1.5f;
	private static final float RADIUS = 1f;
	private static final float TURNSPEED = 20;
	
	public Factory(Vector2 pos, World w) {
		super(pos, EnemyType.FACTORY, null, w, EnemyType.FACTORY.hp, SPEED, 2*RADIUS, 4.5f);
		turnSpeed = TURNSPEED;
		solid = true;
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
	
	@Override 
	public void killBy(Circle hitter) {
		super.killBy(hitter);
		world.effects.add(ParticleAdder.create(Particles.EXPLOSION_MED, pos.x, pos.y));
		world.effects.add(DebrisAdder.create(type.debrisColor, id, pos.x, pos.y, Float.NaN, Particles.DEBRIS_MED));
	}
}
