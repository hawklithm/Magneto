package org.hawklithm.magneto.buffer;

public interface BufferHandler<T,V> {
	void store(T key,V value);
	V getValue(T key);
}
