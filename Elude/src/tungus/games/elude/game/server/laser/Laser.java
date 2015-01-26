package tungus.games.elude.game.server.laser;

import tungus.games.elude.game.client.worldrender.renderable.LaserRenderable;
import tungus.games.elude.game.client.worldrender.renderable.Renderable;
import tungus.games.elude.game.server.Updatable;
import tungus.games.elude.game.server.Vessel;
import tungus.games.elude.game.server.World;
import tungus.games.elude.util.CustomMathUtils;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;

public class Laser extends Updatable {
    
    private static final float WIDTH = 0.5f;
    private static final float LENGTH = World.WIDTH + World.HEIGHT;
    private static final float DAMAGE_PER_SECOND = 150;
    protected final World world;
    protected Vector2 source;    
    protected Vector2 end;
    private Vector2 farPoint;
    
    private boolean over = false;
    
    public Laser(World w, Vector2 source, Vector2 dir) {
        this.world = w;
        this.source = source;
        this.farPoint = source.cpy().add(dir.x * LENGTH, dir.y * LENGTH);
        this.end = farPoint.cpy();
    }
    
    @Override
    public boolean update(float deltaTime) {
        Vessel closestVessel = closestVesselHit();
        if (closestVessel != null) {
            closestVessel.tryDamage(DAMAGE_PER_SECOND * deltaTime);
            CustomMathUtils.lineCircleIntersectionPoint(source, end, closestVessel.bounds, end);
        } else {
            end.set(farPoint);
        }
        return over;
    }
    
    private Vessel closestVesselHit() {
        Vessel closestVessel = null;
        for (Vessel v : world.vessels) {
            if (Intersector.distanceSegmentPoint(source, end, v.pos) < v.bounds.radius + WIDTH / 2) {
                if (closestVessel == null || source.dst2(closestVessel.pos) > source.dst2(v.pos)) {
                    closestVessel = v;
                }
            }
        }
        return closestVessel;
    }
    
    public void set(Vector2 source, Vector2 dir) {
        this.source.set(source);
        farPoint.set(source).add(dir.x * LENGTH, dir.y * LENGTH);
        end.set(farPoint);
        Vessel v = closestVesselHit();
        if (v != null) {
            CustomMathUtils.lineCircleIntersectionPoint(source, end, v.bounds, end);
        }
    }
    
    public void stop() {
        over = true;
    }
    
    @Override
    public Renderable getRenderable() {
        return LaserRenderable.create(source, end);
    }
}
