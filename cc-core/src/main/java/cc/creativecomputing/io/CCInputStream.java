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

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author christianriekoff
 *
 */
public class CCInputStream {

	private InputStream _myStream;
	private long bytePosition;
	
	public CCInputStream(InputStream theStream) {
		_myStream = theStream;
	}
	
	public int readUnsignedByte () throws IOException {
		bytePosition++;
		int b = _myStream.read();
		if (b == -1) throw new EOFException("Unexpected end of file.");
		return b;
	}
 
	public byte readByte () throws IOException {
		return (byte)readUnsignedByte();
	}
 
	public int readUnsignedShort () throws IOException {
		return (readUnsignedByte() << 8) + readUnsignedByte();
	}
 
	public short readShort () throws IOException {
		return (short)readUnsignedShort();
	}
 
	public long readUnsignedLong () throws IOException {
		long value = readUnsignedByte();
		value = (value << 8) + readUnsignedByte();
		value = (value << 8) + readUnsignedByte();
		value = (value << 8) + readUnsignedByte();
		return value;
	}
 
	public void skip (int bytes) throws IOException {
		_myStream.skip(bytes);
		bytePosition += bytes;
	}
 
	public void seek (long position) throws IOException {
		_myStream.skip(position - bytePosition);
		bytePosition = position;
	}
}
