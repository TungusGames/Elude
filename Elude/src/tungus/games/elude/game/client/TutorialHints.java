package tungus.games.elude.game.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

import tungus.games.elude.Assets;
import tungus.games.elude.game.server.World;
import tungus.games.elude.game.server.enemies.Enemy.EnemyType;
import tungus.games.elude.game.server.pickups.Pickup.PickupType;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
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
	private static final float MAX_FADE = 0.5f;
	
	private int state = STATE_PASSIVE;
	private float stateTime = 0;
	
	private boolean lowerHalf = true;
	
	private TextBounds tb = new TextBounds();
	
	private boolean[] seenEnemy = new boolean[EnemyType.values().length];
	private boolean[] seenPickup = new boolean[PickupType.values().length];
	private String[] enemyHint = new String[EnemyType.values().length];
	private String[] pickupHint = new String[PickupType.values().length];
	
	public EnemyType highlightEnemy = null;
	public PickupType highlightPickup = null;
	public float requestedAlpha = 1;
	
	public TutorialHints() {
		Scanner sc = new Scanner(hintTextFile.read());
		sc.useDelimiter("\n\n");
		for (int i = 0; i < enemyHint.length; i++) {
			enemyHint[i] = sc.next();
		}
		for (int i = 0; i < pickupHint.length; i++) {
			pickupHint[i] = sc.next();
		}
		sc.close();
		if (!seenDataFile.exists()) {
			save();
		} else {
			try {
				ObjectInputStream in = new ObjectInputStream(seenDataFile.read());
				seenEnemy = (boolean[])(in.readObject());
				seenPickup = (boolean[])(in.readObject());
			} catch (Exception e) {
				Gdx.app.log("Tutorial hints", "Couldn't load file");
				e.printStackTrace();
			}			
		}
	}


	public void onEnemyArrived(Vector2 pos, EnemyType e) {
		state = STATE_FADEIN;
		stateTime = 0;
		highlightEnemy = e;
		lowerHalf = (pos.y < World.HEIGHT / 2);
	}
	
	public void update(float deltaTime) {
		stateTime += deltaTime;
		if (state == STATE_FADEIN) {
			if (stateTime > FADE_TIME) {
				state = STATE_ACTIVE;
				requestedAlpha = MAX_FADE;
			} else {
				requestedAlpha = 1 - (stateTime / FADE_TIME) * (1 - MAX_FADE);
			}
		}
		if (state == STATE_FADEOUT) {
			if (stateTime > FADE_TIME) {
				state = STATE_PASSIVE;
				requestedAlpha = 1;
				highlightEnemy = null;
				highlightPickup = null;
			} else {
				requestedAlpha = (stateTime / FADE_TIME) * (1 - MAX_FADE) + MAX_FADE;
			}
		}
	}
	
	public void drawText(SpriteBatch fontBatch) {
		if (state == STATE_PASSIVE)
			return;
		String text = (highlightEnemy == null) ? pickupHint[highlightPickup.ordinal()] :
												 enemyHint [highlightEnemy.ordinal()];
		tb = Assets.font.getMultiLineBounds(text);
		float x = World.WIDTH - tb.width / 2;
		float y = World.HEIGHT * (lowerHalf ? 0.25f : 0.75f) + tb.height / 2;
		float alpha = (state == STATE_ACTIVE) ? 1 :
					  (state == STATE_FADEIN) ? stateTime / FADE_TIME :
						  					    1 - stateTime / FADE_TIME;
		Color prev = Assets.font.getColor();
		Assets.font.setColor(1, 1, 1, alpha);
		fontBatch.begin();
		Assets.font.drawMultiLine(fontBatch, text, x, y);
		fontBatch.end();
		Assets.font.setColor(prev);
	}
	
	public void save() {
		try {
			ObjectOutputStream out = new ObjectOutputStream(seenDataFile.write(false));
			out.writeObject(seenEnemy);
			out.writeObject(seenPickup);
			out.close();
		} catch (IOException e) {
			Gdx.app.log("Tutorial hints", "Failed to save file");
			e.printStackTrace();
		}
		
	}
}
