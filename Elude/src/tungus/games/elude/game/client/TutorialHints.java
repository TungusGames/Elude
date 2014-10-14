package tungus.games.elude.game.client;

import tungus.games.elude.game.server.enemies.Enemy.EnemyType;
import tungus.games.elude.game.server.pickups.Pickup.PickupType;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class TutorialHints {
	
	private static final FileHandle seenDataFile = Gdx.files.local("tutorial/seen.dat");
	private static final FileHandle hintTextFile = Gdx.files.internal("tutorial/hinttext.txt");
	
	private static final int STATE_FADEIN = 0;
	private static final int STATE_FADEOUT = 1;
	private static final int STATE_ACTIVE = 2;
	private static final int STATE_PASSIVE = 3;
	
	private static final float FADE_TIME = 0.7f;
	
	private int state = STATE_PASSIVE;
	private float stateTime = 0;
	
	private boolean[] seenEnemies = new boolean[EnemyType.values().length];
	private boolean[] seenPickups = new boolean[PickupType.values().length];
	private String[] enemyHint = new String[EnemyType.values().length];
	private String[] pickupHint = new String[PickupType.values().length];
	
	public EnemyType highlightEnemy = null;
	public PickupType hightlightPickup = null;
	public float requestedAlpha = 1;
	
	public TutorialHints() {
		// Load files
	}
	
	
	public void onEnemyArrived(Vector2 pos, EnemyType e) {
		
	}
	
	public void update(float deltaTime) {
		
	}
	
	public void drawText(SpriteBatch fontBatch) {
		
	}
	
	public void save() {
		
	}
}
