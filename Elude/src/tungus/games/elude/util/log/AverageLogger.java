package tungus.games.elude.util.log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.TimeUtils;

public class AverageLogger {
	private long startTime;
	private final String tag;
	private final String msg;
	private int count;
	private float sum;
	
	public AverageLogger(String tag, String msg) {
		startTime = TimeUtils.millis();
		this.tag = tag;
		this.msg = msg;
	}
	
	public void log(float f) {
		count++;
		sum += f;
		long time = TimeUtils.millis();
		if (time - 1000 > startTime) {
			Gdx.app.log(tag, msg+(sum/count));
			startTime = time;
			sum = count = 0;
		}
	}
}
