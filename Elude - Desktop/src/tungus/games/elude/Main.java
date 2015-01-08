package tungus.games.elude;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Elude";
		cfg.width = 800;
		cfg.height = 480;
		cfg.audioDeviceSimultaneousSources = 150;
		//cfg.addIcon("icons/small.png", FileType.Internal);
		//cfg.addIcon("icons/medium.png", FileType.Internal);
		//cfg.addIcon("icons/large.png", FileType.Internal);
		
		
		Settings settings = new Settings();
        settings.maxWidth = 1024;
        settings.maxHeight = 1024;
        settings.pot = true;
        settings.filterMag = TextureFilter.Linear;
        settings.filterMin = TextureFilter.Linear;
        TexturePacker.process(settings, "../img/done", "../Elude - Android/assets/textures", "game");
        
        Elude.mpScreen = NetMPScreen.class;
		
		new LwjglApplication(new Elude(), cfg);
	}
}
