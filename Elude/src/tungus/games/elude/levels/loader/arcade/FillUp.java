package tungus.games.elude.levels.loader.arcade;

import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.enemies.Enemy;
import tungus.games.elude.game.server.enemies.Enemy.EnemyType;

import com.badlogic.gdx.math.MathUtils;

public class FillUp extends ArcadeLoaderBase {
	private final int fillTo;
	private final float incrementTime;
	protected final EnemyType[] types;
	
	private float timeSinceIncrement = 0;
	private int currentFill = 1;
	
	public FillUp(World w, int levelNum, int fillTo, float timeToMax, EnemyType... types) {
		this(w, levelNum, fillTo, timeToMax, 0, 0, 0, 0, types);
	}
	
	public FillUp(World w, int levelNum, int fillTo, float timeToMax, float a, float b, float c, float d, EnemyType... types) {
		super(w, a, b, c, d, levelNum);
		this.fillTo = fillTo;
		this.incrementTime = timeToMax / (fillTo-1);
		this.types = types;
		addEnemy();
	}
	
	@Override
	public boolean update(float deltaTime) {
		super.update(deltaTime);
		timeSinceIncrement += deltaTime;
		if (timeSinceIncrement > incrementTime && currentFill < fillTo) {
			currentFill++;
			timeSinceIncrement = 0;
		}
		
		if (world.enemyCount < currentFill) {
			addEnemy();
		}
		return false;
	}
	
	protected void addEnemy() {
		world.addEnemy(Enemy.fromType(world, types[MathUtils.random(types.length-1)]));
	}
}
