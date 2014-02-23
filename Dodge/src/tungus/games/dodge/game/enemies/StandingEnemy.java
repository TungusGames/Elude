package tungus.games.dodge.game.enemies;

import tungus.games.dodge.Assets;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class StandingEnemy extends Enemy {
	
	private static final float WIDTH = 0.5f;
	private static final float HEIGHT = 0.5f;
	private static final float MAX_HP = 10f;
	
	
	
	public StandingEnemy(Vector2 pos) {
		super(pos, WIDTH, HEIGHT, MAX_HP, Assets.vessel);
		
	}

	@Override
	protected void aiUpdate(float deltaTime) {
		
	}

}
