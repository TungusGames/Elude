package tungus.games.elude.game.client.input;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public interface Controls {
	public Vector2 getDir(Vector2 vessel);
	public void draw(SpriteBatch batch, float alpha);
}
