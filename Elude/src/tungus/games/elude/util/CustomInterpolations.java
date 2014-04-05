package tungus.games.elude.util;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;

public class CustomInterpolations {
	public static final Interpolation FLASH = new Interpolation() {
		@Override
		public float apply(float a) {
			float rad = (a*5-0.5f)*MathUtils.PI;
			float sin0to1 = (MathUtils.sin(rad)+1)/2;
			if (a < 0.8f) {
				return sin0to1/2;
			} else {
				return sin0to1;
			}
		}
	};
	
	public static class FadeinFlash extends Interpolation {
		private static final Interpolation fadeIn = Interpolation.fade;
		private final float inEnd, flashStart;
		private final float inCoefficient;
		private final float flashCoefficient;
		
		public FadeinFlash(float inEnd, float flashStart) {
			this.inEnd = inEnd;
			this.flashStart = flashStart;
			inCoefficient = 1/inEnd;
			flashCoefficient = 1/(1-flashStart);
		}
		@Override
		public float apply(float a) {
			if (a < inEnd)
				return fadeIn.apply(a*inCoefficient);
			else if (a > flashStart)
				return FLASH.apply(1, 0, (a-flashStart)*flashCoefficient);
			else
				return 1;
		}
	};
}
