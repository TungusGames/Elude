package tungus.games.elude.game.client.worldrender;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import tungus.games.elude.game.client.worldrender.lastingeffects.LastingEffect;

import com.badlogic.gdx.utils.IntMap;

public class LastingEffectCollection implements Iterable<LastingEffect> {
	
	private IntMap<List<LastingEffect>> lastingEffects = new IntMap<List<LastingEffect>>(300);
	
	public void put(int id, LastingEffect effect) {
		List<LastingEffect> list = lastingEffects.get(id);
		if (list == null) {
			list = new LinkedList<LastingEffect>();
			lastingEffects.put(id, list);
		}
		list.add(effect);
	}
	
	public LastingEffect getFirst(int id) {
		List<LastingEffect> list = lastingEffects.get(id);
		if (list == null) 
			return null;
		return list.get(0);
	}
	
	public List<LastingEffect> getAll(int id) {
		List<LastingEffect> list = lastingEffects.get(id);
		if (list == null) {
			list = new LinkedList<LastingEffect>();
			lastingEffects.put(id, list);
		}
		return list;
	}
	
	@Override
	public Iterator<LastingEffect> iterator() {
		return new LastingEffectIterator();
	}
	
	private class LastingEffectIterator implements Iterator<LastingEffect> {
		
		private Iterator<List<LastingEffect>> mapIterator;
		private ListIterator<LastingEffect> listIterator;
		
		public LastingEffectIterator() {
			mapIterator = lastingEffects.values();
			if (mapIterator.hasNext()) {
				listIterator = mapIterator.next().listIterator();
			} else {
				listIterator = null;
			}
		}
		
		@Override
		public boolean hasNext() {
			return mapIterator.hasNext() || (listIterator != null && listIterator.hasNext());
		}

		@Override
		public LastingEffect next() {
			if (!listIterator.hasNext()) {
				listIterator = mapIterator.next().listIterator();				
			}
			return listIterator.next();
		}

		@Override
		public void remove() {
			listIterator.remove();
			if (!listIterator.hasNext() && !listIterator.hasPrevious()) {
				mapIterator.remove();
			}
		}
		
	}

}
