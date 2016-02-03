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


/**
 * Use this class to easily write and read data from a byte array.
 * @author christianriekoff
 *
 */
public class CCByteBuffer {
	protected final byte[] _myBytes;
	protected final int _myBytesLength;

	public CCByteBuffer(final byte[] theBytes, final int theBytesLength) {
		_myBytes = theBytes;
		_myBytesLength = theBytesLength;
	}
	
	public CCByteBuffer(CCByteBuffer theBuffer){
		this(theBuffer._myBytes, theBuffer._myBytesLength);
	}
	
	public CCByteBuffer(final byte[] theBytes) {
		this(theBytes, theBytes.length);
	}
	
	public CCByteBuffer(final int theSize) {
		this(new byte[theSize]);
	}
	
	/**
	 * Returns the lengths of the stream in bytes
	 * @return
	 */
	public int length() {
		return _myBytesLength;
	}
	
	public byte[] bytes() {
		return _myBytes;
	}
	
	/**
	 * Returns the next n bytes from the stream as array
	 * @param theLength, number of bytes to return
	 * @return
	 */
	public byte[] getBytes(final int theOffset, final int theLength){
		final byte[] myResultBytes = new byte[theLength];
		
		for (int i = 0; i < theLength; i++){
			myResultBytes[i] = _myBytes[theOffset + i];
		}
		
		return myResultBytes;
	}

	/**
	 * Read a char from the byte stream.
	 * @return a Character
	 */
	public char readChar(int theOffset) {
		return (char) _myBytes[theOffset];
	}

	/**
	 * Get an Integer (32 bit int) from the byte buffer. (Little Endian, LSB first)
	 * @return an Integer
	 */
	public int getInteger(int theOffset) {
		final int result = 
			(_myBytes[theOffset] & 0xFF) << 24 | 
			(_myBytes[theOffset + 1] & 0xFF) << 16 |
			(_myBytes[theOffset + 2] & 0xFF) << 8 |
			_myBytes[theOffset + 3] & 0xFF;
			
		return result;
	}
	
	/**
	 * Returns the byte at the given offset inside the buffer
	 * @param theOffset the offset
	 * @return the byte at the given position
	 */
	public byte getByte(int theOffset) {
		return _myBytes[theOffset];
	}
	
	/**
	 * Returns the char at the given offset inside the buffer
	 * @param theOffset the offset
	 * @return the char at the given position
	 */
	public char getChar(int theOffset) {
		return (char)_myBytes[theOffset];
	}

	/**
	 * Read a Big Integer (long) from the byte stream.
	 * @return a BigInteger
	 */
	public long getLong(int theOffset) {
		final long result = 
			(_myBytes[theOffset] & 0xFFl) << 56 | 
			(_myBytes[theOffset + 1] & 0xFFl) << 48 | 
			(_myBytes[theOffset + 2] & 0xFFl) << 40 |
			(_myBytes[theOffset + 3] & 0xFFl) << 32 |
			(_myBytes[theOffset + 4] & 0xFFl) << 24 | 
			(_myBytes[theOffset + 5] & 0xFFl) << 16 |
			(_myBytes[theOffset + 6] & 0xFFl) << 8 |
			_myBytes[theOffset + 7] & 0xFFl;
			
		return result;
	}

	/**
	 * Read a float from the byte stream.
	 * @return a Float
	 */
	public float getFloat(int theOffset) {
		return Float.intBitsToFloat(getInteger(theOffset));
	}

	/**
	 * Read a double
	 * @return a Double
	 */
	public double getDouble(int theOffset) {
		return Double.longBitsToDouble(getLong(theOffset));
	}

	/**
	 * Get the length of the string currently in the byte stream.
	 * @return
	 */
	private int lengthOfCurrentString(int theOffset, int theLength) {
		int i = 0;
		while (_myBytes[theOffset + i] != 0 && theOffset + i < theOffset + theLength - 1){
			i++;
		}
		
		return i;
	}

	/**
	 * Read a string from the byte stream.
	 * @return the next string in the byte stream
	 */
	public String readString(int theOffset, int theLength) {
		int strLen = lengthOfCurrentString(theOffset, theLength);
		char[] stringChars = new char[strLen];
		for (int i = 0; i < strLen; i++)
			stringChars[i] = (char) _myBytes[theOffset+i];
		return new String(stringChars);
	}
}
