package tungus.games.elude;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Assets {
	
	public static TextureAtlas atlas;
		
	//Title screen
	public static TextureRegion eludeTitleOn;
	public static TextureRegion eludeTitleOff;
	
	//Gameplay
	public static TextureRegion vessel;	
	public static TextureRegion rocket;
	public static TextureRegion standingEnemyGreen;
	public static TextureRegion standingEnemyRed;
	public static TextureRegion movingEnemyBlue;
	public static TextureRegion movingEnemyGreen;
	public static TextureRegion kamikaze;
	public static TextureRegion hpBonus;
	public static TextureRegion speedBonus;
	public static TextureRegion virtualDPadPerimeter;
	public static TextureRegion whiteRectangle;
	public static TextureRegion smallCircle;
	public static TextureRegion shield;
	
	//Ingame menus
	public static TextureRegion pause;
	public static TextureRegion resume;
	public static TextureRegion restart;
	public static TextureRegion toMenu;
	public static TextureRegion nextLevel;
	public static TextureRegion shadower;
	
	//Level select screen
	public static TextureRegion frame;
	public static TextureRegion play;
	public static TextureRegion[] stars = new TextureRegion[4]; // 0: empty, 1: bronze, 2: silver, 3: gold
	public static TextureRegion[] smallStars = new TextureRegion[4];
	public static TextureRegion lock;
	
	public static BitmapFont font;
	
	public static final String PARTICLE_LOCATION = "particles/";	
	public static ParticleEffectPool flameRocket;
	public static ParticleEffectPool fastFlameRocket;
	public static ParticleEffectPool matrixRocket;
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
		rocket = atlas.findRegion("rocket");
		standingEnemyGreen = atlas.findRegion("StandingEnemy");
		standingEnemyRed = atlas.findRegion("StandingEnemyRed");
		movingEnemyBlue = atlas.findRegion("MovingEnemy");
		movingEnemyGreen = atlas.findRegion("MovingEnemyGreen");
		kamikaze = atlas.findRegion("kamikaze");
		hpBonus = atlas.findRegion("hpbonus");
		speedBonus = atlas.findRegion("speedbonus");
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
		
		ParticleEffect particle = new ParticleEffect();
		particle.load(Gdx.files.internal(Assets.PARTICLE_LOCATION + "flamerocket"), Assets.atlas);
		flameRocket = new ParticleEffectPool(particle, 10, 100);
		particle = new ParticleEffect();
		particle.load(Gdx.files.internal(Assets.PARTICLE_LOCATION + "matrixrocket"), Assets.atlas);
		matrixRocket = new ParticleEffectPool(particle, 10, 100);
		particle = new ParticleEffect();
		particle.load(Gdx.files.internal(Assets.PARTICLE_LOCATION + "fastflamerocket"), Assets.atlas);
		fastFlameRocket = new ParticleEffectPool(particle, 10, 100);
		particle = new ParticleEffect();
		particle.load(Gdx.files.internal(Assets.PARTICLE_LOCATION + "explosion"), Assets.atlas);
		explosion = new ParticleEffectPool(particle, 10, 100);
		particle = new ParticleEffect();
		particle.load(Gdx.files.internal(Assets.PARTICLE_LOCATION + "debris2"), Assets.atlas);
		debris = new ParticleEffectPool(particle, 10, 100);
		particle = new ParticleEffect();
		particle.load(Gdx.files.internal(Assets.PARTICLE_LOCATION + "vesseltrails"), Assets.atlas);
		vesselTrails = new ParticleEffectPool(particle, 10, 100);
		
		neonSound = Gdx.audio.newSound(Gdx.files.internal("sounds/neonSound.wav"));
		neonFlicker = Gdx.audio.newSound(Gdx.files.internal("sounds/flicker.wav"));
		explosionSound = Gdx.audio.newSound(Gdx.files.internal("sounds/explosion2.wav"));
	}
	
	public static FileHandle levelFile(int levelNum) {
		return Gdx.files.internal("levels/" + levelNum + ".lvl");
	}

}
