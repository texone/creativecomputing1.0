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
package cc.creativecomputing.nio;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;

import cc.creativecomputing.io.CCIOException;
import cc.creativecomputing.io.CCIOUtil;

/**
 * @author christianriekoff
 *
 */
public class CCFileChannel {
	
	/**
	 * The mode argument specifies the access mode in which the file is to be opened.
	 * @author christianriekoff
	 *
	 */
	public static enum CCFileMode{
		/**
		 * Open for reading only. Invoking any of the write methods of the 
		 * resulting object will cause an IOException to be thrown.
		 */
		R("r"),	 
		/**
		 * Open for reading and writing. If the file does not already 
		 * exist then an attempt will be made to create it.
		 */
		RW("rw"),	 
		/**
		 * Open for reading and writing, as with "rw", and also require 
		 * that every update to the file's content or metadata be written 
		 * synchronously to the underlying storage device.
		 */
		RWS("rws"), 
		/**
		 * Open for reading and writing, as with "rw", and also require 
		 * that every update to the file's content be written synchronously
		 *  to the underlying storage device.
		 */
		RWD("rwd"); 	 
		private String _myName;
		
		private CCFileMode(String theName) {
			_myName = theName;
		}
		
		public String modeName() {
			return _myName;
		}
	}

	private String _myFile;
	private CCFileMode _myMode;
	
	private ByteBuffer _my8ByteBuffer = ByteBuffer.allocate(8);
	private ByteBuffer _my4ByteBuffer = ByteBuffer.allocate(4);
//	private ByteBuffer _my2ByteBuffer = ByteBuffer.allocate(2);
	
	private FileChannel _myFileChannel;
	private RandomAccessFile _myRandomAccessFile;
	
	public CCFileChannel(String theFile, CCFileMode theMode) {
		_myFile = theFile;
		_myMode = theMode;
		open();
	}
	
	/**
	 * Returns the underlying java file channel
	 * @return
	 */
	public FileChannel channel(){
		return _myFileChannel;
	}
	
	public void open() {
		if(_myFileChannel != null && _myFileChannel.isOpen())return;
		
		if(_myMode!= CCFileMode.R)CCIOUtil.createPath(_myFile);
		try {
			_myRandomAccessFile = new RandomAccessFile(_myFile, _myMode.modeName());
		} catch (FileNotFoundException e) {
			throw new CCIOException(e);
		}
		
		_myFileChannel = _myRandomAccessFile.getChannel();
//		try {
//			_myFileChannel.position(_myFileChannel.size());
//		} catch (IOException e) {
//			throw new CCIOException(e);
//		}
	}
	
	public void write(String theString) {
		write(theString.getBytes());
	}
	
	public void write(ByteBuffer theBuffer) {
		try {
			_myFileChannel.write(theBuffer);
		} catch (IOException e) {
			throw new CCIOException(e);
		}
	}
	
	public void write(ByteBuffer theBuffer, long thePosition) {
		try {
			_myFileChannel.write(theBuffer, thePosition);
		} catch (IOException e) {
			throw new CCIOException(e);
		}
	}
	
	public void append(ByteBuffer theBuffer) {
		try {
			write(theBuffer, _myFileChannel.size());
		} catch (IOException e) {
			throw new CCIOException(e);
		}
	}
	
	public void append() {
		
	}
	
	public void write(byte[] theBytes) {
		ByteBuffer myBuffer = ByteBuffer.wrap(theBytes);
		write(myBuffer);
	}
	
	public void write(float[] theData) {
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(theData.length * 4); //4 bytes per float
		byteBuf.asFloatBuffer().put(theData);
		byteBuf.position(0);
		write(byteBuf);
	}
	
	public int read(ByteBuffer theBuffer) {
		try {
			return _myFileChannel.read(theBuffer);
		} catch (IOException e) {
			throw new CCIOException(e);
		}
	}
	
	public void read(ByteBuffer theBuffer, long thePosition) {
		try {
			_myFileChannel.read(theBuffer, thePosition);
		} catch (IOException e) {
			throw new CCIOException(e);
		}
	}
	
	public void read(byte[] theBytes) {
		ByteBuffer myBuffer = ByteBuffer.allocate(theBytes.length);
		read(myBuffer);
		myBuffer.flip();
		myBuffer.get(theBytes);
	}
	
	public IntBuffer readInts(int theSize) {
		ByteBuffer myBuffer = ByteBuffer.allocate(theSize * 4);
		read(myBuffer);
		myBuffer.rewind();
		return myBuffer.asIntBuffer();
	}
	
	public FloatBuffer readFloats(int theSize) {
		ByteBuffer myBuffer = ByteBuffer.allocate(theSize * 4);
		read(myBuffer);
		myBuffer.position(0);
		return myBuffer.asFloatBuffer();
	}
	
	public long position() {
		try {
			return _myFileChannel.position();
		} catch (IOException e) {
			throw new CCIOException(e);
		}
	}
	
	public void force() {
		try {
			_myFileChannel.force(true);
		} catch (IOException e) {
			throw new CCIOException(e);
		}
	}
	
	public void close() {
		try {
			_myFileChannel.close();
		} catch (IOException e) {
			throw new CCIOException(e);
		}
	}
	
	public long size() {
		try {
			return _myFileChannel.size();
		} catch (IOException e) {
			throw new CCIOException(e);
		}
	}
	
	public void write(long theLong) {
		_my8ByteBuffer.putLong(0,theLong);
		_my8ByteBuffer.position(0);
		write(_my8ByteBuffer);
	}
	
	public long readLong() {
		_my8ByteBuffer.position(0);
		read(_my8ByteBuffer);
		return _my8ByteBuffer.getLong(0);
	}
	
	public void write(int theInt) {
		_my4ByteBuffer.putInt(0,theInt);
		_my4ByteBuffer.position(0);
		write(_my4ByteBuffer);
	}
	
	public void write(float theFloat) {
		_my4ByteBuffer.putFloat(0,theFloat);
		_my4ByteBuffer.position(0);
		write(_my4ByteBuffer);
	}
	
	public int readInt() {
		_my4ByteBuffer.position(0);
		read(_my4ByteBuffer);
		return _my4ByteBuffer.getInt(0);
	}
	
	public float readFloat() {
		_my4ByteBuffer.position(0);
		read(_my4ByteBuffer);
		return _my4ByteBuffer.getFloat(0);
	}
}
