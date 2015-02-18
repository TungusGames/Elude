package tungus.games.elude.game.server.rockets;

import tungus.games.elude.Assets.Particles;
import tungus.games.elude.Assets.Tex;
import tungus.games.elude.game.client.worldrender.phases.RenderPhase;
import tungus.games.elude.game.client.worldrender.renderable.Renderable;
import tungus.games.elude.game.client.worldrender.renderable.Sprite;
import tungus.games.elude.game.client.worldrender.renderable.effect.ParticleAdder;
import tungus.games.elude.game.client.worldrender.renderable.effect.ParticleRemover;
import tungus.games.elude.game.server.Vessel;
import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.enemies.Enemy;

import com.badlogic.gdx.math.Vector2;

public class SwarmRocket extends TurningRocket {
	
	private static final float FADEOUT_TIME = 0.2f;
	
	public SwarmRocket(Enemy origin, RocketType type, Vector2 pos, Vector2 dir, World world, Vessel target) {
		super(origin, type, pos, dir, world, target);
	}
	@Override
	public void kill() {
		world.effects.add(ParticleRemover.create(id));
		world.effects.add(ParticleAdder.create(Particles.EXPLOSION_SMALL, pos.x, pos.y));
	}
	@Override
	public Renderable getRenderable() {
		float alpha = 0.8f;
		if (life < FADEOUT_TIME) {
			alpha = life / FADEOUT_TIME * 0.8f;
		}
		return Sprite.create(RenderPhase.ROCKET, Tex.SWARMROCKET_SPOT, pos.x, pos.y-0.8f, 0.6f, 1.8f, vel.angle()-90, 0.3f, 1.7f, alpha);
	}
}
