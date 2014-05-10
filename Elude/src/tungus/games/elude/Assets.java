package tungus.games.elude;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class Assets {
	
	public static TextureAtlas atlas;
		
	//Title screen
	public static TextureRegion eludeTitleOn;
	public static TextureRegion eludeTitleOff;
	
	//Menus
	public static NinePatch frame9p;
	
	//Gameplay
	public static TextureRegion vessel;
	public static TextureRegion vesselRed;
	public static TextureRegion shield;
	public static TextureRegion rocket;
	
	public static TextureRegion standingEnemyGreen;
	public static TextureRegion standingEnemyRed;
	public static TextureRegion movingEnemyBlue;
	public static TextureRegion movingEnemyGreen;
	public static TextureRegion kamikaze;
	public static TextureRegion sharpshooter;
	
	public static TextureRegion hpBonus;
	public static TextureRegion speedBonus;
	public static TextureRegion shieldBonus;
	
	public static TextureRegion whiteRectangle;
	public static TextureRegion smallCircle;
	
	public static TextureRegion virtualDPadPerimeter;
	
	//Ingame menus
	public static TextureRegion pause;
	public static TextureRegion resume;
	public static TextureRegion restart;
	public static TextureRegion toMenu;
	public static TextureRegion nextLevel;
	public static TextureRegion shadower;
	
	//Level select screen
	public static TextureRegion frame;
	public static TextureRegion frameRed;
	public static TextureRegion frameBlue;
	public static TextureRegion frameGreen;
	public static TextureRegion frameYellow;
	public static TextureRegion play;
	public static TextureRegion[] stars = new TextureRegion[4]; // 0: empty, 1: bronze, 2: silver, 3: gold
	public static TextureRegion[] smallStars = new TextureRegion[4];
	public static TextureRegion lock;
	
	public static BitmapFont font;
	
	public static final String PARTICLE_LOCATION = "particles/";	
	public static ParticleEffectPool flameRocket;
	public static ParticleEffectPool fastFlameRocket;
	public static ParticleEffectPool matrixRocket;
	public static ParticleEffectPool straightRocket;
	public static ParticleEffectPool testRocket;
	public static ParticleEffectPool explosion;
	public static ParticleEffectPool debris;
	public static ParticleEffectPool vesselTrails;
	
	public static Sound neonFlicker;
	public static Sound neonSound;
	public static Sound explosionSound;
	
	public static void load() {
		atlas = new TextureAtlas(Gdx.files.internal("textures/game.atlas"));
		
		eludeTitleOn = atlas.findRegion("EludeOn");
		eludeTitleOff = atlas.findRegion("EludeOff");
		vessel = atlas.findRegion("vessel");
		vesselRed = atlas.findRegion("vesselred");
		rocket = atlas.findRegion("rocket");
		standingEnemyGreen = atlas.findRegion("StandingEnemy");
		standingEnemyRed = atlas.findRegion("StandingEnemyRed");
		movingEnemyBlue = atlas.findRegion("MovingEnemy");
		movingEnemyGreen = atlas.findRegion("MovingEnemyGreen");
		kamikaze = atlas.findRegion("kamikaze");
		sharpshooter = atlas.findRegion("sharpshooter");
		hpBonus = atlas.findRegion("hpbonus");
		speedBonus = atlas.findRegion("speedbonus");
		shieldBonus = atlas.findRegion("shieldbonus");
		virtualDPadPerimeter = atlas.findRegion("virtualdpadperimeter");
		whiteRectangle = atlas.findRegion("whiterect");
		smallCircle = atlas.findRegion("smallcircle");
		shield = atlas.findRegion("shield");
		
		pause = atlas.findRegion("pause");
		resume = atlas.findRegion("ingamemenu/resume");
		toMenu = atlas.findRegion("ingamemenu/tomenu");
		restart = atlas.findRegion("ingamemenu/restart");
		shadower = atlas.findRegion("ingamemenu/shadower");
		nextLevel = atlas.findRegion("ingamemenu/nextlevel");
		
		frame = atlas.findRegion("frame");
		frameRed = atlas.findRegion("frame-red");
		frameBlue = atlas.findRegion("frame-blue");
		frameGreen = atlas.findRegion("frame-green");
		frameYellow = atlas.findRegion("frame-yellow");
		play = atlas.findRegion("play");
		stars[0] = atlas.findRegion("starempty");
		stars[1] = atlas.findRegion("starbronze");
		stars[2] = atlas.findRegion("starsilver");
		stars[3] = atlas.findRegion("stargold");
		smallStars[0] = atlas.findRegion("starsmallempty");
		smallStars[1] = atlas.findRegion("starsmallbronze");
		smallStars[2] = atlas.findRegion("starsmallsilver");
		smallStars[3] = atlas.findRegion("starsmallgold");
		lock = atlas.findRegion("lock");
		
		font = new BitmapFont(Gdx.files.internal("font/bulletproof.fnt"));
		
		flameRocket = loadParticle("flamerocket", 40, 80);
		matrixRocket = loadParticle("matrixrocket");
		fastFlameRocket = loadParticle("fastflamerocket");
		straightRocket = loadParticle("straightrocket");
		explosion = loadParticle("explosion", 20, 40);
		debris = loadParticle("debris2", 40, 80);
		vesselTrails = loadParticle("vesseltrails", 2, 2);
		
		neonSound = Gdx.audio.newSound(Gdx.files.internal("sounds/neonSound.wav"));
		neonFlicker = Gdx.audio.newSound(Gdx.files.internal("sounds/flicker.wav"));
		explosionSound = Gdx.audio.newSound(Gdx.files.internal("sounds/explosion2.wav"));
		
		frame9p = new NinePatch(frame, 15, 84, 15, 84);
	}
	
	private static ParticleEffectPool loadParticle(String filename) {
		return loadParticle(filename, 10, 50);
	}
	
	private static ParticleEffectPool loadParticle(String filename, int s, int m) {
		ParticleEffect particle = new ParticleEffect();
		particle.load(Gdx.files.internal(Assets.PARTICLE_LOCATION + filename), Assets.atlas);
		return new ParticleEffectPool(particle, s, m);
	}
	
	public static FileHandle levelFile(int levelNum) {
		return Gdx.files.internal("levels/" + levelNum + ".lvl");
	}
	
	public static PooledEffect debris(float[] color, float dir) {
		PooledEffect p = debris.obtain();
		Array<ParticleEmitter> emitters = p.getEmitters();
		for (int i = 0; i < emitters.size; i++) {
			// Mod color
			float[] separateColor = (i == emitters.size-1) ? color : color.clone(); // Color for each emitter - last one uses up the original array
			float mul = MathUtils.random()*0.6f + 0.7f;
			for (int j = 0; j < 3; j++) {
				// Randomly change the color slightly
				mul = MathUtils.random() + 0.5f;
				color[j] = MathUtils.clamp(separateColor[j]*mul, 0f, 1f);
			}
			emitters.get(i).getTint().setColors(separateColor);
			
			// Mod angle
			if (dir == dir) { // Dir is not NaN (NaN != NaN)
				emitters.get(i).getAngle().setLow(dir);
				emitters.get(i).getAngle().setHigh(-90, 90);
			}
			else {
				emitters.get(i).getAngle().setHigh(-180, 180);
			}
		}
		return p;
	}

}
