package tungus.games.elude.game.server.rockets;

import tungus.games.elude.Assets;
import tungus.games.elude.game.client.worldrender.renderable.Renderable;
import tungus.games.elude.game.client.worldrender.renderable.StraightRocketRenderable;
import tungus.games.elude.game.client.worldrender.renderable.effect.SoundEffect;
import tungus.games.elude.game.server.Vessel;
import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.enemies.Enemy;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class StraightRocket extends Rocket {
	
	public static final float SPEED = 17.5f;
	private static final float MAX_CORNER_BOUNCE_TIME = 0.05f; 
	
	private boolean bounced = false;
	private float timeSinceBounce = 0;
	
	public StraightRocket(Enemy origin, Vector2 pos, Vector2 dir, World world, Vessel target) {
		super(origin, RocketType.STRAIGHT, pos, dir, world, target);
		world.effects.add(SoundEffect.create(Assets.Sounds.LASERSHOT));
	}

	@Override
	protected void aiUpdate(float deltaTime) {
		if (bounced) {
			timeSinceBounce += deltaTime;
		}
	}
	
	@Override
	protected boolean hitWall(boolean vert) {
		if (timeSinceBounce < MAX_CORNER_BOUNCE_TIME) {
			if (vert)
				vel.x = -vel.x;
			else
				vel.y = -vel.y;
			pos.x = MathUtils.clamp(pos.x, bounds.radius, World.WIDTH-bounds.radius);
			pos.y = MathUtils.clamp(pos.y, bounds.radius, World.HEIGHT-bounds.radius);
			bounced = true;
			return false;
		}
		kill();
		return true;
	}
	
	@Override
	public Renderable getRenderable() {
		return StraightRocketRenderable.create(pos.x, pos.y, vel.angle(), id);
	}

}
