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
package cc.creativecomputing.graphics.texture;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.texture.CCPixelFormat;
import cc.creativecomputing.graphics.texture.CCPixelInternalFormat;
import cc.creativecomputing.graphics.texture.CCPixelStorageModes;
import cc.creativecomputing.graphics.texture.CCPixelType;
import cc.creativecomputing.io.CCBufferUtil;

/**
 * <p>
 * Defines a data format for a graphical image. The image
 * is defined by a format, a height and width, and the image data. The width and
 * height must be greater than 0. The data is contained in a byte buffer, and
 * should be packed before creation of the image object.
 * </p>
 * <p>
 * Represents the data for an OpenGL texture. This is separated from
 * the notion of a Texture to support things like streaming in of
 * textures in a background thread without requiring an OpenGL context
 * to be current on that thread. 
 * <p>
 * @author info
 *
 */
public class CCTextureData {
	
	/**
	 * Defines a callback mechanism to allow the user to explicitly deallocate native resources (memory-mapped files,
	 * etc.) associated with a particular TextureData.
	 */
	public static interface Flusher {
		/**
		 * Flushes any native resources associated with this TextureData.
		 */
		public void flush();
	}
	
	/**
	 * width of the texture this data represents in pixels
	 */
	protected int _myWidth;
	
	/**
	 * width of the texture this data represents in pixels
	 */
	protected int _myHeight;
	
	/**
	 * number of pixels of border this texture data has (0 or 1)
	 */
	protected int _myBorder;
	
	/**
	 * OpenGL internal format for the resulting texture; must be specified, may not be 0
	 */
	protected CCPixelInternalFormat _myPixelInternalFormat;
	
	/**
	 * OpenGL pixel format for the resulting texture; must be specified, may not be 0
	 */
	private CCPixelFormat _myPixelFormat;
	
	/**
	 * OpenGL type of the pixels of the texture
	 */
	protected CCPixelType _myPixelType;
	
	/**
	 * indicates whether the texture data is in compressed form
	 */
	protected boolean _myIsDataCompressed;
	
	/**
	 * indicates whether the texture coordinates must be flipped 
	 * vertically in order to properly display the texture
	 */
	protected boolean _myMustFlipVertically;
	
	/**
	 * buffer containing the texture data or all mipmap levels of the texture's data
	 */
	protected Buffer[] _myBuffer;
	
	protected Flusher _myFlusher;
	
	protected int _myEstimatedMemorySize;
	
	/**
	 * pixel storage modes for correct memory read out
	 */
	protected final CCPixelStorageModes _myPixelStorageModes;
	
	public CCTextureData() {
		_myPixelStorageModes = new CCPixelStorageModes();
	}
	
	private int[] _myColorIntChannels;
	private float[] _myColorFloatChannels;

	/**
	 * Constructs a new TextureData object with the specified parameters and data contained in the given Buffer. The
	 * optional Flusher can be used to clean up native resources associated with this TextureData when processing is
	 * complete; for example, closing of memory-mapped files that might otherwise require a garbage collection to
	 * reclaim and close.
	 * 
	 * @param theInternalFormat the OpenGL internal format for the resulting texture; must be specified, may not be 0
	 * @param theWidth the width in pixels of the texture
	 * @param theHeight the height in pixels of the texture
	 * @param theBorder the number of pixels of border this texture data has (0 or 1)
	 * @param thePixelFormat the OpenGL pixel format for the resulting texture; must be specified, may not be 0
	 * @param thePixelType the OpenGL type of the pixels of the texture
	 * @param theIsDataCompressed indicates whether the texture data is in compressed form (e.g.
	 *        COMPRESSED_RGB_S3TC_DXT1_EXT)
	 * @param theMustFlipVertically indicates whether the texture coordinates must be flipped vertically in order to
	 *        properly display the texture
	 * @param theBuffer the buffer containing the texture data
	 * @param theFlusher optional flusher to perform cleanup tasks upon call to flush()
	 * 
	 * @throws CCTextureException if any parameters of the texture data were invalid, 
	 * 			such as requesting mipmap generation for a compressed texture
	 */
	public CCTextureData(
		final int theWidth, final int theHeight, final int theBorder,
		final CCPixelInternalFormat theInternalFormat, final CCPixelFormat thePixelFormat, 
		final CCPixelType thePixelType, 
		final boolean theIsDataCompressed, final boolean theMustFlipVertically,
		final Buffer theBuffer, final Flusher theFlusher
	){
		this(
			theWidth, theHeight, 
			theBorder, 
			theInternalFormat, 
			thePixelFormat, 
			thePixelType, 
			theIsDataCompressed, 
			theMustFlipVertically, 
			new Buffer[] {theBuffer}, 
			theFlusher
		);
	}

