package tungus.games.elude.game.server.laser;

import com.badlogic.gdx.math.Vector2;
import tungus.games.elude.game.server.World;

public class RotatingLaser extends Laser {
    private static Vector2 temp = new Vector2();
    private static Vector2 temp2 = new Vector2();
    
    private final Vector2 center;
    private float angularVelocity;
    
    RotatingLaser(World w, Vector2 center, Vector2 startDir, 
                  float radiusFromCenter, float angularVelocity) {
        super(w, new Vector2(center).add(startDir.scl(radiusFromCenter)), startDir);
        this.center = center;
        this.angularVelocity = angularVelocity;
    }
    
    @Override
    public boolean update(float deltaTime) {
        temp.set(source).sub(center);
        temp.rotate(deltaTime * angularVelocity);
        temp2.set(temp).add(center);
        temp.nor();
        set(temp2, temp);
        return super.update(deltaTime);
    }
}
