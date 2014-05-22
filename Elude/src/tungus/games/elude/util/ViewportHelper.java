package tungus.games.elude.util;

import tungus.games.elude.game.server.World;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

public class ViewportHelper {
	
	private static Vector3 viewportTopleft = new Vector3(0, 0, 0);	// Vector3 - easily chainable with Camera.unproject()
	private static float viewportWidth = 800;
	private static float viewportHeight = 480;
	
	public static void maximizeForRatio(float viewportRatio) {
		int screenW = Gdx.graphics.getWidth();
		int screenH = Gdx.graphics.getHeight();
		float screenRatio = (float)screenW / screenH;
		if (screenRatio > viewportRatio) {
			viewportWidth = screenH * viewportRatio;
			viewportHeight = screenH;
			int diff = (screenW - (int)viewportWidth) / 2;			
			Gdx.gl10.glViewport(diff, 0, (int)viewportWidth, screenH);
			viewportTopleft.set(diff, 0, 0);
		} else {
			viewportHeight = screenW / viewportRatio;
			viewportWidth = screenW;
			int diff = (screenH - (int)viewportHeight) / 2;
			Gdx.gl10.glViewport(0, diff, screenW, (int)viewportHeight);
			viewportTopleft.set(0, diff, 0);
			
		}
		Gdx.app.log("DEBUG", "VP top left: " + viewportTopleft.toString());
	}
	
	public static void setFullScreen() {
		
		Gdx.gl10.glViewport(0, 0, (int)(viewportWidth = Gdx.graphics.getWidth()), (int)(viewportHeight = Gdx.graphics.getHeight()));
		viewportTopleft.set(0, 0, 0);
	}
	
	public static void setWorldSizeFromArea() {
		float screenRatio = (float)Gdx.graphics.getWidth() / Gdx.graphics.getHeight();
		World.HEIGHT = (float)Math.sqrt(World.AREA / screenRatio);
		Gdx.app.log("DEBUG", "height:"+World.HEIGHT);
		World.WIDTH = World.HEIGHT * screenRatio;
		Gdx.app.log("WIDTH", "width:"+World.WIDTH);
		World.calcBounds();
	}
	
	public static Vector3 unproject(Vector3 t, OrthographicCamera c) {
		c.unproject(t, viewportTopleft.x, viewportTopleft.y, viewportWidth, viewportHeight);
		return t;
	}
	
}
