package tungus.games.dodge;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Assets {
	
	public static TextureAtlas atlas;
	
	private static final String TEXTURE_LOCATION = "textures/";
	
	public static TextureRegion vessel;
	private static final String VESSEL_TEXTURE = "vessel";
	
	public static TextureRegion rocket;
	private static final String ROCKET_TEXTURE = "rocket";
	
	public static TextureRegion standingEnemy;
	private static final String STANDING_TEXTURE = "StandingEnemy";
	
	public static TextureRegion movingEnemy;
	private static final String MOVING_TEXTURE = "MovingEnemy";
	
	public static TextureRegion virtualDPadPerimeter;
	private static String DPAD_PERIMETER_TEXTURE = "virtualdpadperimeter";
	
	public static TextureRegion whiteRectangle;
	private static String WHITE_RECT_TEXTURE = "whiterect";
	
	public static TextureRegion smallCircle;
	private static String SMALL_CIRCLE_TEXTURE = "smallcircle";
	
	public static final String PARTICLE_LOCATION = "particles/";
	
	public static ParticleEffectPool flameRocket;
	public static ParticleEffectPool fastFlameRocket;
	public static ParticleEffectPool matrixRocket;
	public static ParticleEffectPool testRocket;
	public static ParticleEffectPool explosion;
	public static ParticleEffectPool debris;
	
	
	public static void load() {
		atlas = new TextureAtlas(Gdx.files.internal(TEXTURE_LOCATION + "game.atlas"));
		
		vessel = atlas.findRegion(VESSEL_TEXTURE);
		rocket = atlas.findRegion(ROCKET_TEXTURE);
		standingEnemy = atlas.findRegion(STANDING_TEXTURE);
		movingEnemy = atlas.findRegion(MOVING_TEXTURE);
		virtualDPadPerimeter = atlas.findRegion(DPAD_PERIMETER_TEXTURE);
		whiteRectangle = atlas.findRegion(WHITE_RECT_TEXTURE);
		smallCircle = atlas.findRegion(SMALL_CIRCLE_TEXTURE);
		
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
	}

}
