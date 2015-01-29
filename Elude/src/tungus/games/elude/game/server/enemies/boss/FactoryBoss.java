package tungus.games.elude.game.server.enemies.boss;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.enemies.Enemy;
import tungus.games.elude.game.server.laser.RotatingLaser;
import tungus.games.elude.game.server.rockets.Rocket.RocketType;

public class FactoryBoss extends Enemy {
    
    private static final float RADIUS = 1f;
    
    private static final int STATE_ENTER = 0;
    private static final int STATE_IN = 1;
    
    private int state = STATE_ENTER;
    private float time = 0;
    
    private RotatingLaser laser;
    
    public FactoryBoss(World w) {
        super(new Vector2(-5, World.HEIGHT / 2),
              EnemyType.BOSS_FACTORY,
              RADIUS,
              w,
              RocketType.FAST_TURNING);
        vel.set(3, 0);
    }
    
    @Override
    protected boolean aiUpdate(float deltaTime) {
        if (state == STATE_ENTER && pos.dst2(World.WIDTH/2, World.HEIGHT/2) < vel.len2()) {
            state = STATE_IN;
            vel.set(0, 0);
            time = 0;
            laser = new RotatingLaser(world, pos, new Vector2(1, 0), RADIUS, 90);
            world.addNextFrame.add(laser);
        } else if (state == STATE_IN) {
            time += deltaTime;
            pos.set(World.WIDTH/2, World.HEIGHT/2);
            laser.setCenter(pos);
            //Gdx.app.log("boss", pos.x + ";" + pos.y);
        }
        return false;
    }
    
}
