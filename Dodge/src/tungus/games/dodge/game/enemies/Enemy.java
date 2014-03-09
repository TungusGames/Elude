package tungus.games.dodge.game.enemies;

import tungus.games.dodge.Assets;
import tungus.games.dodge.game.World;
import tungus.games.dodge.game.rockets.Rocket;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public abstract class Enemy extends Sprite {
	
	public static enum EnemyType {STANDING, MOVING, KAMIKAZE};
	
	public static final float MAX_GRAPHIC_TURNSPEED = 540;
	
	protected final static PooledEffect debrisFromColor(float[] color) {
		PooledEffect p = Assets.debris.obtain();
		Array<ParticleEmitter> emitters = p.getEmitters();
		for (int i = 0; i < emitters.size; i++) {
			emitters.get(i).getTint().setColors(color);
		}
		return p;
	}
	
	public static final Enemy newEnemy(World w, EnemyType t) {
		Enemy e = null;
		switch (t) {
		case STANDING:
			e = new StandingEnemy(w.randomPosOutsideEdge(new Vector2(), 1), w);
			break;
		case MOVING:
			e = new MovingEnemy(w.randomPosOutsideEdge(new Vector2(), 1), w);
			break;
		case KAMIKAZE:
			e = new Kamikaze(w.randomPosOutsideEdge(new Vector2(), 1), w);
			break;
		}
		return e;
	}
	
	protected final World world;
	
	public Vector2 pos;
	public Vector2 vel;
	
	public final Rectangle collisionBounds;
	
	public float hp;
	
	protected float turnGoal;
	
	public final PooledEffect onDestroy;
	
	public Enemy(Vector2 pos, float boundSize, float drawWidth, float drawHeight, float hp, TextureRegion texture, PooledEffect onDestroy, World w) {
		super(texture);
		this.pos = pos;
		this.onDestroy = onDestroy;
		this.world = w;
		vel = new Vector2(0,0);
		this.hp = hp;
		setBounds(pos.x - drawWidth/2, pos.y - drawHeight/2, drawWidth, drawHeight); //drawWidth and drawHeight are stored in the superclass
		setOrigin(drawWidth/2, drawHeight/2);
		collisionBounds = new Rectangle(pos.x - boundSize/2, pos.y - boundSize/2, boundSize, boundSize);
	}
	
	public final boolean update(float deltaTime) {
		boolean b = aiUpdate(deltaTime);
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
		return b;
	}
	
	protected abstract boolean aiUpdate(float deltaTime);
	
	public void kill(Rocket r) {
		onDestroy.setPosition(pos.x, pos.y);
		Array<ParticleEmitter> emitters = onDestroy.getEmitters();
		for (int i = 0; i < emitters.size; i++) {
			if (r != null)
				emitters.get(i).getAngle().setLow(r.vel.angle());
			else
				emitters.get(i).getAngle().setLow(MathUtils.random(360));
		}
		onDestroy.start();
		world.particles.add(onDestroy);
		
		world.enemies.remove(this);
		world.waveLoader.onEnemyDead(this);
		/*if (world.enemies.size() < 5) {
			world.enemies.add(new StandingEnemy(new Vector2(MathUtils.random()*20, 13)));
			world.enemies.add(new MovingEnemy(new Vector2(MathUtils.random()*20, -1)));
		}
		else {
			world.enemies.add(this instanceof MovingEnemy ? 
					new StandingEnemy(new Vector2(MathUtils.random()*20, MathUtils.randomBoolean() ? 13 : -1)) :
					new MovingEnemy(new Vector2(MathUtils.random()*20, MathUtils.randomBoolean() ? 13 : -1)));
		}*/
	}
	
}
