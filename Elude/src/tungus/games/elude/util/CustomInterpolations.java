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
	
	public static final Interpolation FLOAT_THROUGH = new Interpolation() {
		private final Interpolation interp = Interpolation.exp10;
		@Override
		public float apply(float a) {
			if (a < 0.5f) {
				return interp.apply(2*a)/2;
			} else {
				return 0.5f + interp.apply(2*(a-0.5f))/2;
			}
		}
	};
	public static class FadeInOut extends Interpolation {
		/** Fades in and out linearly in the same time
		 made for freezing
		 intelligently detects resetting the fadeTime value.
		 Actually, it works backwards only */
		private float fadeInEndTime;
		private float fadeOutStartTime;
		
		public FadeInOut(float fadeTime, float totalTime) {
			fadeInEndTime = fadeTime;
			fadeOutStartTime = totalTime - fadeTime;
		}
		
		private static enum State {IN, OUT, FADING};
		private State lastState = State.OUT;
		
		@Override
		public float apply(float a) {
			if (a <= 0f) {
				lastState = State.OUT;
				return 0;
			}
			else if (a < fadeInEndTime)
				return a / fadeInEndTime;
			else if (a <= fadeOutStartTime) {
				lastState = State.IN;
				return 1;
			}
			else if (a < (fadeInEndTime + fadeOutStartTime)) {
				if (lastState == State.IN)
					return 1;
				else
					lastState = State.FADING;
					return (fadeInEndTime - (a - fadeOutStartTime)) / fadeInEndTime;
			}
			else switch (lastState) {
			case OUT: return 0;
			case IN: return 1;
			case FADING:
				lastState = State.IN;
				return 1;
			default: return 1;
			}
		}
	}
	
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
