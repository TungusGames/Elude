package tungus.games.elude.game.server.laser;

import tungus.games.elude.Assets.Tex;
import tungus.games.elude.game.client.worldrender.phases.RenderPhase;
import tungus.games.elude.game.client.worldrender.renderable.Renderable;
import tungus.games.elude.game.client.worldrender.renderable.Sprite;
import tungus.games.elude.game.server.Updatable;
import tungus.games.elude.game.server.Vessel;
import tungus.games.elude.game.server.World;
import tungus.games.elude.util.CustomMathUtils;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Laser extends Updatable {
    
	protected static final float WIDTH = 0.2f;
    protected static final float LENGTH = World.WIDTH + World.HEIGHT;
    protected static final float DAMAGE_PER_SECOND = 100f;
    
    private static final Vector2 temp = new Vector2();
    
    protected final World world;
    protected Vector2 source;    
    protected Vector2 end;
    
    private boolean over = false;
    
    public Laser(World w, Vector2 source, Vector2 dir) {
        this.world = w;
        this.source = source;
        this.end = source.cpy().add(dir.x * LENGTH, dir.y * LENGTH);
        //world.effects.add(SoundEffect.create(Sounds.LASERBEAM));
    }
    
    @Override
    public boolean update(float deltaTime) {
        Vessel hit = updateEnd();
        if (hit != null) {
        	hit.tryDamage(DAMAGE_PER_SECOND * deltaTime);            
        }
        return over;
    }
    
    private Vessel updateEnd() {
    	Vessel v = closestVesselHit();
        if (v != null) {
        	CustomMathUtils.lineCircleIntersectionPoint(source, end, v.bounds.x, v.bounds.y, v.bounds.radius + WIDTH / 2, end);
            end.add(temp.set(end).sub(source).nor().scl(v.bounds.radius*1.25f));
        } else {
            end.sub(source).nor().scl(LENGTH).add(source);
        }
        return v;
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
        end.set(source).add(dir.x * LENGTH, dir.y * LENGTH);
        updateEnd();
    }
    
    public void stop() {
        over = true;
    }
    
    @Override
    public Renderable getRenderable() {
    	return Sprite.create(RenderPhase.ROCKET, Tex.LASER, 
				 (source.x + end.x) / 2, (source.y + end.y) / 2, 
				 source.dst(end), 0.5f,
				 (float)Math.atan2(end.y - source.y, end.x - source.x) * MathUtils.radiansToDegrees, 
				 1);
    }
}
