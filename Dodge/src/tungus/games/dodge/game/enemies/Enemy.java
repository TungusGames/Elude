package tungus.games.dodge.game.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public abstract class Enemy extends Sprite {
	
	Vector2 pos;
	Vector2 vel;
	
	protected final float width;
	protected final float height;
	
	public float hp;
	
	public Enemy(Vector2 pos, float width, float height, float hp, Texture texture) {
		super(texture);
		this.pos = pos;
		vel = new Vector2(0,0);
		this.width = width;
		this.height = height;
		setBounds(pos.x - width/2, pos.y - height/2, width, height);
	}
	
	public final void update(float deltaTime) {
		aiUpdate(deltaTime);
		pos.add(vel.x * deltaTime, vel.y * deltaTime);
		setPosition(pos.x - width/2, pos.y - height/2);
	}
	
	protected abstract void aiUpdate(float deltaTime);
	
}
