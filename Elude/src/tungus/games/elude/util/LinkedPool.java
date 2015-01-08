package tungus.games.elude.util;

import tungus.games.elude.util.LinkedPool.Poolable;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Constructor;
import com.badlogic.gdx.utils.reflect.ReflectionException;

public class LinkedPool<T extends Poolable> extends SyncPool<T>{
	
	private final Constructor constructor;
	
	public LinkedPool(Class<? extends T> c, int initial) {
		super(initial);
		try {
			constructor = ClassReflection.getDeclaredConstructor(c, LinkedPool.class);
		} catch (ReflectionException e) {
			e.printStackTrace();
			throw new GdxRuntimeException("Couldn't find constructor on " + ClassReflection.getSimpleName(c));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected T newObject() {
		try {
			return (T)constructor.newInstance(this);
		} catch (ReflectionException e) {
			e.printStackTrace();
			throw new GdxRuntimeException("Couldn't call constructor on " + ClassReflection.getSimpleName(constructor.getDeclaringClass()));
		} catch (ClassCastException e) {
			e.printStackTrace();
			throw new GdxRuntimeException("Bad type from constructor on " + ClassReflection.getSimpleName(constructor.getDeclaringClass()));
		}
	}
	
	@SuppressWarnings("unchecked")
	public synchronized void free(Poolable p) {
		try {
			super.free((T)p);
		} catch (ClassCastException e) {
			e.printStackTrace();
			throw new GdxRuntimeException("Bad type freeing itself on " + ClassReflection.getSimpleName(constructor.getDeclaringClass()));
		}
		
	}
	
	public static abstract class Poolable implements com.badlogic.gdx.utils.Pool.Poolable {
		private final LinkedPool<?> pool;
		public Poolable(LinkedPool<?> p) {
			pool = p;
		}
		@Override
		public void reset() {}
		public void free() {
			pool.free(this);
		}
	}
}
