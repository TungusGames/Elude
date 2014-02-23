package tungus.games.dodge.game.enemies;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class Enemy extends Sprite {
	
	Vector2 pos;
	Vector2 vel;
	
	public final Rectangle collisionBounds;
	
	public float hp;
	
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
	}
	
	protected abstract void aiUpdate(float deltaTime);
	
}
