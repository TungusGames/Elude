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
	
	public static final String PARTICLE_LOCATION = "particles/";
	
	public static ParticleEffectPool rocket2;
	
	
	public static void load() {
		atlas = new TextureAtlas(Gdx.files.internal(TEXTURE_LOCATION + "game.atlas"));
		
		vessel = atlas.findRegion(VESSEL_TEXTURE);
		rocket = atlas.findRegion(ROCKET_TEXTURE);
		standingEnemy = atlas.findRegion(STANDING_TEXTURE);
		movingEnemy = atlas.findRegion(MOVING_TEXTURE);
		virtualDPadPerimeter = atlas.findRegion(DPAD_PERIMETER_TEXTURE);
		whiteRectangle = atlas.findRegion(WHITE_RECT_TEXTURE);
		
		ParticleEffect particle = new ParticleEffect();
		particle.load(Gdx.files.internal(Assets.PARTICLE_LOCATION + "rocket_2"), Assets.atlas);
		rocket2 = new ParticleEffectPool(particle, 10, 100);
	}

}
