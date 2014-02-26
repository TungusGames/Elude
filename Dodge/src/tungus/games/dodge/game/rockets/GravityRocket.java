package tungus.games.dodge.game.rockets;

import tungus.games.dodge.game.World;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;


public class GravityRocket extends Rocket {
	
	private static final float DEFAULT_G = 1;
	private static final float START_SPEED = 1;
	private static final Vector2 tempVector = new Vector2();
	
	private final float g;
	private final Vector2 playerPos;
	
	public GravityRocket(Vector2 pos, Vector2 dir, World world, TextureRegion texture, Vector2 playerPos) {
		this(pos, dir, world, texture, playerPos, DEFAULT_G);
	}
	
	public GravityRocket(Vector2 pos, Vector2 dir, World world, TextureRegion texture, Vector2 playerPos, float g) {
		super(pos, dir, world, texture);
		this.g = g;
		this.playerPos = playerPos;
		vel.nor().scl(START_SPEED);
	}

	@Override
	protected void aiUpdate(float deltaTime) {
		tempVector.set(playerPos).sub(pos);
		float r = tempVector.len();
		tempVector.scl(g * r);	// Div by r once for normalizing, twice for the laws of gravity
		vel.add(tempVector.scl(deltaTime));
	}

}
