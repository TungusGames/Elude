package tungus.games.elude.game.server.laser;

import com.badlogic.gdx.math.Vector2;
import tungus.games.elude.game.server.World;

public class RotatingLaser extends Laser {
    private static Vector2 dirTemp = new Vector2();
    private static Vector2 sourceTemp = new Vector2();
    
    private final Vector2 center;
    private float angularVelocity;
    
    public RotatingLaser(World w, Vector2 center, Vector2 startDir, 
                  float radiusFromCenter, float angularVelocity) {
        super(w, new Vector2(center).add(startDir.scl(radiusFromCenter)), startDir);
        this.center = center;
        this.angularVelocity = angularVelocity;
    }
    
    @Override
    public boolean update(float deltaTime) {
        dirTemp.set(source).sub(center);
        dirTemp.rotate(deltaTime * angularVelocity);
        sourceTemp.set(dirTemp).add(center);
        dirTemp.nor();
        set(sourceTemp, dirTemp);
        return super.update(deltaTime);
    }
}
