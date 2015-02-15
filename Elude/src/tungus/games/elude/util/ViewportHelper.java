package tungus.games.elude.util;

import tungus.games.elude.game.server.World;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class ViewportHelper {
	
	public static void setWorldSizeFromArea() {
		float screenRatio = (float)Gdx.graphics.getWidth() / Gdx.graphics.getHeight();
		World.HEIGHT = (float)Math.sqrt(World.AREA / screenRatio);
		World.WIDTH = World.HEIGHT * screenRatio;
		World.calcBounds();
	}
	
	public static OrthographicCamera newCamera(float width, float height) {
		float viewportRatio = width/height;
		int screenW = Gdx.graphics.getWidth();
		int screenH = Gdx.graphics.getHeight();
		float screenRatio = (float)screenW / screenH;
		OrthographicCamera cam = null;
		if (screenRatio > viewportRatio) {
			float viewportWidth = screenH * viewportRatio;
			float scale = height / screenH;
			int diff = (screenW - (int)viewportWidth) / 2;			
			cam = new OrthographicCamera(width + 2 * diff * scale, height);
		} else {
			float viewportHeight = screenW / viewportRatio;
			float scale = width / screenW;
			int diff = (screenH - (int)viewportHeight) / 2;
			cam = new OrthographicCamera(width, height + 2 * diff * scale);
		}
		cam.position.set(width/2, height/2, 0);
		cam.update();
		return cam;
	}
	
}
