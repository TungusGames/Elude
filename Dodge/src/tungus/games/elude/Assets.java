package tungus.games.elude;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Assets {
	
	public static TextureAtlas atlas;
		
	public static TextureRegion vessel;	
	public static TextureRegion rocket;
	public static TextureRegion standingEnemy;
	public static TextureRegion movingEnemy;
	public static TextureRegion kamikaze;
	public static TextureRegion hpBonus;
	public static TextureRegion speedBonus;
	public static TextureRegion virtualDPadPerimeter;
	public static TextureRegion whiteRectangle;
	public static TextureRegion smallCircle;
	
	public static final String PARTICLE_LOCATION = "particles/";	
	public static ParticleEffectPool flameRocket;
	public static ParticleEffectPool fastFlameRocket;
	public static ParticleEffectPool matrixRocket;
	public static ParticleEffectPool testRocket;
	public static ParticleEffectPool explosion;
	public static ParticleEffectPool debris;
	
	public static void load() {
		atlas = new TextureAtlas(Gdx.files.internal("textures/game.atlas"));
		
		vessel = atlas.findRegion("vessel");
		rocket = atlas.findRegion("rocket");
		standingEnemy = atlas.findRegion("StandingEnemy");
		movingEnemy = atlas.findRegion("MovingEnemy");
		kamikaze = atlas.findRegion("kamikaze");
		hpBonus = atlas.findRegion("hpbonus");
		speedBonus = atlas.findRegion("speedbonus");
		virtualDPadPerimeter = atlas.findRegion("virtualdpadperimeter");
		whiteRectangle = atlas.findRegion("whiterect");
		smallCircle = atlas.findRegion("smallcircle");
		
		
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
	
	public static FileHandle levelFile(int levelNum) {
		return Gdx.files.internal("levels/" + levelNum + ".lvl");
	}

}
