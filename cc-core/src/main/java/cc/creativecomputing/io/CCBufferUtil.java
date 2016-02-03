/*
 * Copyright (c) 2013 christianr.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     christianr - initial API and implementation
 */
package cc.creativecomputing.io;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import cc.creativecomputing.graphics.texture.CCPixelType;

import com.jogamp.common.nio.Buffers;

/**
 * This class contains several utility function for java nio buffer objects.
 * 
 * @author christianriekoff
 * 
 */
public class CCBufferUtil {
	public static final int SIZE_OF_FLOAT = 4;
	public static final int SIZE_OF_DOUBLE = 8;

	public static final int SIZE_OF_BYTE = 1;
	public static final int SIZE_OF_SHORT = 2;
	public static final int SIZE_OF_INT = 4;
	public static final int SIZE_OF_LONG = 8;

	/**
	 * Creates a FloatBuffer of the given size
	 * 
	 * @param theSize
	 *            size for the buffer
	 * @return FloatBuffer of the given size
	 */
	public static DoubleBuffer newDoubleBuffer(final int theSize) {
		return DoubleBuffer.allocate(theSize);
	}

	public static DoubleBuffer newDirectDoubleBuffer(final int theSize) {
		ByteBuffer myBuffer = ByteBuffer.allocateDirect(theSize * SIZE_OF_DOUBLE);
		myBuffer.order(ByteOrder.nativeOrder());
		return myBuffer.asDoubleBuffer();
	}

	/**
	 * Creates a FloatBuffer of the given size
	 * 
	 * @param theSize
	 *            size for the buffer
	 * @return FloatBuffer of the given size
	 */
	public static FloatBuffer newFloatBuffer(final int theSize) {
		return FloatBuffer.allocate(theSize);
	}

	public static FloatBuffer newDirectFloatBuffer(final int theSize) {
		ByteBuffer myBuffer = ByteBuffer.allocateDirect(theSize * SIZE_OF_FLOAT);
		myBuffer.order(ByteOrder.nativeOrder());
		return myBuffer.asFloatBuffer();
	}

	/**
	 * Fills the given FloatBuffer with the given default value
	 * 
	 * @param theBuffer
	 *            buffer to be filled
	 * @param theValue
	 *            value for the buffer
	 */
	public static void fill(FloatBuffer theBuffer, float theValue) {
		theBuffer.rewind();
		for (int i = 0; i < theBuffer.limit(); i++) {
			theBuffer.put(theValue);
		}
		theBuffer.rewind();
	}

	/**
	 * @param theLength
	 * @return
	 */
	public static ByteBuffer newByteBuffer(int theSize) {
		return ByteBuffer.allocate(theSize);
	}

	public static ByteBuffer newDirectByteBuffer(final int theSize) {
		ByteBuffer myBuffer = ByteBuffer.allocateDirect(theSize * SIZE_OF_BYTE);
		myBuffer.order(ByteOrder.nativeOrder());
		return myBuffer;
	}

	/**
	 * Fills the given ByteBuffer with the given default value
	 * 
	 * @param theBuffer
	 *            buffer to be filled
	 * @param theValue
	 *            value for the buffer
	 */
	public static void fill(ByteBuffer theBuffer, byte theValue) {
		theBuffer.rewind();
		for (int i = 0; i < theBuffer.limit(); i++) {
			theBuffer.put(theValue);
		}
		theBuffer.rewind();
	}

	/**
	 * Creates a ShortBuffer of the given size
	 * 
	 * @param theSize
	 *            size for the buffer
	 * @return ShortBuffer of the given size
	 */
	public static ShortBuffer newShortBuffer(final int theSize) {
		return ShortBuffer.allocate(theSize);
	}

	public static ShortBuffer newDirectShortBuffer(final int theSize) {
		ByteBuffer myBuffer = ByteBuffer.allocateDirect(theSize * SIZE_OF_SHORT);
		myBuffer.order(ByteOrder.nativeOrder());
		return myBuffer.asShortBuffer();
	}

	/**
	 * Fills the given ShortBuffer with the given default value
	 * 
	 * @param theBuffer
	 *            buffer to be filled
	 * @param theValue
	 *            value for the buffer
	 */
	public static void fill(ShortBuffer theBuffer, short theValue) {
		theBuffer.rewind();
		for (int i = 0; i < theBuffer.limit(); i++) {
			theBuffer.put(theValue);
		}
		theBuffer.rewind();
	}

