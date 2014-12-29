package tungus.games.elude.game.client.worldrender;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.utils.IntMap;

public class LastingEffectCollection implements Iterable<PooledEffect> {
	
	private IntMap<List<PooledEffect>> lastingEffects = new IntMap<List<PooledEffect>>(300);
	
	public void put(int id, PooledEffect effect) {
		List<PooledEffect> list = lastingEffects.get(id);
		if (list == null) {
			list = new LinkedList<PooledEffect>();
			lastingEffects.put(id, list);
		}
		list.add(effect);
	}
	
	public PooledEffect getFirst(int id) {
		List<PooledEffect> list = lastingEffects.get(id);
		if (list == null) 
			return null;
		return list.get(0);
	}
	
	public List<PooledEffect> getAll(int id) {
		List<PooledEffect> list = lastingEffects.get(id);
		if (list == null) {
			list = new LinkedList<PooledEffect>();
			lastingEffects.put(id, list);
		}
		return list;
	}
	
	@Override
	public Iterator<PooledEffect> iterator() {
		return new LastingEffectIterator();
	}
	
	private class LastingEffectIterator implements Iterator<PooledEffect> {
		
		private Iterator<List<PooledEffect>> mapIterator;
		private ListIterator<PooledEffect> listIterator;
		
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
		public PooledEffect next() {
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
