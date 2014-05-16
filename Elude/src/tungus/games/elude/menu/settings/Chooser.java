package tungus.games.elude.menu.settings;

import tungus.games.elude.Assets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Chooser {
	private static final float SWITCH_TIME = 0.3f;
	private static final Color ON_COLOR = Color.WHITE;
	private static final Color OFF_COLOR = Color.GRAY;
	
	private final Rectangle[] rects;
	private final String[] texts;
	public int chosen;
	private int prevChosen;
	private Color choosingColor = new Color(ON_COLOR);
	private Color prevColor = new Color(OFF_COLOR);
	private boolean switching = false;
	private float sinceSwitch = 0;
	
	public Chooser(Rectangle[] rects, String[] strings, int chosen) {
		this.rects = rects;
		this.texts = strings;
		this.chosen = chosen;
	}
	
	public boolean touch(float x, float y) {
		for (int i = 0; i < rects.length; i++) {
			if (rects[i].contains(x, y)) {
				prevChosen = chosen;
				chosen = i;
				switching = true;
				return true;
			}
		}
		return false;
	}
	
	public void render(float deltaTime, SpriteBatch batch, float alpha) {
		if (switching) {
			sinceSwitch += deltaTime;
			choosingColor.set(OFF_COLOR).lerp(ON_COLOR, sinceSwitch/SWITCH_TIME);
			prevColor.set(ON_COLOR).lerp(OFF_COLOR, sinceSwitch/SWITCH_TIME);
			if (sinceSwitch > SWITCH_TIME) {
				switching = false;
				sinceSwitch = 0;
				choosingColor.set(ON_COLOR);
				prevColor.set(OFF_COLOR);
			}
		}
		for (int i = 0; i < rects.length; i++) {
			// Get base color
			if (i == chosen) {
				Assets.font.setColor(choosingColor);
			} else if (i == prevChosen) {
				Assets.font.setColor(prevColor);
			} else {
				Assets.font.setColor(OFF_COLOR);
			}
			// Mod by alpha
			Color c = Assets.font.getColor();
			c.a *= alpha;
			Assets.font.setColor(c);
			// Draw
			Assets.font.draw(batch, texts[i], rects[i].x, rects[i].y + rects[i].height);
		}
	}
}
