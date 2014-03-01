package tungus.games.dodge.game;

import tungus.games.dodge.Assets;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Vessel extends Sprite {

	public static final float DRAW_WIDTH = 0.75f;		//Dimensions of the sprite drawn
	public static final float DRAW_HEIGHT = 0.8f;
	public static final float MAX_GRAPHIC_TURNSPEED = 540;
	public static final float COLLIDER_SIZE = 0.6f;		//Dimensions of the bounding box for collisions
	public static final float MAX_SPEED = 6f;
	public static final float MAX_HP = 100f;
	
	public Vector2 pos;
	public Vector2 vel;
	
	public Rectangle bounds;
		
	public float hp = MAX_HP;
	
	private ParticleEffect particle;

	
	public Vessel() {
		super(Assets.vessel);
		setBounds(World.WIDTH / 2 - DRAW_WIDTH / 2, World.HEIGHT / 2 - DRAW_HEIGHT / 2, DRAW_WIDTH, DRAW_HEIGHT);
		
		pos = new Vector2(World.WIDTH / 2, World.HEIGHT / 2);
		bounds = new Rectangle(pos.x - COLLIDER_SIZE/2, pos.y - COLLIDER_SIZE/2, COLLIDER_SIZE, COLLIDER_SIZE);
		vel = new Vector2(0, 0);
		setOrigin(DRAW_WIDTH / 2, DRAW_HEIGHT / 2);
	}
	
	public void update(float deltaTime, Vector2 dir) {
		if (hp > 0) {
			vel.set(dir).scl(MAX_SPEED);
			pos.add(vel.x * deltaTime, vel.y * deltaTime);
			
			if (pos.x + COLLIDER_SIZE/2 > World.WIDTH)				// Keep inside world bounds
				pos.x -= (pos.x + COLLIDER_SIZE/2 - World.WIDTH);
			else if (pos.x - COLLIDER_SIZE/2 < 0)
				pos.x += (COLLIDER_SIZE/2 - pos.x);
			if (pos.y + COLLIDER_SIZE/2 > World.HEIGHT)
				pos.y -= (pos.y + COLLIDER_SIZE/2 - World.HEIGHT);
			else if (pos.y - COLLIDER_SIZE/2 < 0)
				pos.y += (COLLIDER_SIZE/2 - pos.y);
			
			if (!vel.equals(Vector2.Zero)) {
				float goal = vel.angle()-90;
				float current = getRotation();
				float diff = goal - current;
				if (diff < -180)
					diff += 360;
				if (diff > 180)
					diff -= 360;
				if (Math.abs(diff) < MAX_GRAPHIC_TURNSPEED * deltaTime)
					setRotation(goal);
				else
					setRotation(current + Math.signum(diff) * MAX_GRAPHIC_TURNSPEED * deltaTime);
			}
			setPosition(pos.x - DRAW_WIDTH / 2, pos.y - DRAW_HEIGHT / 2);	// Update the drawn sprite
			bounds.x = pos.x - COLLIDER_SIZE/2;								// Update the bounds 
			bounds.y = pos.y - COLLIDER_SIZE/2;
		}
	}

}
