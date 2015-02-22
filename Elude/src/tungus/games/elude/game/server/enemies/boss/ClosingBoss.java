package tungus.games.elude.game.server.enemies.boss;

import java.util.List;

import tungus.games.elude.Assets;
import tungus.games.elude.Assets.Tex;
import tungus.games.elude.game.client.worldrender.phases.RenderPhase;
import tungus.games.elude.game.client.worldrender.renderable.EnemyRenderable;
import tungus.games.elude.game.client.worldrender.renderable.Renderable;
import tungus.games.elude.game.client.worldrender.renderable.Sprite;
import tungus.games.elude.game.client.worldrender.renderable.effect.DebrisAdder;
import tungus.games.elude.game.client.worldrender.renderable.effect.ParticleAdder;
import tungus.games.elude.game.client.worldrender.renderable.effect.SoundEffect;
import tungus.games.elude.game.server.Updatable;
import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.enemies.Enemy;
import tungus.games.elude.game.server.laser.RotatingLaser;
import tungus.games.elude.game.server.rockets.Rocket;
import tungus.games.elude.game.server.rockets.Rocket.RocketType;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;


public class ClosingBoss extends Enemy {

	private static final float COLLIDER_RADIUS = 1f;
	private static final float LASER_SOURCE_DISTANCE = 1f;

	private static final int STATE_ENTER = 0;
	private static final int STATE_IN = 1;

	private static final float LASER_START_SPEED = 20; // Degrees per sec
	private static final float LASER_END_SPEED = 60;

	private static final float SPEED = 2f;

	private static final int SHOTS_START = 3;
	private static final int SHOTS_END = 7;
	private static final float RELOAD_START = 7.5f;
	private static final float RELOAD_END = 3.5f;
	private static final float SHORT_RELOAD = 0.35f;
	
	private static final EnemyType[][] SPAWN = new EnemyType[][]{{EnemyType.SHIELDED, EnemyType.MOVING, EnemyType.MOVING}, 
																 {EnemyType.MOVING, EnemyType.MOVING, EnemyType.MOVING, EnemyType.MOVING, EnemyType.MINER},
																 {EnemyType.MINER, EnemyType.MOVING, EnemyType.KAMIKAZE, EnemyType.MOVING}};
	private final Spawner spawner;

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
				2 * COLLIDER_RADIUS,
				w,
				RocketType.SLOW_TURNING);
		vel.set(3, 0);
		spawner = new Spawner(w, SPAWN);
		super.solid = true;		
		super.turnSpeed = 100f;
		countsForProgress = true;
	}

	@Override
	protected boolean aiUpdate(float deltaTime) {
		if (state == STATE_ENTER && pos.dst2(World.WIDTH/2, World.HEIGHT/2) < vel.len2()*deltaTime*deltaTime) {
			state = STATE_IN;            
			laser = new RotatingLaser(world, pos, new Vector2(1, 0), LASER_SOURCE_DISTANCE, LASER_END_SPEED - (LASER_END_SPEED - LASER_START_SPEED) * (hp / maxHp));
			world.addNextFrame.add(laser);
		}
		if (state == STATE_IN) {
			vel.set(world.vessels.get(0).pos).sub(pos).nor().scl(SPEED);
			timeSinceShot += deltaTime;
			if ((shotsFiredInVolley == 0 && timeSinceShot >= longReload) || (shotsFiredInVolley > 0 && timeSinceShot >= shortReload)) {
				shootRocket(new Vector2(1, 0).rotate(rot + 90));
				shotsFiredInVolley++;
				timeSinceShot = 0;
				if (shotsFiredInVolley == shotsAtOnce) {
					shotsFiredInVolley = 0;
				}
			}
			spawner.update(deltaTime, 1 - hp / maxHp);
		}
		return false;
	}
	
	@Override
	protected void shootRocket(RocketType t, Vector2 dir) {
		timeSinceShot = 0;
		Rocket r = Rocket.fromType(t, this, pos.cpy().add(dir.scl(LASER_SOURCE_DISTANCE)), dir.nor(), targetPlayer(), world);
		world.addNextFrame.add(r);
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
		for (Updatable u : world.updatables) {
			if (u instanceof Enemy && !(u instanceof TeleportingBoss || u instanceof ClosingBoss)) {
				((Enemy)u).killBy(null);
			}
		}
                world.effects.add(ParticleAdder.create(Assets.Particles.EXPLOSION_BIG, pos.x, pos.y));
		world.effects.add(DebrisAdder.create(type.debrisColor, id, pos.x, pos.y, Float.NaN, true));
		world.effects.add(SoundEffect.create(Assets.Sounds.EXPLOSION));
	}
	
	@Override
	public void putRenderables(List<List<Renderable>> phases) {
		Renderable back = getSpriteForLaserTurret();	// Puts back, following laser
		if (back != null) {
			phases.get(back.phase.ordinal()).add(back);
		}
		super.putRenderables(phases); // Puts front, following vessel
	}
	
	@Override
	public Renderable getRenderable() {
		return EnemyRenderable.create(id, hp/maxHp, Tex.BOSS1, pos.x, pos.y - width()/2 + height()/2, // Y coord correction for difference between image center and rotation center 
								width(), height(), rot, width()/2, width()/2);
	}
	
	private Renderable getSpriteForLaserTurret() {
		if (laser == null) {
			return null;
		}
		return Sprite.create(RenderPhase.ENEMY, Tex.BOSS1_BACK, pos.x, pos.y- width()/2 + height()/2, 
					width(), height(), laser.angle() - 90, width()/2, width()/2, 1);
	}
}
