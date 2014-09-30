package tungus.games.elude.dev;

import java.util.List;
import java.util.Locale;

import tungus.games.elude.Assets;
import tungus.games.elude.util.ViewportHelper;
import android.bluetooth.BluetoothDevice;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class BTListUI {
	
	public static Locale loc;
	private static final Vector2 start = new Vector2(300, 380);
	private static final float offset = 60;
	
	private /*static*/ final Vector3 t = new Vector3();
	
	private SpriteBatch batch;
	private OrthographicCamera cam;
	
	private float[] stringLen = new float[8];
	private int deviceNum = 0;
	
	public BTListUI() {
		cam = ViewportHelper.newCamera(800, 480);
		batch = new SpriteBatch(300);
		batch.setProjectionMatrix(cam.combined);
		Assets.font.setScale(1.6f);
	}
	
	public void renderMessage(String msg) {
		TextBounds t = Assets.font.getBounds(msg);
		batch.begin();
		Assets.font.draw(batch, msg, cam.viewportWidth/2 - t.width/2, cam.viewportHeight/2 - t.height/2);
		batch.end();
	}
	
	public void renderList(List<BluetoothDevice> devices) {
		batch.begin();
		Assets.font.setColor(1,1,1,1);
		Assets.font.draw(batch, "TEST", 0, 50);
		float y = start.y;
		deviceNum = devices.size();
		int i = 0;
		for (BluetoothDevice d : devices) {			
			Assets.font.draw(batch, d.getName().toUpperCase(loc), start.x, y);
			y -= offset;
			if (i < 8) {
				stringLen[i] = Assets.font.getBounds(d.getName()).width;
			}
			i++;
		}
		Assets.font.draw(batch, "N+1", start.x, y);
		batch.end();
	}
	
	public int tapSelection (float x, float y) {
		t.set(x, y, 0);
		cam.unproject(t);
		for (int i = 0; i < deviceNum; i++) {
			if (t.y < start.y - i*offset && t.y > start.y - (i+1) * offset) {
				if (t.x > start.x && t.x < start.x + stringLen[i]) {
					Gdx.app.log("BT tap", "Returning " + i);
					return i;
				} else {
					return -1;
				}
			}
		}
		return -1;
	}
}
