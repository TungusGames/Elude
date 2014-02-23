package tungus.games.dodge;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class Assets {
	
	private static final String TEXTURE_LOCATION = "Textures/";
	
	public static Texture vessel;
	private static final String VESSEL_TEXTURE = "vessel.png";
	
	public static Texture rocket;
	private static final String ROCKET_TEXTURE = "rocket.png";
	
	
	public static void load() {
		vessel = new Texture(Gdx.files.internal(TEXTURE_LOCATION + VESSEL_TEXTURE));
		rocket = new Texture(Gdx.files.internal(TEXTURE_LOCATION + ROCKET_TEXTURE));
	}

}
