package tungus.games.dodge;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Assets {
	
	public static TextureAtlas atlas;
	
	public static final String TEXTURE_LOCATION = "textures/";
	public static final String PARTICLE_LOCATION = "particles/";
	
	public static TextureRegion vessel;
	private static final String VESSEL_TEXTURE = "vessel";
	
	public static TextureRegion rocket;
	private static final String ROCKET_TEXTURE = "rocket";
	
	public static TextureRegion standingEnemy;
	private static final String STANDING_TEXTURE = "StandingEnemy";
	
	public static TextureRegion virtualDPadPerimeter;
	private static String DPAD_PERIMETER_TEXTURE = "virtualdpadperimeter";
	
	public static TextureRegion whiteRectangle;
	private static String WHITE_RECT_TEXTURE = "whiterect";
	
	public static TextureRegion particle;
	private static String PARTICLE_TEXTURE = "particle";
	
	
	public static void load() {
		atlas = new TextureAtlas(Gdx.files.internal(TEXTURE_LOCATION + "game.atlas"));
		
		vessel = atlas.findRegion(VESSEL_TEXTURE);
		rocket = atlas.findRegion(ROCKET_TEXTURE);
		standingEnemy = atlas.findRegion(STANDING_TEXTURE);
		virtualDPadPerimeter = atlas.findRegion(DPAD_PERIMETER_TEXTURE);
		whiteRectangle = atlas.findRegion(WHITE_RECT_TEXTURE);
		particle = atlas.findRegion(PARTICLE_TEXTURE);
	}

}
