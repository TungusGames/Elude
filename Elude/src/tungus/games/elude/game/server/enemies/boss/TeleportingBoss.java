package tungus.games.elude.game.server.enemies.boss;

import com.badlogic.gdx.math.Circle;
import tungus.games.elude.game.server.Updatable;
import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.enemies.StandingBase;
import tungus.games.elude.game.server.rockets.Rocket;
import tungus.games.elude.game.server.rockets.Rocket.RocketType;
import tungus.games.elude.util.CustomMathUtils;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import tungus.games.elude.Assets;
import tungus.games.elude.game.client.worldrender.renderable.effect.DebrisAdder;
import tungus.games.elude.game.client.worldrender.renderable.effect.ParticleAdder;
import tungus.games.elude.game.client.worldrender.renderable.effect.SoundEffect;
import tungus.games.elude.game.server.enemies.Enemy;

public class TeleportingBoss extends StandingBase {

	private static final int STATE_SHOOT = 0;
	//private static final int STATE_SPAWN = 1;
	//private static final int STATE_LASER = 2;
	private static final int STATE_TELEPORT_FADE_OUT = 3;
	private static final int STATE_TELEPORT_FADE_IN = 4;

	private static final float WAIT_TIME = 2.5f;
	private static final float[] STATE_TIME = {5f, 1f, 5f, 0.25f, 0.25f};

	private static final float RADIUS = 0.6f;

	private static final float RELOAD = 1/45f;
	
	private static final EnemyType[][] SPAWN = 
			new EnemyType[][]{	{EnemyType.MOVING, EnemyType.KAMIKAZE}, 
		 					  	{EnemyType.MINER, EnemyType.MACHINEGUNNER, EnemyType.MACHINEGUNNER, EnemyType.SHIELDED, EnemyType.SHIELDED},
		 					  	{EnemyType.MINER, EnemyType.SHARPSHOOTER, EnemyType.SHARPSHOOTER, EnemyType.MACHINEGUNNER, EnemyType.MACHINEGUNNER}};

	private final Spawner spawner;
	
	private int state = STATE_SHOOT;
	private float stateTime = 0;
	private boolean waiting = false;

	private float sizeScalar = 1f;

	public TeleportingBoss(Vector2 pos, World w) {
		super(pos, EnemyType.BOSS_TELEPORT, RocketType.SWARM, w, DEFAULT_SPEED, RADIUS * 2);
		spawner = new Spawner(w, SPAWN);
	}
	@Override
	protected boolean standingUpdate(float deltaTime) {
		stateTime += deltaTime;
		switch (state) {
		case STATE_SHOOT:
		//case STATE_SPAWN:
		//case STATE_LASER:
			if (rocketsComing(STATE_TIME[STATE_TELEPORT_FADE_OUT])) {
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
				setState(/*STATE_SPAWN*/STATE_SHOOT); waiting = true;
				break;
			/*case STATE_SPAWN:
				setState(STATE_LASER); waiting = true;
				break;
			case STATE_LASER:
				setState(STATE_SHOOT); waiting = true;
				break;*/
			case STATE_TELEPORT_FADE_OUT:
				setState(STATE_TELEPORT_FADE_IN);
				setNewPos();				
				break;
			case STATE_TELEPORT_FADE_IN:
				sizeScalar = 1f;
				setState(STATE_SHOOT); waiting = true;
				break;
			}
		}
		collisionBounds.setRadius(RADIUS * sizeScalar);
		spawner.update(deltaTime, 1 - hp/maxHp);
		return false;
	}

	private void setNewPos() {
		do {
			pos.x = MathUtils.random() * (World.WIDTH - 2*World.EDGE) + World.EDGE;
			pos.y = MathUtils.random() * (World.HEIGHT - 2*World.EDGE) + World.EDGE;
		} while (rocketsComing(STATE_TIME[STATE_TELEPORT_FADE_OUT]+STATE_TIME[STATE_TELEPORT_FADE_IN]));
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

	private boolean rocketsComing(float time) {
		for (Updatable u : world.updatables) {
			if (u instanceof Rocket) {
				Rocket r = (Rocket)u;
				Vector2 rToCenter = pos.cpy().sub(r.pos);
				float dist = rToCenter.len();
				if (dist <= r.vel.cpy().scl(time*1.3f).len() + RADIUS + Rocket.ROCKET_SIZE) {
					double tangentAngle = Math.asin(RADIUS / rToCenter.len()) * MathUtils.radiansToDegrees;// Angle of tangent line (erinto)
					double a = CustomMathUtils.convexSub(r.vel.angle(), rToCenter.angle());
					if (a <= tangentAngle + 5)
						return true;
				}
			}
		}
		return false;
	}

	private float cosFade(float t) {
		return Math.max(MathUtils.cos(t * MathUtils.PI / STATE_TIME[STATE_TELEPORT_FADE_OUT] / 2), 0f); //Voodo magic...
	}

	private void doAction(float deltaTime) {
		if (timeSinceShot > RELOAD) {
			shootRocket();
		}
		//TODO Implement different states
	}
        
        @Override
	public void killBy(Circle hitter) {
		super.killBy(hitter);
		for (Updatable u : world.updatables) {
			if (u instanceof Enemy && u != this) {
				((Enemy)u).killBy(null);
			}
		}
                world.effects.add(ParticleAdder.create(Assets.Particles.EXPLOSION_BIG, pos.x, pos.y));
		world.effects.add(DebrisAdder.create(type.debrisColor, id, pos.x, pos.y, Float.NaN, true));
		world.effects.add(SoundEffect.create(Assets.Sounds.EXPLOSION));
	}
}
