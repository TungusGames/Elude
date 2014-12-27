package tungus.games.elude.game.server;

import tungus.games.elude.game.client.worldrender.renderable.Renderable;
import tungus.games.elude.game.client.worldrender.renderable.VesselRenderable;
import tungus.games.elude.util.CustomInterpolations.FadeinFlash;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;

public class Vessel extends Updatable {

	public static final float DRAW_WIDTH = 0.75f;		//Dimensions of the sprite drawn
	public static final float HALF_WIDTH = DRAW_WIDTH / 2;
	public static final float DRAW_HEIGHT = 0.945f;
	public static final float HALF_HEIGHT = DRAW_HEIGHT / 2;
	public static final float COLLIDER_SIZE = 0.7f;		//Dimensions of the bounding box for collisions
	public static final float COLLIDER_HALF = COLLIDER_SIZE / 2;
	public static final float SHIELD_SIZE = 1.3f;		//drawn size
	public static final float SHIELD_HALF_SIZE = SHIELD_SIZE / 2;
	public static final float MAX_GRAPHIC_TURNSPEED = 540;
	public static final float MAX_SPEED = 8f;
	public static final float MAX_HP = 100f;
	
	private static final Interpolation shieldOpacity = new FadeinFlash(0.08f, 0.6f);
	
	//private final World world;
	
	public Vector2 pos;
	public Vector2 vel;
	public float rot = 0;
	
	public Circle bounds;
		
	public float hp = MAX_HP;
	public boolean shielded = false;
	public float shieldAlpha = 0;
	
	private float shieldTimeLeft = 0f;
	private float fullShieldTime = 0f;
	public float speedBonus = 1f;
	public float speedBonusTime = 0f;
	
	public Vessel(World world) {
		//this.world = world;
		pos = new Vector2(World.WIDTH / 2, World.HEIGHT / 2);
		vel = new Vector2(0, 0);
		bounds = new Circle(pos, COLLIDER_SIZE/2);
	}
	
	public void setInput(Vector2 dir) {
		vel.set(dir).scl(MAX_SPEED);
	}
	
	@Override
	public boolean update(float deltaTime) {
		if (hp > 0) {
			if (speedBonusTime > 0f) {
				speedBonusTime -= deltaTime;
				vel.scl(speedBonus);
			}
			if (shielded) {
				if (shieldTimeLeft > 0f) {
					shieldTimeLeft -= deltaTime;
					shieldAlpha = shieldOpacity.apply(1-shieldTimeLeft/fullShieldTime);
				}
				else {
					removeShield();
				}
					
			}
			pos.add(vel.x * deltaTime, vel.y * deltaTime);
			
			if (pos.x + COLLIDER_HALF > World.WIDTH)				// Keep inside world bounds
				pos.x -= (pos.x + COLLIDER_HALF - World.WIDTH);
			else if (pos.x - COLLIDER_HALF < 0)
				pos.x += (COLLIDER_HALF - pos.x);
			if (pos.y + COLLIDER_HALF > World.HEIGHT)
				pos.y -= (pos.y + COLLIDER_HALF - World.HEIGHT);
			else if (pos.y - COLLIDER_HALF < 0)
				pos.y += (COLLIDER_HALF - pos.y);
			
			if (!vel.equals(Vector2.Zero)) {
				float goal = vel.angle()-90;
				float diff = goal - rot;
				if (diff < -180)
					diff += 360;
				if (diff > 180)
					diff -= 360;
				if (Math.abs(diff) < MAX_GRAPHIC_TURNSPEED * deltaTime)
					rot = goal;
				else
					rot += Math.signum(diff) * MAX_GRAPHIC_TURNSPEED * deltaTime;
			}
			bounds.x = pos.x;								// Update the bounds 
			bounds.y = pos.y;
			return false;
		} else {
			return true;
		}
	}
	
	public void addShield(float shieldTime) {
		fullShieldTime = shieldTimeLeft = shieldTime;
		shielded = true;
	}
	
	public void removeShield() {
		shieldAlpha = 0;
		shielded = false;
	}
	
	@Override
	public Renderable getRenderable() {
		return VesselRenderable.create(pos.x, pos.y, vel.x, vel.y, rot, shieldAlpha, id);
	}
}
