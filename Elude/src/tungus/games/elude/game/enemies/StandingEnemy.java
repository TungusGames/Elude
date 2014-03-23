package tungus.games.elude.game.enemies;

import tungus.games.elude.Assets;
import tungus.games.elude.game.World;
import tungus.games.elude.game.rockets.Rocket.RocketType;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class StandingEnemy extends Enemy {
	
	private static final float DRAW_WIDTH = 0.6f;
	private static final float DRAW_HEIGHT = 1f;
	private static final float COLLIDER_SIZE = 0.5f;
	
	private static final float MAX_HP = 4f;
	private static final float SPEED = 4f;
	private static final float RELOAD = 2f;
	
	private static Vector2 tempVector = new Vector2();
	
	private final Vector2 targetPos;
	private boolean reachedTarget = false;
	
	private float timeSinceShot = 0f;
	
	private final float speed;
	private final float reload;
	
	boolean rocketType = false;
	
	public StandingEnemy(Vector2 pos, World w) {
		this(pos, w, Assets.standingEnemyGreen, RocketType.SLOW_TURNING, SPEED, RELOAD);
	}
	
	public StandingEnemy(Vector2 pos, World w, TextureRegion tex, RocketType type, float speed, float reload) {
		super(pos, COLLIDER_SIZE, DRAW_WIDTH, DRAW_HEIGHT, MAX_HP, tex, debrisFromColor(new float[]{0.1f,1,0.1f,1}), w, type);
		
		this.speed = speed;
		this.reload = reload;
		
		targetPos = new Vector2();
		getInnerTargetPos(pos, targetPos);
		
		vel.set(targetPos).sub(pos).nor().scl(speed);
		turnGoal = vel.angle()-90;
		setRotation(turnGoal);
		
	}

	@Override
	protected boolean aiUpdate(float deltaTime) {
		if (!reachedTarget) {
			if (pos.dst2(targetPos) < speed*speed*deltaTime*deltaTime) {
				pos.set(targetPos);
				reachedTarget = true;
				vel.set(Vector2.Zero);
			}
		} else {
			timeSinceShot += deltaTime;
			if (timeSinceShot > reload) 
			{
				timeSinceShot -= reload;
				shootRocket(world.vessels.get(0).pos.cpy().sub(pos));
			}
			turnGoal = tempVector.set(world.vessels.get(0).pos).sub(pos).angle()-90; // Turn towards player
		}
		return false;
	}

}
