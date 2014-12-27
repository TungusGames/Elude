package tungus.games.elude.game.multiplayer.transfer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import tungus.games.elude.game.client.worldrender.phases.RenderPhase;
import tungus.games.elude.game.client.worldrender.renderable.Renderable;
import tungus.games.elude.game.multiplayer.Connection.TransferData;
import tungus.games.elude.game.server.Updatable;
import tungus.games.elude.game.server.Vessel;
import tungus.games.elude.game.server.World;
import tungus.games.elude.levels.loader.FiniteLevelLoader;
import tungus.games.elude.util.LinkedPool.Poolable;

public class RenderInfo extends TransferData {
	private static final long serialVersionUID = -4315239911779247372L;
	
	public float[] hp = null;
	public float progress = -1;
	
	public List<List<Renderable>> phases = new ArrayList<List<Renderable>>();
		
	public RenderInfo() {
		for (int i = 0; i < RenderPhase.values().length; i++) {
			phases.add(new LinkedList<Renderable>());
		}
	}
	
	public RenderInfo(List<Renderable> e) {
		this();
		phases.set(RenderPhase.EFFECT.ordinal(), e);
	}

	public void setFromWorld(World w) {
		int i = 0;
		for (List<Renderable> phaseList : phases) {
			if (i != RenderPhase.EFFECT.ordinal()) {
				while (!phaseList.isEmpty()) {
					((Poolable)phaseList.remove(0)).free();
				}
			}
			i++;
		}
		if (w.waveLoader instanceof FiniteLevelLoader) {
			this.progress = ((FiniteLevelLoader)w.waveLoader).progress();
		}
		
		for (Vessel vessel : w.vessels) {
			Renderable r = vessel.getRenderable();
			RenderPhase ph = r.phase;
			int ord = ph.ordinal();
			List<Renderable> list = phases.get(ord);
			list.add(r);
		}	
		
		for (Updatable element : w.updatables) {
			Renderable r = element.getRenderable();
			if (r != null) {
				phases.get(r.phase.ordinal()).add(r);
			}			
		}
		
		for (i = 0; i < hp.length; i++) {
			hp[i] = w.vessels.get(i).hp / Vessel.MAX_HP;
		}
	}
	
	@Override
	public TransferData copyTo(TransferData otherData) {
		RenderInfo other = null;
		if (otherData instanceof RenderInfo) {
			other = (RenderInfo)otherData;
		} else {
			other = new RenderInfo();
		}
		super.copyTo(other);
		int size = phases.size();
		for (int i = 0; i < size; i++) {
			List<Renderable> otherPhaseList = other.phases.get(i);
			//if (i != RenderPhase.EFFECT.ordinal() || other.handled) {
				while (!otherPhaseList.isEmpty()) {
					((Poolable)otherPhaseList.remove(0)).free();
				}				
			//}
			List<Renderable> myPhaseList = this.phases.get(i);
			for (Renderable r : myPhaseList) {
				otherPhaseList.add(r.clone());
			}
		}
		size = hp.length;
		if (other.hp == null || other.hp.length < hp.length)
			other.hp = new float[hp.length];
		for (int i = 0; i < size; i++)
			other.hp[i] = hp[i];
		
		other.progress = this.progress;
		return other;
	}
}