	/**
	 * Constructs a new TextureData object with the specified parameters and data for multiple mipmap levels contained
	 * in the given array of Buffers. The optional Flusher can be used to clean up native resources associated with this
	 * TextureData when processing is complete; for example, closing of memory-mapped files that might otherwise require
	 * a garbage collection to reclaim and close.
	 * 
	 * @param theInternalFormat the OpenGL internal format for the resulting texture; must be specified, may not be 0
	 * @param theWidth the width in pixels of the topmost mipmap level of the texture
	 * @param theHeight the height in pixels of the topmost mipmap level of the texture
	 * @param theBorder the number of pixels of border this texture data has (0 or 1)
	 * @param thePixelFormat the OpenGL pixel format for the resulting texture; must be specified, may not be 0
	 * @param thePixelType the OpenGL type of the pixels of the texture
	 * @param theIsDataCompressed indicates whether the texture data is in compressed form (e.g.
	 *        GL_COMPRESSED_RGB_S3TC_DXT1_EXT)
	 * @param theMustFlipVertically indicates whether the texture coordinates must be flipped vertically in order to
	 *        properly display the texture
	 * @param theMipmapData the buffers containing all mipmap levels of the texture's data
	 * @param theFlusher optional flusher to perform cleanup tasks upon call to flush()
	 */
	public CCTextureData(
		final int theWidth, final int theHeight, final int theBorder,
		final CCPixelInternalFormat theInternalFormat, final CCPixelFormat thePixelFormat, 
		final CCPixelType thePixelType, 
		final boolean theIsDataCompressed, final boolean theMustFlipVertically, 
		final Buffer[] theMipmapData, final Flusher theFlusher
	){
		_myWidth = theWidth;
		_myHeight = theHeight;
		_myBorder = theBorder;

		_myPixelInternalFormat = theInternalFormat;
		_myPixelFormat = thePixelFormat;
		_myPixelType = thePixelType;
		
		_myIsDataCompressed = theIsDataCompressed;
		_myMustFlipVertically = theMustFlipVertically;
		
		_myBuffer = (Buffer[]) theMipmapData.clone();
		_myFlusher = theFlusher;
		
		_myPixelStorageModes = new CCPixelStorageModes();
		_myPixelStorageModes.alignment(1);
		
		for (int i = 0; i < theMipmapData.length; i++) {
			_myEstimatedMemorySize += estimatedMemorySize(theMipmapData[i]);
		}

		_myColorIntChannels = new int[_myPixelFormat.numberOfChannels];
		_myColorFloatChannels = new float[_myPixelFormat.numberOfChannels];
	}
	public CCTextureData(final int theWidth, final int theHeight, CCPixelFormat theFormat, CCPixelType theType) {
		this(theWidth, theHeight, CCPixelFormat.internalPixelFormat(theFormat.glID), theFormat, theType);
	}
	public CCTextureData(
		final int theWidth, final int theHeight, 
		CCPixelInternalFormat theInternalFormat, 
		CCPixelFormat theFormat, 
		CCPixelType theType
	) {
		_myWidth = theWidth;
		_myHeight = theHeight;
		_myBorder = 0;
		
		_myPixelInternalFormat = theInternalFormat;
		_myPixelFormat = theFormat;
		_myPixelType = theType;
		
		_myIsDataCompressed = false;
		_myMustFlipVertically = false;
		
		Buffer myBuffer = null;
		
		switch(theType) {
		case FLOAT:
			FloatBuffer myFloatBuffer = CCBufferUtil.newFloatBuffer(_myPixelFormat.numberOfChannels * _myWidth * _myHeight);
			CCBufferUtil.fill(myFloatBuffer, 0);
			myBuffer = myFloatBuffer;
			break;
		case BYTE:
		case UNSIGNED_BYTE:
			ByteBuffer myByteBuffer = CCBufferUtil.newByteBuffer(_myPixelFormat.numberOfChannels * _myWidth * _myHeight);
			CCBufferUtil.fill(myByteBuffer, (byte)0);
			myBuffer = myByteBuffer;
			break;
		case SHORT:
		case UNSIGNED_SHORT:
			ShortBuffer myShortBuffer = CCBufferUtil.newShortBuffer(_myPixelFormat.numberOfChannels * _myWidth * _myHeight);
			CCBufferUtil.fill(myShortBuffer, (short)0);
			myBuffer = myShortBuffer;
			break;
		case INT:
		case UNSIGNED_INT:
			IntBuffer myIntBuffer = CCBufferUtil.newIntBuffer(_myPixelFormat.numberOfChannels * _myWidth * _myHeight);
			CCBufferUtil.fill(myIntBuffer, 0);
			myBuffer = myIntBuffer;
			break;
		default:
		}
		_myBuffer = new Buffer[] {myBuffer};
		
		_myFlusher = null;
		_myPixelStorageModes = new CCPixelStorageModes();
		_myPixelStorageModes.alignment(4); 
		_myEstimatedMemorySize = estimatedMemorySize(_myBuffer[0]);

		_myColorIntChannels = new int[_myPixelFormat.numberOfChannels];
		_myColorFloatChannels = new float[_myPixelFormat.numberOfChannels];
	}
	
