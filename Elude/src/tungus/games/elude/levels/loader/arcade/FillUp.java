package tungus.games.elude.levels.loader.arcade;

import com.badlogic.gdx.math.MathUtils;

import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.enemies.Enemy;
import tungus.games.elude.game.server.enemies.Enemy.EnemyType;

public class FillUp extends ArcadeLoaderBase {
	private final int fillTo;
	private final float cooldown;
	private final EnemyType[] types;
	
	private float timeSinceLast = 0;
	
	public FillUp(World w, int levelNum, int fillTo, float cooldown, EnemyType... types) {
		this(w, levelNum, fillTo, cooldown, 0, 0, 0, 0, types);
	}
	
	public FillUp(World w, int levelNum, int fillTo, float cooldown, float a, float b, float c, float d, EnemyType... types) {
		super(w, a, b, c, d, levelNum);
		this.fillTo = fillTo;
		this.cooldown = cooldown;
		this.types = types;
		addEnemy();
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		timeSinceLast += deltaTime;
		if (world.enemies.size() < fillTo && timeSinceLast > cooldown) {
			addEnemy();
			timeSinceLast = 0;
		}
	}
	
	private void addEnemy() {
		world.enemies.add(Enemy.fromType(world, types[MathUtils.random(types.length-1)]));
	}
}
