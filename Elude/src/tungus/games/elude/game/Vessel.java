package tungus.games.elude.game;

import tungus.games.elude.Assets;
import tungus.games.elude.util.CustomInterpolations.FadeinFlash;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Vessel extends Sprite {

	public static final float DRAW_WIDTH = 0.75f;		//Dimensions of the sprite drawn
	public static final float DRAW_HEIGHT = 0.945f;
	public static final float COLLIDER_SIZE = 0.6f;		//Dimensions of the bounding box for collisions
	public static final float SHIELD_SIZE = 1.3f;		//Both drawn and collider size
	public static final float MAX_GRAPHIC_TURNSPEED = 540;
	public static final float MAX_SPEED = 6f;
	public static final float MAX_HP = 100f;
	
	private static final Interpolation shieldOpacity = new FadeinFlash(0.08f, 0.6f);
	
	public Vector2 pos;
	public Vector2 vel;
	
	public float drawWidth = DRAW_WIDTH;
	public float drawHeight = DRAW_HEIGHT;
	public Rectangle bounds;
		
	public float hp = MAX_HP;
	public boolean shielded = false;
	
	private float shieldTimeLeft = 0f;
	private float fullShieldTime = 0f;
	private final Sprite shield;
	public float speedBonus = 1f;
	public float speedBonusTime = 0f;
	
	//TODO particle
	
	public Vessel() {
		super(Assets.vessel);
		setBounds(World.WIDTH / 2 - DRAW_WIDTH / 2, World.HEIGHT / 2 - DRAW_HEIGHT / 2, DRAW_WIDTH, DRAW_HEIGHT);
		setOrigin(DRAW_WIDTH / 2, DRAW_HEIGHT / 2);
		
		shield = new Sprite(Assets.shield);
		shield.setBounds(World.WIDTH / 2 - SHIELD_SIZE / 2, World.HEIGHT / 2 - SHIELD_SIZE / 2, SHIELD_SIZE, SHIELD_SIZE);
		shield.setOrigin(SHIELD_SIZE/2, SHIELD_SIZE/2);
		Color c = shield.getColor();
		c.a = 0;
		shield.setColor(c);
		
		pos = new Vector2(World.WIDTH / 2, World.HEIGHT / 2);
		vel = new Vector2(0, 0);
		bounds = new Rectangle(pos.x - COLLIDER_SIZE/2, pos.y - COLLIDER_SIZE/2, COLLIDER_SIZE, COLLIDER_SIZE);
	}
	
	public void update(float deltaTime, Vector2 dir) {
		if (hp > 0) {
			vel.set(dir).scl(MAX_SPEED);
			if (speedBonusTime > 0f) {
				speedBonusTime -= deltaTime;
				vel.scl(speedBonus);
			}
			if (shielded) {
				if (shieldTimeLeft > 0f) {
					shieldTimeLeft -= deltaTime;
					Color c = shield.getColor();
					c.a = shieldOpacity.apply(1-shieldTimeLeft/fullShieldTime);
					shield.setColor(c);
				}
				else {
					Color c = shield.getColor();
					c.a = 0;
					shield.setColor(c);
					removeShield();
				}
					
			}
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
			setPosition(pos.x - drawWidth / 2, pos.y - drawHeight / 2);	// Update the drawn sprite
			shield.setPosition(pos.x-SHIELD_SIZE/2, pos.y-SHIELD_SIZE/2);
			bounds.x = pos.x - COLLIDER_SIZE / 2;								// Update the bounds 
			bounds.y = pos.y - COLLIDER_SIZE / 2;
		}
	}
	
	public void addShield(float shieldTime) {
		fullShieldTime = shieldTimeLeft = shieldTime;
		shielded = true;
	}
	
	public void removeShield() {
		shielded = false;
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		super.draw(batch);
		if (shielded)
			shield.draw(batch);
	}
}