	public CCTextureData(final int theWidth, final int theHeight) {
		_myWidth = theWidth;
		_myHeight = theHeight;
		_myBorder = 0;
		
		_myPixelInternalFormat = CCPixelInternalFormat.RGBA;
		_myPixelFormat = CCPixelFormat.RGBA;
		_myPixelType = CCPixelType.UNSIGNED_BYTE;
		
		_myIsDataCompressed = false;
		_myMustFlipVertically = false;
		
		ByteBuffer myBytes = ByteBuffer.allocate(theWidth * theHeight * 4);
		for(int i = 0; i < theWidth * theHeight * 4;i++) {
			myBytes.put((byte)0);
		}
		myBytes.rewind();
		_myBuffer = new Buffer[] {myBytes};
		
		_myFlusher = null;
		_myPixelStorageModes = new CCPixelStorageModes();
		_myPixelStorageModes.alignment(4);
		_myEstimatedMemorySize = estimatedMemorySize(_myBuffer[0]);

		_myColorIntChannels = new int[_myPixelFormat.numberOfChannels];
		_myColorFloatChannels = new float[_myPixelFormat.numberOfChannels];
	}
	
	/**
	 * Estimates the memory size of the given buffer
	 * @param theBuffer
	 * @return the memory size of the given buffer
	 */
	protected int estimatedMemorySize(final Buffer theBuffer) {
		if (theBuffer == null) {
			return 0;
		}
		int capacity = theBuffer.capacity();
		if (theBuffer instanceof ByteBuffer) {
			return capacity;
		} else if (theBuffer instanceof IntBuffer) {
			return capacity * CCBufferUtil.SIZE_OF_INT;
		} else if (theBuffer instanceof FloatBuffer) {
			return capacity * CCBufferUtil.SIZE_OF_FLOAT;
		} else if (theBuffer instanceof ShortBuffer) {
			return capacity * CCBufferUtil.SIZE_OF_SHORT;
		} else if (theBuffer instanceof LongBuffer) {
			return capacity * CCBufferUtil.SIZE_OF_LONG;
		} else if (theBuffer instanceof DoubleBuffer) {
			return capacity * CCBufferUtil.SIZE_OF_DOUBLE;
		}
		throw new CCTextureException("Unexpected buffer type " + theBuffer.getClass().getName());
	}
	
	/** 
	 * Returns the width in pixels of the texture data. 
	 **/
	public int width() {
		return _myWidth;
	}

	/** 
	 * Sets the width in pixels of the texture data. 
	 **/
	public void width(final int theWidth) {
		_myWidth = theWidth;
	}

	/** 
	 * Returns the height in pixels of the texture data. 
	 **/
	public int height() {
		return _myHeight;
	}

	/** 
	 * Sets the height in pixels of the texture data. 
	 **/
	public void height(final int theHeight) {
		_myHeight = theHeight;
	}

