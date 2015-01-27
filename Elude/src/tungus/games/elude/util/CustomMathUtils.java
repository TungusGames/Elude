package tungus.games.elude.util;

import com.badlogic.gdx.math.Vector2;

public class CustomMathUtils {
	
	public static Vector2 lineCircleIntersectionPoint(Vector2 p1, Vector2 p2, float cx, float cy, float r, Vector2 out) {
		float m = (p2.y - p1.y) / (p2.x - p1.x);
		float c = p1.y - m * p1.x;
		
		float A = m*m + 1;
		float B = 2 * (m*c - m*cy - cx);
		float C = cy * cy + cx * cx - r * r - 2*c*cy + c*c;

		float D = (float)Math.sqrt(B*B - 4*A*C);
		float x1 = (-B + D) / (2 * A);
		float x2 = (-B - D) / (2 * A);
		
		if (Math.abs(x1 - p1.x) < Math.abs(x2 - p1.x) && (x1-p1.x) * (p2.x-p1.x) > 0) {
			out.x = x1;
		} else {
			out.x = x2;
		}
		
		out.y = m * out.x + c;
		return out;
	}
}
