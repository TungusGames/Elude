package tungus.games.elude.game.server.rockets;

import com.badlogic.gdx.math.Vector2;
import tungus.games.elude.game.client.worldrender.renderable.effect.ParticleRemover;
import tungus.games.elude.game.server.Vessel;
import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.enemies.Enemy;

public class SwarmRocket extends TurningRocket {
    public SwarmRocket(Enemy origin, RocketType type, Vector2 pos, Vector2 dir, World world, Vessel target) {
    	super(origin, type, pos, dir, world, target);
    }
    @Override
    public void kill() {
	world.effects.add(ParticleRemover.create(id));
    }
}