	/** 
	 * Returns the border in pixels of the texture data. 
	 **/
	public int border() {
		return _myBorder;
	}

	/** 
	 * Sets the border in pixels of the texture data. 
	 **/
	public void border(final int theBorder) {
		_myBorder = theBorder;
	}

	/** 
	 * Returns the intended internal format of the texture data. 
	 **/
	public CCPixelInternalFormat internalFormat() {
		return _myPixelInternalFormat;
	}

	/** 
	 * Sets the intended internal format of the texture data. 
	 **/
	public void internalFormat(final CCPixelInternalFormat theInternalFormat) {
		_myPixelInternalFormat = theInternalFormat;
	}

	/** 
	 * Returns the intended pixel format of the texture data. 
	 **/
	public CCPixelFormat pixelFormat() {
		return _myPixelFormat;
	}

	/** 
	 * Sets the intended pixel format of the texture data. 
	 **/
	public void pixelFormat(final CCPixelFormat thePixelFormat) {
		_myPixelFormat = thePixelFormat;

		if(_myPixelFormat == null)return;
		
		_myColorIntChannels = new int[_myPixelFormat.numberOfChannels];
		_myColorFloatChannels = new float[_myPixelFormat.numberOfChannels];
	}

	/** 
	 * Returns the intended OpenGL pixel type of the texture data. 
	 **/
	public CCPixelType pixelType() {
		return _myPixelType;
	}

	/** 
	 * Sets the intended OpenGL pixel type of the texture data. 
	 **/
	public void pixelType(final CCPixelType thePixelType) {
		_myPixelType = thePixelType;
	}

	/** 
	 * Indicates whether the texture data is in compressed form. 
	 **/
	public boolean isDataCompressed() {
		return _myIsDataCompressed;
	}

	/** 
	 * Indicates whether the texture coordinates must be flipped
	 * vertically for proper display. 
	 **/
	public boolean mustFlipVertically() {
		return _myMustFlipVertically;
	}

	/** 
	 * Returns the texture data, or null if it is specified as a set of mipmaps. 
	 **/
	public Buffer buffer() {
		return _myBuffer[0];
	}
	
	/**
	 * Returns the Buffer for the given id
	 * @param theID id of the buffer
	 * @return
	 */
	public Buffer buffer(final int theID) {
		return _myBuffer[theID];
	}

	/** 
	 * Sets the texture data. 
	 **/
	public void buffer(final Buffer theBuffer) {
		_myBuffer = new Buffer[1];
		_myBuffer[0] = theBuffer;
	}
	
	public void buffers(final Buffer[] theBuffer) {
		_myBuffer = theBuffer;
	}

	/**
	 * Returns all mipmap levels for the texture data, or null if it is specified as a single image.
	 */
	public Buffer[] mipmapData() {
		return _myBuffer;
	}

	/** 
	 * Returns reference to the pixel storage modes for this texture data object. 
	 **/
	public CCPixelStorageModes pixelStorageModes() {
		return _myPixelStorageModes;
	}

	/** 
	 * Sets whether the texture data is in compressed form. 
	 **/
	public void isDataCompressed(final boolean theIsCompressed) {
		_myIsDataCompressed = theIsCompressed;
	}

	/**
	 * Sets whether the texture coordinates must be flipped vertically for proper display.
	 */
	public void mustFlipVertically(final boolean theMustFlipVertically) {
		_myMustFlipVertically = theMustFlipVertically;
	}

	/**
	 * Returns an estimate of the amount of memory in bytes this TextureData will consume once uploaded to the graphics
	 * card. It should only be treated as an estimate; most applications should not need to query this but instead let
	 * the OpenGL implementation page textures in and out as necessary.
	 */
	public int estimatedMemorySize() {
		return _myEstimatedMemorySize;
	}

	/**
	 * Flushes resources associated with this TextureData by calling Flusher.flush().
	 */
	public void flush() {
		if (_myFlusher != null) {
			_myFlusher.flush();
			_myFlusher = null;
		}
	}
	
	private int unsignedByteToInt(byte b) {
        return (int) b & 0xFF;
    }
	
