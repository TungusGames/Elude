package tungus.games.elude.game.server.laser;

import tungus.games.elude.game.server.World;

import com.badlogic.gdx.math.Vector2;

public class RotatingLaser extends Laser {
    private static Vector2 sourceTemp = new Vector2();
    private static Vector2 dirTemp = new Vector2();
    
    private Vector2 center;
    private Vector2 posFromCenter;
    private float angularVelocity;
    
    public RotatingLaser(World w, Vector2 center, Vector2 startDir, 
                  float radiusFromCenter, float angularVelocity) {
        super(w, new Vector2(center).add(startDir.scl(radiusFromCenter)), startDir);
        this.center = center;
        this.posFromCenter = new Vector2(source).sub(center);
        this.angularVelocity = angularVelocity;
    }
    
    @Override
    public boolean update(float deltaTime) {
    	posFromCenter.rotate(deltaTime * angularVelocity);
    	sourceTemp.set(center).add(posFromCenter);
    	dirTemp.set(posFromCenter).nor();
    	set(sourceTemp, dirTemp);
        return super.update(deltaTime);
    }
}
