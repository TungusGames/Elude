package tungus.games.elude.util.log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.TimeUtils;
/**
 * Logs the average of the values given to since the last log it every second, 
 * with the tag and message strings given to its constructor
 */
public class AverageLogger {
	
	private long startTime;
	private final String tag;
	private final String msg;
	private int count = 0;
	private float sum = 0;
	
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