	/**
	 * Read color from byte buffer 
	 * @param theBuffer
	 * @param theIndex
	 * @return
	 */
	private CCColor colorFromBuffer(ByteBuffer theBuffer, int theIndex, CCColor theStore) {
		for(int i = 0; i < _myPixelFormat.numberOfChannels;i++) {
			_myColorIntChannels[i] = unsignedByteToInt(theBuffer.get(theIndex + _myPixelFormat.offsets[i]));
		}
		return theStore.set(_myColorIntChannels);
	}
	
	/**
	 * Read color from short buffer 
	 * @param theBuffer
	 * @param theIndex
	 * @return
	 */
	private CCColor colorFromBuffer(ShortBuffer theBuffer, int theIndex, CCColor theStore) {
		for(int i = 0; i < _myPixelFormat.numberOfChannels;i++) {
			_myColorIntChannels[i] = theBuffer.get(theIndex + _myPixelFormat.offsets[i]);
		}
		return theStore.set(_myColorIntChannels);
	}
	
	/**
	 * Read color from int buffer 
	 * @param theBuffer
	 * @param theIndex
	 * @return
	 */
	private CCColor colorFromBuffer(IntBuffer theBuffer, int theIndex, CCColor theStore) {
		if(_myPixelType == CCPixelType.UNSIGNED_BYTE) {
			int myValue = theBuffer.get(theIndex / 4);
			switch (_myPixelFormat) {
			case BGRA:
				return theStore.parseFromIntegerBGRA(myValue);
			case RGBA:
				return theStore.parseFromIntegerRGBA(myValue);
			}
			
		}
		for(int i = 0; i < _myPixelFormat.numberOfChannels;i++) {
			_myColorIntChannels[i] = theBuffer.get(theIndex + _myPixelFormat.offsets[i]);
		}
		return theStore.set(_myColorIntChannels);
	}
	
	/**
	 * Read color from int buffer 
	 * @param theBuffer
	 * @param theIndex
	 * @return
	 */
	private CCColor colorFromBuffer(LongBuffer theBuffer, int theIndex, CCColor theStore) {
		for(int i = 0; i < _myPixelFormat.numberOfChannels;i++) {
			_myColorIntChannels[i] = (int)theBuffer.get(theIndex + _myPixelFormat.offsets[i]);
		}
		return theStore.set(_myColorIntChannels);
	}
	
	/**
	 * Read color from int buffer 
	 * @param theBuffer
	 * @param theIndex
	 * @return
	 */
	private CCColor colorFromBuffer(FloatBuffer theBuffer, int theIndex, CCColor theStore) {
		for(int i = 0; i < _myPixelFormat.numberOfChannels;i++) {
			_myColorFloatChannels[i] = theBuffer.get(theIndex + _myPixelFormat.offsets[i]);
		}
		return theStore.set(_myColorFloatChannels);
	}
	
	/**
	 * Read color from int buffer 
	 * @param theBuffer
	 * @param theIndex
	 * @return
	 */
	private CCColor colorFromBuffer(DoubleBuffer theBuffer, int theIndex, CCColor theStore) {
		for(int i = 0; i < _myPixelFormat.numberOfChannels;i++) {
			_myColorFloatChannels[i] = (float)theBuffer.get(theIndex + _myPixelFormat.offsets[i]);
		}
		return theStore.set(_myColorFloatChannels);
	}
	
	/**
	 * Calculates the buffer index based on the pixel position
	 * @param theX
	 * @param theY
	 * @return
	 */
	private int indexFromPixelPos(int theX, int theY) {
		if(_myMustFlipVertically)theY = _myHeight - theY - 1;
		return (theY * _myWidth + theX) * _myPixelFormat.numberOfChannels;
	}
	
