package tungus.games.elude.game.server.enemies;

import tungus.games.elude.Assets.Particles;
import tungus.games.elude.game.client.worldrender.renderable.effect.ParticleAdder;
import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.rockets.Rocket.RocketType;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Minion extends Enemy {
	
	private static final float COLL = 0.65f;
	private static final float SPEED = 2.5f;
	private static final float RELOAD = 1.3f;
	
	private static final float TIME_OUT = 1f;
	
	private final Factory parent;
	private int state = 0;
	private float timeSinceCreation = 0;
	private final Vector2 goal;
	
	public Minion(Vector2 pos, float dir, Factory parent, World w) {
		super(pos, EnemyType.MINION, COLL, w, RocketType.FAST_TURNING);
		vel.set(1, 0).rotate(dir).scl(SPEED);
		this.parent = parent;
		goal = new Vector2(parent.pos);
		goal.add((float)MathUtils.random.nextGaussian() * 5, (float)MathUtils.random.nextGaussian() * 5);
		goal.x = MathUtils.clamp(goal.x, World.EDGE, World.WIDTH - World.EDGE);
		goal.y = MathUtils.clamp(goal.y, World.EDGE, World.HEIGHT - World.EDGE);
		float mindist = (parent.collisionBounds.radius + collisionBounds.radius);
		if (goal.dst2(parent.pos) < mindist * mindist) {
			goal.sub(parent.pos).nor().scl(mindist + MathUtils.random() * 2);
		}
		//goal = new Vector2(MathUtils.random(World.innerBounds.width) + World.EDGE, MathUtils.random(World.innerBounds.height) + World.EDGE);
	}
	
	@Override
	protected boolean aiUpdate(float deltaTime) {
		timeSinceCreation += deltaTime;
		if (state == 0 && timeSinceCreation > TIME_OUT) {
			state++;
			vel.set(goal).sub(pos).nor().scl(SPEED);
		} else if (state == 1 && pos.dst2(goal) < SPEED*SPEED*deltaTime*deltaTime) {
			state++;
			pos.set(goal);
			vel.set(0, 0);
		}
		
		if (timeSinceShot > RELOAD) {
			shootRocket();
		}
		
		if (parent.hp <= 0) {
			killBy(null);
			world.effects.add(ParticleAdder.create(Particles.EXPLOSION, pos.x, pos.y, id));
			return true;
		}
		return false;
	}
}
