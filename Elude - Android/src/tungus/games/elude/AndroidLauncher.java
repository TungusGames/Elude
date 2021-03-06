package tungus.games.elude;

import tungus.games.elude.Elude;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class AndroidLauncher extends AndroidApplication {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.maxSimultaneousSounds = 150;
        this.createWakeLock(true); //TODO check if this works
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        BluetoothConnector.app = this;
        BTListUI.loc = getResources().getConfiguration().locale;
       // Elude.mpScreen = BluetoothConnectScreen.class;
        initialize(new Elude(), cfg);
    }
    
    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
    	BluetoothConnector.INSTANCE.processActivityResult(requestCode, resultCode, data);
    }
    
}