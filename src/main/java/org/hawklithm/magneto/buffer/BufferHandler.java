package org.hawklithm.magneto.buffer;

public interface BufferHandler {
	void store(String key,byte[] value);
	byte[] getValue(String key);
}
