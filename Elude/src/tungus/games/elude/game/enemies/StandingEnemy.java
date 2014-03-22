package tungus.games.elude.game.enemies;

import tungus.games.elude.Assets;
import tungus.games.elude.game.Vessel;
import tungus.games.elude.game.World;
import tungus.games.elude.game.rockets.Rocket;
import tungus.games.elude.game.rockets.TurningRocket;

import com.badlogic.gdx.math.Vector2;

public class StandingEnemy extends Enemy {
	
	private static final float DRAW_WIDTH = 0.6f;
	private static final float DRAW_HEIGHT = 1f;
	private static final float COLLIDER_SIZE = 0.5f;
	
	private static final float MAX_HP = 10f;
	private static final float SPEED = 4f;
	private static final float RELOAD = 2f;
	
	private static Vector2 tempVector = new Vector2();
	
	private final Vector2 targetPos;
	private boolean reachedTarget = false;
	
	private float timeSinceShot = 0f;
	private int shots = 0;
	
	boolean rocketType = false;
	
	public StandingEnemy(Vector2 pos, World w) {
		super(pos, COLLIDER_SIZE, DRAW_WIDTH, DRAW_HEIGHT, MAX_HP, Assets.standingEnemy, debrisFromColor(new float[]{0.1f,1,0.1f,1}), w);
		
		targetPos = new Vector2();
		getInnerTargetPos(pos, targetPos);
		
		vel.set(targetPos).sub(pos).nor().scl(SPEED);
		turnGoal = vel.angle()-90;
		setRotation(turnGoal);
		
	}

	@Override
	protected boolean aiUpdate(float deltaTime) {
		if (!reachedTarget) {
			if (pos.dst2(targetPos) < SPEED*SPEED*deltaTime*deltaTime) {
				pos.set(targetPos);
				reachedTarget = true;
				vel.set(Vector2.Zero);
			}
		} else {
			timeSinceShot += deltaTime;
			if (timeSinceShot > RELOAD) 
			{
				shots++;
				timeSinceShot -= RELOAD;
				Vessel target = world.vessels.get(0);
				Rocket r = null;
				if (!(shots % 3 == 0))
					r = new TurningRocket(this, pos.cpy(), new Vector2(target.pos).sub(pos), world, Assets.rocket, target, false);
				else
					r = new TurningRocket(this, pos.cpy(), new Vector2(target.pos).sub(pos), world, Assets.rocket, target, true);
				world.rockets.add(r);
			}
			turnGoal = tempVector.set(world.vessels.get(0).pos).sub(pos).angle()-90; // Turn towards player
		}
		return false;
	}

}
