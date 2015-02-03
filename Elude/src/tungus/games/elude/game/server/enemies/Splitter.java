package tungus.games.elude.game.server.enemies;

import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.rockets.Rocket.RocketType;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Splitter extends Enemy {
	
	private static final float COLL = 0.9f;
	private static final float SPEED = 3f;
	private static final float RELOAD = 1.5f;
	
	private static final int DEFAULT_SPLITS = 2;	// How many more "generations" the first spawned enemies will create  
	private static final int SPLIT_INTO = 3;		// How many smaller enemies each dead will split into
	private static final float MIN_SIZE = 0.75f;	// Relatively as part of full size
	
	private final int splitsLeft;	// How many more "generations" this enemy will spawn
	private boolean arrived = false;
	
	public Splitter(Vector2 pos, World w) {
		this(pos, w, DEFAULT_SPLITS, !World.outerBounds.contains(pos));
	}
	
	public Splitter(Vector2 pos, World w, int splits, boolean edge) {
		super(pos, EnemyType.SPLITTER, coeff(splits, MIN_SIZE)*COLL, coeff(splits, 0.5f)*EnemyType.SPLITTER.hp, w, RocketType.SLOW_TURNING);
		if (edge) {
			vel.set(World.WIDTH/2, World.HEIGHT/2).sub(pos).nor().scl(SPEED);
		} else {
			vel.set(SPEED, 0).rotate(MathUtils.random(360));
		}
		splitsLeft = splits;
	}
	
	@Override
	protected boolean aiUpdate(float deltaTime) {
		if (!World.innerBounds.contains(pos)) {
			if (arrived) {
				vel.set(MathUtils.random(World.innerBounds.width) + World.EDGE, MathUtils.random(World.innerBounds.height) + World.EDGE).sub(pos).nor().scl(SPEED);
			}
		} else if (!arrived) {
			arrived = true;
		}
		if (timeSinceShot > RELOAD) {
			shootRocket();
		}
		return false;
	}
	
	@Override
	public void killBy(Circle hitter) {
		super.killBy(hitter);
		if (splitsLeft > 0) {
			for (int i = 0; i < SPLIT_INTO; i++) {
				world.addEnemy(new Splitter(pos.cpy(), world, splitsLeft-1, false));
			}
		}
	}
	
	@Override
	public float width() {
		return super.width() * coeff(splitsLeft, MIN_SIZE);
	}
	
	@Override
	public float height() {
		return super.height() * coeff(splitsLeft, MIN_SIZE);
	}
	
	/**
	 * Maps (0 to DEFAULT_SPLITS) to ("bottom" param to 1f)
	 * @param splitsLeft
	 * @param bottom The lowest value, used for last enemies
	 * @return The value to multiply size, HP with
	 */
	private static float coeff(int splitsLeft, float bottom) {
		//return (splitsLeft + DEFAULT_SPLITS) / (float)(DEFAULT_SPLITS * 2);
		return bottom + (1-bottom)/DEFAULT_SPLITS * splitsLeft;
	}
	
	public static float totalHP() {
		return recursiveHP(DEFAULT_SPLITS);
	}
	
	private static float recursiveHP(int r) {
		float hp = coeff(r, 0.5f)*EnemyType.SPLITTER.hp;
		if (r > 0) {
			hp += SPLIT_INTO * recursiveHP(r-1);
		}
		return hp;
	}
	
	
}
