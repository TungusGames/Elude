package tungus.games.elude.util.log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.TimeUtils;

public class FPSLogger {
	private long startTime;
	private final String tag;
	private final String msg;
	private int frames;
	
	public FPSLogger(String tag, String msg) {
		startTime = TimeUtils.millis();
		this.tag = tag;
		this.msg = msg;
	}
	
	public void log() {
		frames++;
		long time = TimeUtils.millis();
		if (time - 1000 > startTime) {
			Gdx.app.log(tag, msg+frames);
			startTime = time;
			frames = 0;
		}
	}
}