	/**
	 * This seems to be way to complex should be tested with more different pixel formats
	 * see cases that are tested so far documented in the code. Be aware before you can
	 * use this method to get the pixel data. You have to call loadPixels() to retrieve
	 * the data from the graphics card.
	 * @param nX
	 * @param nY
	 * @return
	 */
	public CCColor getPixel(final int theX, int theY, CCColor theStore) {
		if(theStore == null){
			theStore = new CCColor();
		}
		if(theX >= _myWidth || theY >= _myHeight || _myBuffer == null){
			theStore.set(0, 0, 0, 0);
			return theStore;
		}
		
        // Calculate The Position In The Texture, Careful Not To Overflow
        int myBufferIndex = indexFromPixelPos(theX, theY);
        
		 _myBuffer[0].rewind();
		 if(_myBuffer[0] instanceof ByteBuffer) {
			 return colorFromBuffer((ByteBuffer)_myBuffer[0], myBufferIndex, theStore);
		 }else if(_myBuffer[0] instanceof ShortBuffer) {
			 return colorFromBuffer((ShortBuffer)_myBuffer[0], myBufferIndex, theStore);
		 }else if(_myBuffer[0] instanceof IntBuffer) {
			 return colorFromBuffer((IntBuffer)_myBuffer[0], myBufferIndex, theStore);
		 }else if(_myBuffer[0] instanceof LongBuffer) {
			 return colorFromBuffer((LongBuffer)_myBuffer[0], myBufferIndex, theStore);
		 }else if(_myBuffer[0] instanceof FloatBuffer) {
			 return colorFromBuffer((FloatBuffer)_myBuffer[0], myBufferIndex, theStore);
		 }else if(_myBuffer[0] instanceof DoubleBuffer) {
			 return colorFromBuffer((DoubleBuffer)_myBuffer[0], myBufferIndex, theStore);
		 }
       
		throw new RuntimeException("Unexpected buffer type " + _myBuffer[0].getClass().getName());
    }
	
	public CCColor getPixel(final int theX, int theY){
		return getPixel(theX, theY, null);
	}
	
	/**
	 * store the channel indices to access color channels dependent on the number of bytes 
	 */
	private static final int[][] color_indices = new int[][] {
		{0},		// one byte red channel
		{0,3},		// two bytes red and alpha channel
		{0,1,2},	// three bytes red, green and blue channel
		{0,1,2,3}	// four bytes red, green, blue and alpha channel
	};
	
	private float colorChannel(CCColor theColor, int theChannel) {
		switch(theChannel) {
		case 0:
			return theColor.r;
		case 1:
			return theColor.g;
		case 2:
			return theColor.b;
		case 3:
			return theColor.a;
		}
		return 0;
	}
	
	/**
	 * Writes a color into a byte buffer
	 * @param theColor
	 * @param theBuffer
	 * @param theIndex
	 */
	private void colorToBuffer(CCColor theColor, ByteBuffer theBuffer, int theIndex) {
		for(int i = 0; i < _myPixelFormat.numberOfChannels;i++) {
			theBuffer.put(
				theIndex + _myPixelFormat.offsets[i], 
				(byte)(colorChannel(theColor, color_indices[_myPixelFormat.numberOfChannels - 1][i])*255)
			);
		}
	}
	
	/**
	 * Writes a color into a short buffer
	 * @param theColor
	 * @param theBuffer
	 * @param theIndex
	 */
	private void colorToBuffer(CCColor theColor, ShortBuffer theBuffer, int theIndex) {
		for(int i = 0; i < _myPixelFormat.numberOfChannels;i++) {
			theBuffer.put(
				theIndex + _myPixelFormat.offsets[i], 
				(short)(colorChannel(theColor, color_indices[_myPixelFormat.numberOfChannels - 1][i])*255)
			);
		}
	}
	
	/**
	 * Writes a color into an int buffer
	 * @param theColor
	 * @param theBuffer
	 * @param theIndex
	 */
	private void colorToBuffer(CCColor theColor, IntBuffer theBuffer, int theIndex) {
		for(int i = 0; i < _myPixelFormat.numberOfChannels;i++) {
			theBuffer.put(
				theIndex + _myPixelFormat.offsets[i], 
				(int)(colorChannel(theColor, color_indices[_myPixelFormat.numberOfChannels - 1][i])*255)
			);
		}
	}
	
	/**
	 * Writes a color into a long buffer
	 * @param theColor
	 * @param theBuffer
	 * @param theIndex
	 */
	private void colorToBuffer(CCColor theColor, LongBuffer theBuffer, int theIndex) {
		for(int i = 0; i < _myPixelFormat.numberOfChannels;i++) {
			theBuffer.put(
				theIndex + _myPixelFormat.offsets[i], 
				(long)(colorChannel(theColor, color_indices[_myPixelFormat.numberOfChannels - 1][i])*255)
			);
		}
	}
	
