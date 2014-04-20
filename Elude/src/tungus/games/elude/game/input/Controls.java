package tungus.games.elude.game.input;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public interface Controls {
	public Vector2 getDir();
	public void draw(SpriteBatch batch, float alpha);
}
