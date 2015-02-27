package tungus.games.elude;

import tungus.games.elude.Assets.EludeMusic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Music.OnCompletionListener;

public class MusicSwitcher implements Runnable {

	private static int activeID = 0;

	private EludeMusic next;
	private float volume;

	public MusicSwitcher(EludeMusic next, float volume) {
		this.next = next;
		this.volume = volume;
	}

	@Override
	public void run() {
		final int id = ++activeID; 
		if (EludeMusic.currentPlaying != null) {
			for (int i = 1; i <= 100; i++) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				(EludeMusic.loopPart ? EludeMusic.currentPlaying.loop : EludeMusic.currentPlaying.start).setVolume(EludeMusic.volume * (1 - i/100f));
			}
			EludeMusic.currentPlaying.start.setVolume(0);
			EludeMusic.currentPlaying.loop.setVolume(0);
			EludeMusic.currentPlaying.start.stop();
			EludeMusic.currentPlaying.loop.stop();
		}
		if (EludeMusic.currentPlaying != null)
		EludeMusic.volume = volume;
		EludeMusic.loopPart = false;
		EludeMusic.currentPlaying = next;
		if (next != null) {			
			next.loop.setVolume(0);
			next.loop.play();			
			next.loop.pause();
			next.loop.setVolume(volume);
			next.loop.setLooping(true);
			next.start.setVolume(volume);
			next.start.setOnCompletionListener(new OnCompletionListener(){
				@Override
				public void onCompletion(Music m) {
					if (activeID != id) {
						return;
					}
					next.loop.play();
					EludeMusic.loopPart = true;
				}
			});
			next.start.play();
		}					
	}

}
