package tungus.games.elude;

import android.content.Intent;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class AndroidLauncher extends AndroidApplication {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useGL20 = false;
        cfg.maxSimultaneousSounds = 150;
        this.createWakeLock(cfg);
        BluetoothConnection.app = this;
        initialize(new Elude(), cfg);
    }
    
    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
    	BluetoothConnection.INSTANCE.processActivityResult(requestCode, resultCode);
    }
}