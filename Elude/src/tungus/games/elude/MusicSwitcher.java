package tungus.games.elude;

import tungus.games.elude.Assets.EludeMusic;

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
		int id = ++activeID; 
		if (EludeMusic.currentPlaying != null) {
			for (int i = 1; i <= 100; i++) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				(EludeMusic.loopPart ? EludeMusic.currentPlaying.loop : EludeMusic.currentPlaying.start).setVolume(EludeMusic.volume * (1 - i/100f));
			}
			(EludeMusic.loopPart ? EludeMusic.currentPlaying.loop : EludeMusic.currentPlaying.start).stop();
		}
		EludeMusic.volume = volume;
		EludeMusic.loopPart = false;		
		if (next != null) {
			EludeMusic.currentPlaying = next;
			next.loop.setVolume(0);
			next.loop.play();			
			next.loop.pause();
			next.loop.setVolume(volume);
			next.loop.setLooping(true);
			next.start.setVolume(volume);

			next.start.play();

			try {
				Thread.sleep(next.startLength);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (activeID != id) {
				return;
			}

			next.loop.play();
			EludeMusic.loopPart = true;

		}					
	}

}