	/**
	 * Writes a color into a float buffer
	 * @param theColor
	 * @param theBuffer
	 * @param theIndex
	 */
	private void colorToBuffer(CCColor theColor, FloatBuffer theBuffer, int theIndex) {
		for(int i = 0; i < _myPixelFormat.numberOfChannels;i++) {
			theBuffer.put(
				theIndex + _myPixelFormat.offsets[i], 
				colorChannel(theColor, color_indices[_myPixelFormat.numberOfChannels - 1][i])
			);
		}
	}
	
	/**
	 * Writes a color into a double buffer
	 * @param theColor
	 * @param theBuffer
	 * @param theIndex
	 */
	private void colorToBuffer(CCColor theColor, DoubleBuffer theBuffer, int theIndex) {
		for(int i = 0; i < _myPixelFormat.numberOfChannels;i++) {
			theBuffer.put(
				theIndex + _myPixelFormat.offsets[i], 
				colorChannel(theColor, color_indices[_myPixelFormat.numberOfChannels - 1][i])
			);
		}
	}
	
	/**
	 * This seems to be way to complex should be tested with more different pixel formats
	 * see cases that are tested so far documented in the code. Be aware before you can
	 * use this method to get the pixel data. You have to call loadPixels() to retrieve
	 * the data from the graphics card.
	 * @param nX
	 * @param nY
	 * @return
	 */
	public void setPixel(final int theX, int theY, final CCColor theColor) {
		if(theX >= _myWidth || theY >= _myHeight || _myBuffer == null){
			return;
		}
		
        // Calculate The Position In The Texture, Careful Not To Overflow
        int myIndex = indexFromPixelPos(theX, theY);
        
        _myBuffer[0].rewind();
        
        if(_myBuffer[0] instanceof ByteBuffer) {
        	colorToBuffer(theColor, (ByteBuffer)_myBuffer[0], myIndex);
        	return;
        }else if(_myBuffer[0] instanceof ShortBuffer) {
        	colorToBuffer(theColor, (ShortBuffer)_myBuffer[0], myIndex);
        	return;
        }else if(_myBuffer[0] instanceof IntBuffer) {
        	colorToBuffer(theColor, (IntBuffer)_myBuffer[0], myIndex);
        	return;
        }else if(_myBuffer[0] instanceof LongBuffer) {
        	colorToBuffer(theColor, (LongBuffer)_myBuffer[0], myIndex);
        	return;
        }else if(_myBuffer[0] instanceof FloatBuffer) {
        	colorToBuffer(theColor, (FloatBuffer)_myBuffer[0], myIndex);
        	return;
        }else if(_myBuffer[0] instanceof DoubleBuffer) {
        	colorToBuffer(theColor, (DoubleBuffer)_myBuffer[0], myIndex);
        	return;
        }
       
		throw new RuntimeException("Unexpected buffer type " + _myBuffer[0].getClass().getName());
    }
	
	/**
	 * Copies an area from the the source data.
	 * @param theSourceData texture data from which to copy the data
	 * @param theSrcX 
	 * @param theSrcY
	 * @param theDestX
	 * @param theDestY
	 * @param theWidth
	 * @param theHeight
	 */
	public void copy(
		final CCTextureData theSourceData,
		final int theSrcX, final int theSrcY,
		final int theDestX, final int theDestY, 
		final int theWidth, final int theHeight
	) {
		for(int x = 0; x < theWidth; x++) {
			for(int y = 0; y < theHeight; y++) {
				CCColor myColor = getPixel(theSrcX + x, theSrcY + y);
				setPixel(theDestX + x, theDestY + y, myColor);
			}
		}
	}
	
	private void addString(String theString, ByteBuffer theBuffer){
		byte[] myStringBytes = theString.getBytes();
		theBuffer.asIntBuffer().put(myStringBytes.length);
		theBuffer.put(myStringBytes);
	}
	
	private static final int BYTE = 0;
	private static final int SHORT = 1;
	private static final int LONG = 2;
	private static final int INTEGER = 3;
	private static final int FLOAT = 4;
	private static final int DOUBLE = 5;
	
