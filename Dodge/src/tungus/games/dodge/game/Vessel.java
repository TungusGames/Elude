package tungus.games.dodge.game;

import tungus.games.dodge.Assets;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Vessel extends Sprite {

	public static final float DRAW_WIDTH = 0.75f;		//Dimensions of the sprite drawn
	public static final float DRAW_HEIGHT = 0.8f;
	public static final float COLLIDER_SIZE = 0.6f;	//Dimensions of the bounding box for collisions
	public static final float MAX_SPEED = 6f;
	public static final float MAX_HP = 100f;
	
	public Vector2 pos;
	public Vector2 vel;
	
	public Rectangle bounds;
	
	public Controls controls; 
	
	public float hp = MAX_HP;
	
	public Vessel() {
		super(Assets.vessel);
		controls = new Controls();
		
		setBounds(World.WIDTH / 2 - DRAW_WIDTH / 2, World.HEIGHT / 2 - DRAW_HEIGHT / 2, DRAW_WIDTH, DRAW_HEIGHT);
		
		pos = new Vector2(World.WIDTH / 2, World.HEIGHT / 2);
		bounds = new Rectangle(pos.x - COLLIDER_SIZE/2, pos.y - COLLIDER_SIZE/2, COLLIDER_SIZE, COLLIDER_SIZE);
		vel = new Vector2(0, 0);
		setOrigin(DRAW_WIDTH / 2, DRAW_HEIGHT / 2);
		
	}
	
	public void update(float deltaTime) {
		if (hp > 0) {
			vel.set(controls.getDirection()).scl(MAX_SPEED);
			pos.add(vel.x * deltaTime, vel.y * deltaTime);
			if (!vel.equals(Vector2.Zero)) {
				setRotation(vel.angle()-90);
			}
			setPosition(pos.x - DRAW_WIDTH / 2, pos.y - DRAW_HEIGHT / 2);	// Update the drawn sprite
			bounds.x = pos.x - COLLIDER_SIZE/2;							// Update the bounds 
			bounds.y = pos.y - COLLIDER_SIZE/2;
			
		}
	}

}
