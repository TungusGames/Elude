package tungus.games.dodge.game.enemies;

import tungus.games.dodge.Assets;
import tungus.games.dodge.game.World;
import tungus.games.dodge.game.rockets.Rocket;
import tungus.games.dodge.game.rockets.TurningRocketAI;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
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
	
	public StandingEnemy(Vector2 pos) {
		super(pos, COLLIDER_SIZE, DRAW_WIDTH, DRAW_HEIGHT, MAX_HP, Assets.standingEnemy);
		targetPos = new Vector2();
		targetPos.x = MathUtils.random() * (World.WIDTH - 2*World.EDGE) + World.EDGE;
		targetPos.y = MathUtils.random() * (World.HEIGHT - 2*World.EDGE) + World.EDGE;
		targetPos.lerp(pos, MathUtils.random()*0.8f + 0.2f);
		vel.set(targetPos).sub(pos).nor().scl(SPEED);
		setRotation(vel.angle()-90);
	}

	@Override
	protected void aiUpdate(float deltaTime) {
		if (!reachedTarget) {
			if (pos.dst2(targetPos) < SPEED*SPEED*deltaTime) {
				pos.set(targetPos);
				reachedTarget = true;
				vel.set(Vector2.Zero);
			}
		} else {
			timeSinceShot += deltaTime;
			if (timeSinceShot > RELOAD) {
				timeSinceShot -= RELOAD;
				World w = World.INSTANCE;
				Vector2 playerPos = w.vessels.get(0).pos;
				Rocket r = new Rocket(new TurningRocketAI(playerPos), pos.cpy(), new Vector2(playerPos).sub(pos), w, Assets.rocket);
				w.rockets.add(r);
			}
			setRotation(tempVector.set(World.INSTANCE.vessels.get(0).pos).sub(pos).angle()-90); // Turn towards player
		}
	}

}