	public void toBytes(ByteBuffer theBuffer){
		theBuffer.asIntBuffer().put(width());
		theBuffer.asIntBuffer().put(height());
		theBuffer.asIntBuffer().put(border());
		
		addString(internalFormat().name(), theBuffer);
		addString(pixelFormat().name(), theBuffer);
		addString(pixelType().name(), theBuffer);

		theBuffer.asIntBuffer().put(isDataCompressed() ? 1 : 0);
		theBuffer.asIntBuffer().put(mustFlipVertically() ? 1 : 0);
		
		if (buffer() instanceof ByteBuffer) {
			theBuffer.asIntBuffer().put(BYTE);
			theBuffer.asIntBuffer().put(buffer().limit());
			theBuffer.put((ByteBuffer) buffer());
		} else if (buffer() instanceof ShortBuffer) {
			theBuffer.asIntBuffer().put(SHORT);
			theBuffer.asIntBuffer().put(buffer().limit());
			theBuffer.asShortBuffer().put((ShortBuffer) buffer());
		} else if (buffer() instanceof IntBuffer) {
			theBuffer.asIntBuffer().put(INTEGER);
			theBuffer.asIntBuffer().put(buffer().limit());
			theBuffer.asIntBuffer().put((IntBuffer) buffer());
		} else if (buffer() instanceof LongBuffer) {
			theBuffer.asIntBuffer().put(LONG);
			theBuffer.asIntBuffer().put(buffer().limit());
			theBuffer.asLongBuffer().put((LongBuffer) buffer());
		} else if (buffer() instanceof FloatBuffer) {
			theBuffer.asIntBuffer().put(FLOAT);
			theBuffer.asIntBuffer().put(buffer().limit());
			theBuffer.asFloatBuffer().put((FloatBuffer) buffer());
		} else if (buffer() instanceof DoubleBuffer) {
			theBuffer.asIntBuffer().put(DOUBLE);
			theBuffer.asIntBuffer().put(buffer().limit());
			theBuffer.asDoubleBuffer().put((DoubleBuffer) buffer());
		}
	}
	
	private String getString(ByteBuffer theBuffer){
		int myStringBytesLength = theBuffer.asIntBuffer().get();
		byte[] myStringBytes = new byte[myStringBytesLength];
		theBuffer.get(myStringBytes);
		return new String(myStringBytes);
	}
	
	public void fromBytes(ByteBuffer theBuffer){
		_myWidth = theBuffer.asIntBuffer().get();
		_myHeight = theBuffer.asIntBuffer().get();
		_myBorder = theBuffer.asIntBuffer().get();
		
		_myPixelInternalFormat = CCPixelInternalFormat.valueOf(getString(theBuffer));
		_myPixelFormat = CCPixelFormat.valueOf(getString(theBuffer));
		_myPixelType = CCPixelType.valueOf(getString(theBuffer));

		_myIsDataCompressed = theBuffer.asIntBuffer().get() == 1;
		_myMustFlipVertically = theBuffer.asIntBuffer().get() == 1;
		
		int myBufferType = theBuffer.asIntBuffer().get();
		int myBufferLimit = theBuffer.asIntBuffer().get();
		
		switch(myBufferType){
		case BYTE:
			ByteBuffer myByteBuffer = ByteBuffer.allocate(myBufferLimit);
			myByteBuffer.put(theBuffer);
			_myBuffer[0] = myByteBuffer;
			break;
		case SHORT:
			ShortBuffer myShortBuffer = ShortBuffer.allocate(myBufferLimit);
			myShortBuffer.put(theBuffer.asShortBuffer());
			_myBuffer[0] = myShortBuffer;
			break;
		case INTEGER:
			IntBuffer myIntBuffer = IntBuffer.allocate(myBufferLimit);
			myIntBuffer.put(theBuffer.asIntBuffer());
			_myBuffer[0] = myIntBuffer;
			break;
		case LONG:
			LongBuffer myLongBuffer = LongBuffer.allocate(myBufferLimit);
			myLongBuffer.put(theBuffer.asLongBuffer());
			_myBuffer[0] = myLongBuffer;
			break;
		case FLOAT:
			FloatBuffer myFloatBuffer = FloatBuffer.allocate(myBufferLimit);
			myFloatBuffer.put(theBuffer.asFloatBuffer());
			_myBuffer[0] = myFloatBuffer;
			break;
		case DOUBLE:
			DoubleBuffer myDoubleBuffer = DoubleBuffer.allocate(myBufferLimit);
			myDoubleBuffer.put(theBuffer.asDoubleBuffer());
			_myBuffer[0] = myDoubleBuffer;
			break;
		}
	}
}
