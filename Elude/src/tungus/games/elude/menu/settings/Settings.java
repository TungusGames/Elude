package tungus.games.elude.menu.settings;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class Settings implements Serializable{
	
	private static final long serialVersionUID = 7875217165564569881L;

	private static FileHandle file = Gdx.files.local("settings/settings.set");
	
	public static Settings INSTANCE = load();
	
	public enum MobileControlType{TAP_TO_TARGET, STATIC_DPAD, DYNAMIC_DPAD}
	
	public boolean soundOn = true;
        public boolean musicOn = true;
	public boolean vibrateOn = true;
	public MobileControlType mobileControl = MobileControlType.TAP_TO_TARGET;
	
	private static Settings load() {
		try {
			return (Settings)(new ObjectInputStream(file.read()).readObject());
		} catch (Exception e) {
			e.printStackTrace();
			return new Settings();
		}
	}
	
	public void save() {		
		try {
			ObjectOutputStream out = new ObjectOutputStream(file.write(false));
			out.writeObject(this);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			Gdx.app.log("Files", "Failed to save settings", e);
		}
		
	}
}
