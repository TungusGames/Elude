package tungus.games.dodge.game.enemies;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class Enemy extends Sprite {
	
	public static final float MAX_GRAPHIC_TURNSPEED = 540;
	
	public Vector2 pos;
	public Vector2 vel;
	
	public final Rectangle collisionBounds;
	
	public float hp;
	
	protected float turnGoal;
	
	private ParticleEffect particle;

	
	public Enemy(Vector2 pos, float boundSize, float drawWidth, float drawHeight, float hp, TextureRegion texture) {
		super(texture);
		this.pos = pos;
		
		vel = new Vector2(0,0);
		this.hp = hp;
		setBounds(pos.x - drawWidth/2, pos.y - drawHeight/2, drawWidth, drawHeight); //drawWidth and drawHeight are stored in the superclass
		setOrigin(drawWidth/2, drawHeight/2);
		collisionBounds = new Rectangle(pos.x - boundSize/2, pos.y - boundSize/2, boundSize, boundSize);
	}
	
	public final void update(float deltaTime) {
		aiUpdate(deltaTime);
		pos.add(vel.x * deltaTime, vel.y * deltaTime);
		setPosition(pos.x - getWidth()/2, pos.y - getHeight()/2);
		collisionBounds.x = pos.x - collisionBounds.width/2;
		collisionBounds.y = pos.y - collisionBounds.height/2;
		float current = getRotation();
		float diff = turnGoal - current;
		if (diff < -180)
			diff += 360;
		if (diff > 180)
			diff -= 360;
		if (Math.abs(diff) < MAX_GRAPHIC_TURNSPEED * deltaTime)
			setRotation(turnGoal);
		else
			setRotation(current + Math.signum(diff) * MAX_GRAPHIC_TURNSPEED * deltaTime);
	}
	
	protected abstract void aiUpdate(float deltaTime);
	
}
