package tungus.games.elude.game.server.rockets;

import tungus.games.elude.Assets.Tex;
import tungus.games.elude.game.client.worldrender.phases.RenderPhase;
import tungus.games.elude.game.client.worldrender.renderable.Renderable;
import tungus.games.elude.game.client.worldrender.renderable.Sprite;
import tungus.games.elude.game.client.worldrender.renderable.effect.ParticleRemover;
import tungus.games.elude.game.server.Vessel;
import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.enemies.Enemy;

import com.badlogic.gdx.math.Vector2;

public class SwarmRocket extends TurningRocket {
	public SwarmRocket(Enemy origin, RocketType type, Vector2 pos, Vector2 dir, World world, Vessel target) {
		super(origin, type, pos, dir, world, target);
	}
	@Override
	public void kill() {
		world.effects.add(ParticleRemover.create(id));
	}
	@Override
	public Renderable getRenderable() {
		return Sprite.create(RenderPhase.ROCKET, Tex.SWARMROCKET_SPOT, pos.x, pos.y, 0.6f, 1.4f, vel.angle()-90, 0.8f);
	}
}
