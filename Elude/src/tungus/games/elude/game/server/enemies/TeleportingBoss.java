package tungus.games.elude.game.server.enemies;

import tungus.games.elude.game.server.Updatable;
import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.rockets.Rocket;
import tungus.games.elude.game.server.rockets.Rocket.RocketType;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class TeleportingBoss extends StandingBase {
	
	private static final int STATE_SHOOT = 0;
	private static final int STATE_SPAWN = 1;
	private static final int STATE_LASER = 2;
	private static final int STATE_TELEPORT_FADE_OUT = 3;
	private static final int STATE_TELEPORT_FADE_IN = 4;
	
	private static final float WAIT_TIME = 5f;
	private static final float[] STATE_TIME = {5f, 1f, 5f, 0.5f, 0.5f};
	
	private static final float RADIUS = 1f;
	
	private int state = STATE_SHOOT;
	private float stateTime = 0;
	private boolean waiting = false;
	
	private float sizeScalar = 1f;
	
	public TeleportingBoss(Vector2 pos, World w) {
		super(pos, EnemyType.BOSS_TELEPORT, RocketType.FAST_TURNING, w, DEFAULT_SPEED, RADIUS);
	}
	@Override
	protected boolean standingUpdate(float deltaTime) {
		stateTime += deltaTime;
		switch (state) {
		case STATE_SHOOT:
		case STATE_SPAWN:
		case STATE_LASER:
			if (rocketsComing()) {
				setState(STATE_TELEPORT_FADE_OUT);
				return false;
			} else if (waiting) {
				if (stateTime > WAIT_TIME) {
					waiting = false;
					stateTime = 0;
					doAction(deltaTime);
				} else doAction(deltaTime);
			}
			break;
		case STATE_TELEPORT_FADE_OUT:
			sizeScalar = cosFade(stateTime);
			break;
		case STATE_TELEPORT_FADE_IN:
			sizeScalar = 1 - cosFade(stateTime);
			break;
		}
		if (stateTime > STATE_TIME[state]) {
			switch (state) {
			case STATE_SHOOT:
				setState(STATE_SPAWN); waiting = true;
				break;
			case STATE_SPAWN:
				setState(STATE_LASER); waiting = true;
				break;
			case STATE_LASER:
				setState(STATE_SHOOT); waiting = true;
				break;
			case STATE_TELEPORT_FADE_OUT:
				setState(STATE_TELEPORT_FADE_IN);
				pos.x = MathUtils.random() * World.WIDTH;
				pos.y = MathUtils.random() * World.HEIGHT;
				break;
			case STATE_TELEPORT_FADE_IN:
				sizeScalar = 1f;
				setState(STATE_SHOOT); waiting = true;
				break;
			}
		}
		return false;
	}
	
	private void setState(int state) {
		this.state = state;
		stateTime = 0;
	}
	
	public float width() {
		return super.width() * sizeScalar;
	}
	
	public float height() {
		return super.height() * sizeScalar;
	}
	private boolean rocketsComing() {
		for (Updatable u : world.updatables) {
			if (u instanceof Rocket) {
				Rocket r = (Rocket)u;
				Vector2 p2 = r.pos.cpy().add(r.vel.scl(STATE_TIME[STATE_TELEPORT_FADE_OUT])); //The point the rocket would reach in the time we could fade out
				if (Intersector.distanceSegmentPoint(r.pos, p2, pos) < RADIUS / 2) //If it would hit us in that time
					return true;
			}
		}
		return false;
	}
	
	private float cosFade(float t) {
		return Math.max(MathUtils.cos(t * MathUtils.PI / STATE_TIME[STATE_TELEPORT_FADE_OUT] / 2), 0f); //Voodo magic...
	}
	
	private void doAction(float deltaTime) {
		if (timeSinceShot > 1f)
			shootRocket();
		//TODO Implement different states
	}
}
