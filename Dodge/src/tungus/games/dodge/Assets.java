package tungus.games.dodge;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Assets {
	
	private static TextureAtlas atlas;
	
	private static final String TEXTURE_LOCATION = "textures/";
	
	public static TextureRegion vessel;
	private static final String VESSEL_TEXTURE = "vessel";
	
	public static TextureRegion rocket;
	private static final String ROCKET_TEXTURE = "rocket";
	
	public static TextureRegion standingEnemy;
	private static final String STANDING_TEXTURE = "StandingEnemy";
	
	
	public static void load() {
		atlas = new TextureAtlas(Gdx.files.internal(TEXTURE_LOCATION + "game.atlas"));
		
		vessel = atlas.findRegion(VESSEL_TEXTURE);
		rocket = atlas.findRegion(ROCKET_TEXTURE);
		standingEnemy = atlas.findRegion(STANDING_TEXTURE);
	}

}
