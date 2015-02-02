package tungus.games.elude.game.server.enemies.boss;

import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.enemies.Enemy;
import tungus.games.elude.game.server.laser.RotatingLaser;
import tungus.games.elude.game.server.rockets.Rocket.RocketType;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

public class FactoryBoss extends Enemy {
    
    private static final float RADIUS = 2f;
    
    private static final int STATE_ENTER = 0;
    private static final int STATE_IN = 1;
    private static final float MOVEMENT_PERIOD = 25;
    public static final float TIME_PER_BEHAVIOR = MOVEMENT_PERIOD / 2;
    private static final float ANGULAR_FREQ = 2*(float)Math.PI / MOVEMENT_PERIOD; // Körfrekvencia
    private static final float AMPLITUDE = 7;
    
    private static final float LASER_START_SPEED = 20; // Degrees per sec
    private static final float LASER_END_SPEED = 90;
    
    /**
     * A matrix of behaviors. Each row corresponds to what the boss does at a section of its lifetime.
     * For example, if there are 3 vectors in the matrix, the first one is active while its life is above
     * 2/3 of the maximum, the second between 1/3 and 2/3, the last one below 1/3.
     * 
     * Each behavior lasts for one "half wave" as the boss goes from one side of the map to the other.
     */
    public static FactoryBossBehavior BEHAVIOR[][] = null;
    
    private int state = STATE_ENTER;
    private float timeSinceArrival = 0;
    
    private float timeOnBehavior = 0;
    private FactoryBossBehavior[] currentLoop;
    private int behaviorIndex;
    
    private RotatingLaser laser;
    
    
    public FactoryBoss(World w) {
        super(new Vector2(-5, World.HEIGHT / 2),
              EnemyType.BOSS_FACTORY,
              2 * RADIUS,
              w,
              RocketType.FAST_TURNING);
        vel.set(AMPLITUDE * ANGULAR_FREQ, 0);
        super.turnSpeed = 0;
        super.solid = true;
        currentLoop = BEHAVIOR[0];
        // 	Start by switching to index 0, so the behavior's start() is called at the right time
        behaviorIndex = -1;	
        timeOnBehavior = TIME_PER_BEHAVIOR;
        
    }
    
    @Override
    protected boolean aiUpdate(float deltaTime) {
        if (state == STATE_ENTER && pos.dst2(World.WIDTH/2, World.HEIGHT/2) < vel.len2()*deltaTime*deltaTime) {
            state = STATE_IN;
            vel.set(0, 0);
            timeSinceArrival = 0;
            laser = new RotatingLaser(world, pos, new Vector2(1, 0), RADIUS, LASER_END_SPEED - (LASER_END_SPEED - LASER_START_SPEED) * (hp / maxHp));
            world.addNextFrame.add(laser);
        } else if (state == STATE_IN) {
            timeSinceArrival += deltaTime;
            pos.set(World.WIDTH/2, World.HEIGHT/2).add((float)Math.sin(timeSinceArrival * ANGULAR_FREQ) * AMPLITUDE, 0);
            if (timeSinceArrival < TIME_PER_BEHAVIOR / 2) {
            	// Start the behavior list coming from the end of the map, not before that
            	return false;
            }
            timeOnBehavior += deltaTime;
            if (timeOnBehavior >= TIME_PER_BEHAVIOR) {
            	timeOnBehavior -= TIME_PER_BEHAVIOR;
            	behaviorIndex++;
            	if (behaviorIndex == currentLoop.length) {
            		behaviorIndex = 0;
            	}
            	currentLoop[behaviorIndex].startPeriod(world, this);
            }
            currentLoop[behaviorIndex].update(world, this, deltaTime);
        }
        return false;
    }
    
    @Override
    protected void takeDamage(float dmg) {
    	super.takeDamage(dmg);
    	if (laser != null) {
    		// Interpolate angular velocity from START to END as hp goes from maxHp to 0
    		laser.angularVelocity = LASER_END_SPEED - (LASER_END_SPEED - LASER_START_SPEED) * (hp / maxHp);
    	}
    	// Interpolate index from 0 to length-1 as hp goes from maxHp to 0.
    	currentLoop = BEHAVIOR[Math.min((int)(BEHAVIOR.length * (1 - hp/maxHp)), BEHAVIOR.length - 1)];
    }
    
    @Override
	public void killBy(Circle hitter) {
    	super.killBy(hitter);
    	laser.stop();
    }
    
}
