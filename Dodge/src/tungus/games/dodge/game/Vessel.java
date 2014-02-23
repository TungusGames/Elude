package tungus.games.dodge.game;

import tungus.games.dodge.Assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class Vessel extends Sprite {

	public static final float WIDTH = 0.75f;
	public static final float HEIGHT = 0.75f;
	public static final float MAX_SPEED = 6f;
	public static final float MAX_HP = 100f;
	
	public Vector2 pos;
	public Vector2 vel;
	public Controls controls; 
	
	public float hp = MAX_HP;
	public boolean isDead = false;
	
	public Vessel() {
		super(Assets.vessel);
		controls = new Controls();
		
		setBounds(World.WIDTH / 2 - WIDTH / 2, World.HEIGHT / 2 - HEIGHT / 2, WIDTH, HEIGHT);
		
		pos = new Vector2(World.WIDTH / 2, World.HEIGHT / 2);
		vel = new Vector2(0, 0);
		setOrigin(WIDTH / 2, HEIGHT / 2);
		
	}
	
	public void update(float deltaTime) {
		if (!isDead) {
			vel.set(controls.getDirection()).scl(MAX_SPEED);
			pos.add(vel.x * deltaTime, vel.y * deltaTime);
			if (!vel.equals(Vector2.Zero)) {
				setRotation(vel.angle()-90);
			}
			setPosition(pos.x - WIDTH / 2, pos.y - HEIGHT / 2);
			
			if (hp < 0) {
				isDead = true;
			}
		}
	}

}
