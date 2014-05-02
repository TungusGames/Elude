package tungus.games.elude.game.server.enemies;

import tungus.games.elude.Assets;
import tungus.games.elude.game.multiplayer.transfer.RenderInfo.Effect;
import tungus.games.elude.game.multiplayer.transfer.RenderInfo.Effect.EffectType;
import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.rockets.Rocket.RocketType;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Kamikaze extends Enemy {
	
	private static final float DRAW_WIDTH = 0.9f;
	private static final float DRAW_HEIGHT = 0.85f;
	private static final float COLLIDER_SIZE = 0.6f;
	
	private static final float MAX_HP = 4f;
	private static final float SPEED = 3f;
	private static final float STANDING_TIME = 4f;
	private static final int ROCKETS_SHOT = 7;
	
	private final Vector2 targetPos;
	private boolean reachedTarget = false;
	
	private float timeStood = 0;
	
	public Kamikaze(Vector2 pos, World w) {
		this(pos, w, Assets.kamikaze, RocketType.FAST_TURNING);
	}
	
	public Kamikaze(Vector2 pos, World w, TextureRegion tex, RocketType type) {
		super(pos, EnemyType.KAMIKAZE, COLLIDER_SIZE, DRAW_WIDTH, DRAW_HEIGHT, MAX_HP, w, type);
		
		targetPos = new Vector2();
		getInnerTargetPos(pos, targetPos);
		
		vel.set(targetPos).sub(pos).nor().scl(SPEED);
		turnGoal = vel.angle()-90;
		rot = turnGoal;
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
			timeStood += deltaTime;
			if (timeStood > STANDING_TIME) {
				explode();
				return true;
			}
		}
		return false;
	}

	private void explode() {
		kill(null);
		
		world.effects.add(new Effect(pos.x, pos.y, EffectType.EXPLOSION.ordinal())); //TODO pool
		
		for (int i = 0; i < ROCKETS_SHOT; i++) {
			shootRocket(new Vector2(1,0).rotate(MathUtils.random(360)));
		}
	}

}
