package tungus.games.elude.util;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

/** A pool class with synchronized functions to be used from multiple threads*/

abstract public class SyncPool<T> extends Pool<T> {

	public SyncPool() {
		super();
	}

	public SyncPool(int initialCapacity) {
		super(initialCapacity);
	}

	public SyncPool(int initialCapacity, int max) {
		super(initialCapacity, max);
	}
	
	@Override
	public synchronized T obtain () {
		return super.obtain();
	}
	
	@Override
	public synchronized void free(T object) {
		super.free(object);
	}
	
	@Override
	public synchronized void freeAll (Array<T> objects) {
		super.freeAll(objects);
	}
	
	@Override
	public synchronized void clear() {
		super.clear();
	}
	
	@Override
	public synchronized int getFree () {
		return super.getFree();
	}
}