	/**
	 * @param theI
	 * @return
	 */
	public static IntBuffer newIntBuffer(int theSize) {
		return IntBuffer.allocate(theSize);
	}

	public static IntBuffer newDirectIntBuffer(final int theSize) {
		ByteBuffer myBuffer = ByteBuffer.allocateDirect(theSize * SIZE_OF_INT);
		myBuffer.order(ByteOrder.nativeOrder());
		return myBuffer.asIntBuffer();
	}

	/**
	 * Fills the given IntBuffer with the given default value
	 * 
	 * @param theBuffer
	 *            buffer to be filled
	 * @param theValue
	 *            value for the buffer
	 */
	public static void fill(IntBuffer theBuffer, int theValue) {
		theBuffer.rewind();
		for (int i = 0; i < theBuffer.limit(); i++) {
			theBuffer.put(theValue);
		}
		theBuffer.rewind();
	}

	public static final int sizeOfGLType(CCPixelType theType) {
		switch (theType) {
		case UNSIGNED_BYTE:
			return SIZE_OF_BYTE;
		case BYTE:
			return SIZE_OF_BYTE;
		case UNSIGNED_SHORT:
			return SIZE_OF_SHORT;
		case SHORT:
			return SIZE_OF_SHORT;
		case FLOAT:
			return SIZE_OF_FLOAT;
		case FIXED:
			return SIZE_OF_INT;
		case INT:
			return SIZE_OF_INT;
		case UNSIGNED_INT:
			return SIZE_OF_INT;
		case DOUBLE:
			return SIZE_OF_DOUBLE;
		default:
		}
		return -1;
	}

	public static final Buffer newDirectGLBuffer(CCPixelType theType,
			int numElements) {
		switch (theType) {
		case UNSIGNED_BYTE:
		case BYTE:
			return newDirectByteBuffer(numElements);
		case UNSIGNED_SHORT:
		case SHORT:
			return newDirectShortBuffer(numElements);
		case FLOAT:
			return newDirectFloatBuffer(numElements);
		case FIXED:
		case INT:
		case UNSIGNED_INT:
			return newDirectIntBuffer(numElements);
		case DOUBLE:
			return newDirectDoubleBuffer(numElements);
        default:
		}
		return null;
	}

	public static final Buffer sliceGLBuffer(ByteBuffer parent, int bytePos,
			int byteLen, CCPixelType theType) {
		if (parent == null || byteLen == 0) {
			return null;
		}
		parent.position(bytePos);
		parent.limit(bytePos + byteLen);

		switch (theType) {
		case UNSIGNED_BYTE:
		case BYTE:
			return parent.slice();
		case UNSIGNED_SHORT:
		case SHORT:
			return parent.asShortBuffer();
		case FLOAT:
			return parent.asFloatBuffer();
		case FIXED:
		case INT:
		case UNSIGNED_INT:
			return parent.asIntBuffer();
		case DOUBLE:
			return parent.asDoubleBuffer();
        default:
		}
		return null;
	}

	private static class BytesRead {
		BytesRead(int payloadLen, byte[] data) {
			this.payloadLen = payloadLen;
			this.data = data;
		}

		int payloadLen;
		byte[] data;
	}

	private static BytesRead readAllImpl(InputStream stream) throws IOException {
		if (!(stream instanceof BufferedInputStream)) {
			stream = new BufferedInputStream(stream);
		}
		int avail = stream.available();
		byte[] data = new byte[avail];
		int numRead = 0;
		int pos = 0;
		do {
			if (pos + avail > data.length) {
				byte[] newData = new byte[pos + avail];
				System.arraycopy(data, 0, newData, 0, pos);
				data = newData;
			}
			numRead = stream.read(data, pos, avail);
			if (numRead >= 0) {
				pos += numRead;
			}
			avail = stream.available();
		} while (avail > 0 && numRead >= 0);

		return new BytesRead(pos, data);
	}

	public static ByteBuffer readAll2Buffer(InputStream stream)
			throws IOException {
		BytesRead bytesRead = readAllImpl(stream);
		return Buffers.newDirectByteBuffer(bytesRead.data, 0,
				bytesRead.payloadLen);
	}

	// ----------------------------------------------------------------------
	// Conversion routines
	//
	public final static float[] getFloatArray(double[] source) {
		int i = source.length;
		float[] dest = new float[i--];
		while (i >= 0) {
			dest[i] = (float) source[i];
			i--;
		}
		return dest;
	}
}
