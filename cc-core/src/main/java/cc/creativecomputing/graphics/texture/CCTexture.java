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


import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import cc.creativecomputing.CCAppCapabilities;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2i;

/**
 * @author christian riekoff
 *
 */
public abstract class CCTexture{
	
	/**
	 * When textures are compressed OpenGL chooses the most appropriate texture 
	 * compression format. You can use CCTextureCompressionHint to specify whether 
	 * you want OpenGL to choose based on the fastest or highest quality algorithm.
	 * @author christianriekoff
	 *
	 */
	public static enum CCTextureCompressionHint{
		/**
		 * choose fastest algorithm for compression
		 */
		FASTEST(GL.GL_FASTEST),
		/**
		 * choose highest quality algorithm
		 */
		NICEST(GL.GL_NICEST), 
		/**
		 * let opengl decide for the compression algorithm
		 */
		DONT_CARE(GL.GL_DONT_CARE);
        
        public int glID;
		
		private CCTextureCompressionHint(final int theGLID) {
			glID = theGLID;
		}
	}
	
	/**
	 * Normally, you specify texture coordinates between 0.0 and 1.0 to map out a texture. 
	 * If texture coordinates fall outside this range, OpenGL handles them according to the 
	 * current texture wrapping mode. This enum holds the possible modes.
	 * @author christian riekoff
	 *
	 */
	public static enum CCTextureWrap{
		/**
		 * clamps the texture if you use values going over the image range. The needed
		 * texels are taken from the texture border.
		 */
		CLAMP(GL2.GL_CLAMP),
		/**
		 * uses only border texels whenever the texture coordinates fall outside the of the texture.
		 */
		CLAMP_TO_BORDER(GL2.GL_CLAMP_TO_BORDER), 
		/**
		 * simply ignores texel samples that go over the edge and does not include them in the average
		 */
		CLAMP_TO_EDGE(GL.GL_CLAMP_TO_EDGE), 
		/**
		 * works like {@link CCTextureWrap#REPEAT} but mirrors 
		 * the texture for more seamless results on repeating the texture
		 */
		MIRRORED_REPEAT(GL.GL_MIRRORED_REPEAT),
		/**
		 * simply causes the texture to repeat in the direction in which the texture 
		 * coordinate has exceeded the image boundary. The texture repeats again for every multiple
		 * of the texture size. This mode is very useful for applying a small tiled texture to large 
		 * geometric surfaces. Well-done seamless textures can lend the appearance of a seemingly much 
		 * larger texture, but at the cost of a much smaller texture image.
		 */
		REPEAT(GL.GL_REPEAT);
        
        public int glID;
		
		private CCTextureWrap(final int theGLID) {
			glID = theGLID;
		}
	}
	
	public static enum CCTextureFilter{
		/**
		 * Returns the value of the texture element that is nearest 
		 * (in Manhattan distance) to the center of the pixel being textured.
		 */
		NEAREST(GL.GL_NEAREST),

		/**
		 * Returns the weighted average of the four texture elements
		 * that are closest to the center of the pixel being textured.
		 * These can include border texture elements, depending on the 
		 * values of TEXTURE_WRAP, and on the exact mapping.
		 */
        LINEAR(GL.GL_LINEAR);

        public int glID;
		
		private CCTextureFilter(final int theGLID) {
			glID = theGLID;
		}
	}
	
	/**
	 * The texture mipmap filter function is used whenever the pixel being textured
	 * maps to an area greater or smaller than one texture element and mipmap data is defined. 
	 * @author christianriekoff
	 *
	 */
	public static enum CCTextureMipmapFilter{
		/**
         * Chooses the mipmap that most closely matches the size of the pixel 
         * being textured.
         */
        NEAREST,

        /**
         * Chooses the two mipmaps that most closely match the size of the pixel
         * being textured and uses the NEAREST criterion (the texture element 
         * nearest to the center of the pixel) to produce a texture value from 
         * each mipmap. The final texture value is a weighted average of those two values.
         */
        LINEAR;
	}
	
	public static enum CCTextureTarget{
		TEXTURE_1D(GL2.GL_TEXTURE_1D),
		TEXTURE_2D(GL.GL_TEXTURE_2D),
		TEXTURE_3D(GL2.GL_TEXTURE_3D),
		TEXTURE_RECT(GL2.GL_TEXTURE_RECTANGLE_ARB),
		TEXTURE_CUBE_MAP(GL.GL_TEXTURE_CUBE_MAP);
        
        public int glID;
		
		private CCTextureTarget(final int theGLID) {
			glID = theGLID;
		}
	}
	
	/**
	 * The environment mode defines how the colors of the texels are combined 
	 * with the color of the underlying geometry.
	 * @author Christian Riekoff
	 *
	 */
	public static enum CCTextureEnvironmentMode{ 
		/**
		 * Texel color values are multiplied by the geometry fragment color values.
		 */
		MODULATE(GL2.GL_MODULATE), 
		/**
		 * Texel values are applied to geometry fragment values. If blending 
		 * is enabled and the texture contains an alpha channel, the geometry 
		 * blends through the texture according to the current blend function.
		 */
		DECAL(GL2.GL_DECAL),
		/**
		 * Texel values replace geometry fragment values. If blending is enabled 
		 * and the texture contains an alpha channel, the texture's alpha values 
		 * are used to replace the geometry fragment colors in the color buffer.
		 */
		REPLACE(GL.GL_REPLACE),
		/**
		 * Texel color values are added to the geometry color values.
		 */
		ADD(GL2.GL_ADD), 
		/**
		 * Texel color values are multiplied by the texture environment color.
		 */
		BLEND(GL.GL_BLEND), 
		/**
		 * Texel color values are combined with a second texture unit according 
		 * to the texture combine function.
		 */
		COMBINE(GL2.GL_COMBINE);
		
		private final int glID;
		
		private CCTextureEnvironmentMode(final int theGlID){
			glID = theGlID;
		}
	}
	
	/**
	 * Specifies a single symbolic constant indicating how depth values should be 
	 * treated during filtering and texture application.
	 * @author christianriekoff
	 *
	 */
	public static enum CCDepthTextureMode{
		LUMINANCE(GL.GL_LUMINANCE), 
		INTENSITY(GL2.GL_INTENSITY),
		ALPHA(GL.GL_ALPHA);
		
		private final int glID;
		
		private CCDepthTextureMode(final int theGlID){
			glID = theGlID;
		}
	}

	protected CCTextureTarget _myTarget;
	protected CCTextureEnvironmentMode _myEnvironmentMode;
	
	protected CCTextureFilter _myTextureFilter;
	protected CCTextureMipmapFilter _myTextureMipmapFilter;
	
	protected CCColor _myBlendColor = new CCColor();
	
	protected CCPixelInternalFormat _myInternalFormat;
	protected CCPixelFormat _myFormat;
	protected CCPixelType _myPixelType;
	
	protected CCPixelStorageModes _myStorageModes;
	
	protected int[] _myTextureIDs;
	protected int _myTextureID;
	
	protected int _myWidth;
	protected int _myHeight;
	protected int _myDepth;
	
	protected int _myEstimatedMemorySize = 0;
	
	protected boolean _myMustFlipVertically;
	
	/**
	 * indicates whether mipmaps should be generated
	 * (ignored if mipmaps are supplied from the file)
	 */
	protected boolean _myGenerateMipmaps;
	
	/**
	 * indicates whether mipmaps are not only present
	 * but should also be used
	 */
	private boolean _myHasMipmaps;
	
	/**
	 * indicates whether the data of a texture is compressed
	 */
	protected boolean _myIsCompressed = false;
	
	/**
	 * Creates a new texture for the specified target. This object can also
	 * contain multiple textures to create texture sequences and do multitexturing.
	 * @param theTarget
	 * @param theGenerateMipmaps if <code>true</code> this automatically generate mipmaps when texture data is passed
	 * @param theNumberOfTextures number of textures to create
	 */
	public CCTexture(final CCTextureTarget theTarget, final CCTextureAttributes theAttributes, final int theNumberOfTextures) {
		_myTarget = theTarget;
		_myEnvironmentMode = CCTextureEnvironmentMode.MODULATE;
		_myTextureIDs = createTextureIds(theNumberOfTextures);
		_myTextureID = 0;
		
		_myWidth = 0;
		_myHeight = 1;
		_myDepth = 1;
		
		_myInternalFormat = theAttributes.internalFormat();
		_myFormat = theAttributes.format();
		_myPixelType = theAttributes.pixelType();
		
		textureFilter(theAttributes.filter());
		wrapS(theAttributes.wrapS());
		wrapT(theAttributes.wrapT());
		
		generateMipmaps(theAttributes.generateMipmaps());
	}
	
	public CCTexture(final CCTextureTarget theTarget, final CCTextureAttributes theAttributes) {
		this(theTarget, theAttributes, 1);
	}
	
	public CCTexture(final CCTextureTarget theTarget) {
		this(theTarget, new CCTextureAttributes());
	}
	
	public CCTexture(final CCTextureTarget theTarget, int theID ){
		_myTarget = theTarget;
		_myTextureIDs = new int[]{theID};
		_myTextureID = 0;

		_myEnvironmentMode = CCTextureEnvironmentMode.MODULATE;
		_myWidth = texLevelParameteri(GL2.GL_TEXTURE_WIDTH);
		_myHeight = texLevelParameteri(GL2.GL_TEXTURE_HEIGHT);
		_myDepth = texLevelParameteri(GL2.GL_TEXTURE_DEPTH);
		
//		_myInternalFormat = texLevelParameteri(GL2.GL_TEXTURE_DEPTH);
//		CCGraphics.currentGL().glBindTexture(theTarget.glID, _myTextureID);
//		CCGraphics.currentGL().glGetTexLevelParameteriv(theTarget.glID, 0, GL2.GL_TEXTURE_WIDTH, myResult);
//		CCLog.info(myResult.get(0));
//		CCGraphics.currentGL().glBindTexture(GL2.GL_TEXTURE_RECTANGLE, 0);
		
	}
	
	public int texLevelParameteri(int theParameter){
		IntBuffer myResult = IntBuffer.allocate(1);
		CCGraphics.currentGL().glBindTexture(_myTarget.glID, id());
		CCGraphics.currentGL().glGetTexLevelParameteriv(_myTarget.glID, 0, theParameter, myResult);
		CCGraphics.currentGL().glBindTexture(_myTarget.glID, 0);
		return myResult.get(0);
	}

	/**
	 * Sets whether mipmaps should be generated for the texture data. 
	 * @param theGenerateMipmaps indicates whether mipmaps should be autogenerated for the resulting texture. 
	 * 		  Currently if generateMipmaps is true then dataIsCompressed may not be true.
	 */
	public void generateMipmaps(final boolean theGenerateMipmaps) {
		_myGenerateMipmaps = theGenerateMipmaps;
		_myHasMipmaps = theGenerateMipmaps;
		_myStorageModes = new CCPixelStorageModes();
		_myStorageModes.alignment(1);
		
		GL2 gl = CCGraphics.currentGL();
		if(_myGenerateMipmaps)gl.glGenerateMipmap(_myTarget.glID);
	}

	/** 
	 * Returns whether mipmaps should be generated for the texture data. 
	 **/
	public boolean generateMipmaps() {
		return _myGenerateMipmaps;
	}
	
	/**
	 * Returns whether the data of this texture is compressed.
	 * @return <code>true</code> if the texture is compressed
	 */
	public boolean isCompressed() {
		return _myIsCompressed;
	}
	
	public abstract void dataImplementation(CCTextureData theData);
		
	
	/**
	 * Sets or resets the data for the texture. Be aware that this method is quiet
	 * expensive. It should only be called for initialization or to totally reset a texture,
	 * meaning to also change its size.
	 * @param theData texture information
	 */
	public void data(CCTextureData theData) {
		_myMustFlipVertically = theData.mustFlipVertically();
		
		_myInternalFormat = theData.internalFormat();
		_myFormat = theData.pixelFormat();
		_myPixelType = theData.pixelType();
		_myStorageModes = theData.pixelStorageModes();
		
		_myWidth = theData.width();
		_myHeight = theData.height();
		
		bind();

		GL2 gl = CCGraphics.currentGL();
		theData.pixelStorageModes().unpackStorage();
		dataImplementation(theData);
		theData.pixelStorageModes().defaultUnpackStorage();
		
		textureFilter(CCTextureFilter.LINEAR);
		
		if(_myGenerateMipmaps)gl.glGenerateMipmap(_myTarget.glID);
	}
	
	/**
	 * Returns a texture image into img. 
	 * @param theLevel specifies the level-of-detail number of the desired image
	 * @return data of the texture
	 */
	public ByteBuffer dataBuffer(int theLevel) {
		GL2 gl = CCGraphics.currentGL();
		size();
		ByteBuffer myBuffer = ByteBuffer.allocate(size() * _myPixelType.bytesPerChannel * 4);
		gl.glGetTexImage(_myTarget.glID, theLevel, _myFormat.glID, _myPixelType.glID, myBuffer);
		myBuffer.rewind();
		return myBuffer;
	}
	
	/**
	 * Sets or resets the data for the texture. Different from the normal data method, calling
	 * <code>compressData()</code> uses OPENGLs internal texture compression. As a result textures
	 * use far less memory on the graphics card. Dependent on the texture data's internal pixel 
	 * format this method might fail. To check if texture compression was successful this method
	 * returns a boolean value which is <code>true</code> in case that the compression was successful
	 * or <code>false</code> otherwise. Be aware that this method is quiet
	 * expensive. It should only be called for initialization or to totally reset a texture,
	 * meaning to also change its size.
	 * @param theData texture information
	 * @param theHint specify whether texture compression uses the fastest or highest quality algorithm
	 * @return <code>true</code> in case that the compression was successful or <code>false</code> otherwise
	 */
	public boolean compressData(final CCTextureCompressionHint theHint, final CCTextureData theData) {
		_myMustFlipVertically = theData.mustFlipVertically();
		
		_myWidth = theData.width();
		_myHeight = theData.height();
		
		_myInternalFormat = theData.internalFormat();
		_myFormat = theData.pixelFormat();
		
		switch(_myInternalFormat) {
		case RGB:
		case RGB4:
		case RGB5:
		case RGB8:
		case RGB10:
		case RGB12:
		case RGB16:
			_myInternalFormat = CCPixelInternalFormat.COMPRESSED_RGB;
			break;
		case RGBA:
		case RGBA2:
		case RGBA4:
		case RGBA8:
		case RGBA12:
		case RGBA16:
		case RGB5_A1:
		case RGB10_A2:
			_myInternalFormat = CCPixelInternalFormat.COMPRESSED_RGBA;
			break;
		default:
		}
		
		bind();
		GL2 gl = CCGraphics.currentGL();
		gl.glHint(GL2.GL_TEXTURE_COMPRESSION_HINT, theHint.glID);
		theData.pixelStorageModes().unpackStorage();
		switch(_myTarget) {
		case TEXTURE_1D:
			gl.glTexImage1D(
				_myTarget.glID, 0, _myInternalFormat.glID,
				theData.width(), 0, 
				theData.pixelFormat().glID, theData.pixelType().glID, theData.buffer()
			);
			break;
		case TEXTURE_2D:
		case TEXTURE_RECT:
			gl.glTexImage2D(
				_myTarget.glID, 0, _myInternalFormat.glID, 
				theData.width(), theData.height(), 0, 
				theData.pixelFormat().glID, theData.pixelType().glID, theData.buffer()
			);
			break;
		default:
		}
		theData.pixelStorageModes().defaultUnpackStorage();
		
//		/* if the compression has been successful */
//		if (compressed == GL_TRUE)
//		{
//		 glGetTexLevelParameteriv(GL_TEXTURE_2D, 0, GL_TEXTURE_INTERNAL_FORMAT, 
//		&internalformat);
//		 glGetTexLevelParameteriv(GL_TEXTURE_2D, 0, GL_TEXTURE_COMPRESSED_IMAGE_SIZE_ARB, 
//		&compressed_size);
//		 img = (unsigned char *)malloc(compressed_size * sizeof(unsigned char));
//		 glGetCompressedTexImageARB(GL_TEXTURE_2D, 0, img);
//		 SaveTexture(width, height, compressed_size, img, internalFormat, 0);
//		}

		
		textureFilter(CCTextureFilter.LINEAR);
		
		if(_myGenerateMipmaps)gl.glGenerateMipmap(_myTarget.glID);
		
		int[] myData = new int[1];
		
		gl.glGetTexLevelParameteriv(_myTarget.glID, 0, GL2.GL_TEXTURE_COMPRESSED, myData, 0);
		
		boolean myResult = myData[0] > 0;
		
		/* if the compression has been successful */
		if(myResult) {
			gl.glGetTexLevelParameteriv(_myTarget.glID, 0, GL2.GL_TEXTURE_COMPRESSED_IMAGE_SIZE, myData, 0);
			_myEstimatedMemorySize = myData[0];
			gl.glGetTexLevelParameteriv(_myTarget.glID, 0, GL2.GL_TEXTURE_INTERNAL_FORMAT, myData, 0);
			_myInternalFormat = CCPixelInternalFormat.valueOf(myData[0]);
			_myIsCompressed = true;
		}
		
		return myResult;
	}
	
	public int estimatedMemorySize(){
		return _myEstimatedMemorySize;
	}
	
	public boolean compressData(final CCTextureData theData) {
		return compressData(CCTextureCompressionHint.DONT_CARE, theData);
	}
	
	public abstract void updateData(final CCTextureData theData);
	
	protected int[] createTextureIds(final int theNumberOfIds) {
		GL gl = CCGraphics.currentGL();
		int[] tmp = new int[theNumberOfIds];
		gl.glGenTextures(theNumberOfIds, tmp, 0);
		return tmp;
	}
	
	public void bind() {
		bind(_myTextureID);
	}
	
	public void bind(final int theID) {
		GL2 gl = CCGraphics.currentGL();
		gl.glBindTexture(_myTarget.glID, _myTextureIDs[theID]);
		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, _myEnvironmentMode.glID);
		
		if(_myEnvironmentMode == CCTextureEnvironmentMode.BLEND) {
			gl.glTexEnvfv(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_COLOR, _myBlendColor.array(),0);
		}
	}
	
	public void unbind() {
		GL2 gl = CCGraphics.currentGL();
		gl.glBindTexture(_myTarget.glID, 0);
	}
	
	public int id() {
		return _myTextureIDs[_myTextureID];
	}
	
	public int id(final int theLevel) {
		return _myTextureIDs[theLevel];
	}
	
	@Override
	public void finalize() {
		CCGraphics.currentGL().glDeleteTextures(_myTextureIDs.length, _myTextureIDs, 0);
	}
	
	/**
	 * Returns the target of the texture can be 1D, 2D, 3D, RECT and CUBEMAP.
	 * @return target of the texture
	 */
	public CCTextureTarget target() {
		return _myTarget;
	}
	
	/**
	 * Indicates whether the texture coordinates must be flipped vertically in 
	 * order to properly display the texture. This is handled automatically by 
	 * {@link CCGraphics#texture(cc.creativecomputing.graphics.texture.CCAbstractTexture) texture()} in
	 * {@link CCGraphics} by setting a texture transform, but applications may generate or otherwise
	 * produce texture coordinates which must be corrected.
	 *
	 * @return
	 */
	public boolean mustFlipVertically() {
		return _myMustFlipVertically;
	}
	
	/**
	 * Indicates whether the texture coordinates must be flipped vertically in 
	 * order to properly display the texture. This is handled automatically by 
	 * {@link CCGraphics#texture(cc.creativecomputing.graphics.texture.CCAbstractTexture) texture()} in
	 * {@link CCGraphics} by setting a texture transform, but applications may generate or otherwise
	 * produce texture coordinates which must be corrected.
	 *
	 * @param theMustFlipVertically true if the texture must be flipped otherwise false
	 */
	public void mustFlipVertically(boolean theMustFlipVertically) {
		_myMustFlipVertically = theMustFlipVertically;
	}
	
	/** 
	 * Returns the width of the texture.
	 *
	 * @return the width of the texture
	 */
	public int width() {
		return _myWidth;
	}

	/**
	 * Returns the height of the texture. For 1D textures
	 * this value is 1.
	 *
	 * @return the height of the texture
	 */
	public int height() {
		return _myHeight;
	}
	
	/**
	 * Returns the number of pixels of this texture.
	 * @return the number of pixels
	 */
	public int size() {
		return _myWidth * _myHeight * _myDepth;
	}
	
	/**
	 * Returns the size of the texture in pixels
	 * @return
	 */
	public CCVector2i dimension() {
		return new CCVector2i(_myWidth, _myHeight);
	}
	
	/**
	 * Returns the depth of the texture. This makes only 
	 * sense for 3D texture. For all others this value is 1.
	 * 
	 * @return the depth of the texture
	 */
	public int depth() {
		return _myDepth;
	}
	
	public int border() {
		return 0;
	}
	
	/**
	 * 
	 * @return
	 */
	public CCPixelFormat format() {
		return _myFormat;
	}
	
	/**
	 * 
	 * @return
	 */
	public CCPixelInternalFormat internalFormat() {
		return _myInternalFormat;
	}
	
	
	public CCPixelType pixelType() {
		return _myPixelType;
	}
	
	/**
	 * Shortcut to set a texture parameter only used internally
	 * @param theType the parameter type we want to change
	 * @param theValue the value for the parameter
	 */
	protected void parameter(final int theType, final int theValue) {
		if(_myTextureIDs == null)return;
		for(int i = 0; i < _myTextureIDs.length;i++) {
			bind(i);
			CCGraphics.currentGL().glTexParameteri(_myTarget.glID, theType, theValue);
		}
	}
	
	/**
	 * Shortcut to set a texture parameter only used internally
	 * @param theType the parameter type we want to change
	 * @param theValue the value for the parameter
	 */
	protected void parameter(final int theType, final float theValue) {
		for(int i = 0; i < _myTextureIDs.length;i++) {
			bind(i);
			CCGraphics.currentGL().glTexParameterf(_myTarget.glID, theType, theValue);
		}
	}
	
	
	/**
	 * Shortcut to set a texture parameter only used internally
	 * @param theType the parameter type we want to change
	 * @param theValue the value for the parameter
	 */
	protected void parameter(final int theType, final float[] theValues) {
		for(int i = 0; i < _myTextureIDs.length;i++) {
			bind(i);
			CCGraphics.currentGL().glTexParameterfv(_myTarget.glID, theType, theValues,0);
		}
	}
	
	/**
	 * Shortcut to get a texture parameter
	 * @param theGLID the gl id of the parameter to get
	 * @return the value for the given parameter
	 */
	protected int parameter(final int theGLID) {
		int[] myResult = new int[1];
		CCGraphics.currentGL().glGetTexLevelParameteriv(_myTarget.glID, 0, theGLID, myResult, 0);
		return myResult[0];
	}
	
	/**
	 * Normally, you specify texture coordinates between 0.0 and 1.0 to map out a texture. 
	 * If texture coordinates fall outside this range, OpenGL handles them according to the 
	 * current texture wrapping mode. Using this method you can set the wrap mode for each coordinate 
	 * individually. The wrap mode can then be set to one of the following values:
	 * <ul>
	 * <li>{@link CCTextureWrap#REPEAT} simply causes the texture to repeat in the direction in which the texture 
	 * coordinate has exceeded the image boundary. The texture repeats again for every multiple
	 * of the texture size. This mode is very useful for applying a small tiled texture to large 
	 * geometric surfaces. Well-done seamless textures can lend the appearance of a seemingly much 
	 * larger texture, but at the cost of a much smaller texture image.</li>
	 * <li>{@link CCTextureWrap#MIRRORED_REPEAT} works like {@link CCTextureWrap#REPEAT} but mirrors 
	 * the texture for more seamless results on repeating the texture</li>
	 * <li>{@link CCTextureWrap#CLAMP} clamps the texture if you use values going over the image range. The needed
	 * texels are taken from the texture border.
	 * results on repeating the texture</li>
	 * <li>{@link CCTextureWrap#CLAMP_TO_EDGE} simply ignores texel samples that go over the edge and does not include them in the average</li>
	 * <li>{@link CCTextureWrap#CLAMP_TO_BORDER} uses only border texels whenever the texture coordinates fall outside the of the texture.</li>
	 * </ul>
	 * @param theTextureWrap mode for texture wrapping 
	 */
	public void wrap(final CCTextureWrap theTextureWrap){
		parameter(GL2.GL_TEXTURE_WRAP_R, theTextureWrap.glID);
		parameter(GL.GL_TEXTURE_WRAP_S, theTextureWrap.glID);
		parameter(GL.GL_TEXTURE_WRAP_T, theTextureWrap.glID);
	}
	
	public void wrapR(final CCTextureWrap theTextureWrap){
		parameter(GL2.GL_TEXTURE_WRAP_R, theTextureWrap.glID);
	}
	
	/**
	 * Sets the horizontal wrapping behavior when a texture coordinate falls outside the range of [0,1].
	 * @see #wrap(CCTextureWrap)
	 * @param theTextureWrap
	 */
	public void wrapS(final CCTextureWrap theTextureWrap){
		parameter(GL.GL_TEXTURE_WRAP_S, theTextureWrap.glID);
	}
	
	/**
	 * 
	 * @param theTextureWrap
	 */
	public void wrapT(final CCTextureWrap theTextureWrap) {
		parameter(GL.GL_TEXTURE_WRAP_T, theTextureWrap.glID);
	}
	
	public void textureBorderColor(final CCColor theColor) {
		parameter(GL2.GL_TEXTURE_BORDER_COLOR, theColor.array());
	}
	
	private void updateFilter() {
		// set mag filter first as this has no impact on mipmapping
		parameter(GL.GL_TEXTURE_MAG_FILTER, _myTextureFilter.glID);
			
		if(!_myHasMipmaps) {
			parameter(GL.GL_TEXTURE_MIN_FILTER, _myTextureFilter.glID);
			return;
		}
			
		if(_myTextureFilter == CCTextureFilter.NEAREST) {
			if(_myTextureMipmapFilter == CCTextureMipmapFilter.NEAREST) {
				parameter(GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST_MIPMAP_NEAREST);
			}else {
				parameter(GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST_MIPMAP_LINEAR);
			}
		}else {
			if(_myTextureMipmapFilter == CCTextureMipmapFilter.NEAREST) {
				parameter(GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR_MIPMAP_NEAREST);
			}else {
				parameter(GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR);
			}
		}
	}
	
	/**
	 * Applies anisotropic filtering to the texture. THis is helpful to avoid
	 * blurring in more oblique angle of the geometry to the view. You have to 
	 * provide an amount between 0 and 1 be aware that the maximum amount of filtering
	 * is dependent on the current hardware. So 0 stands for no anisotropic filtering
	 * 1 for the maximum available amount.
	 * @param theAmount the amount of filtering number between 0 and 1
	 */
	public void anisotropicFiltering(final float theAmount) {
		parameter(GL.GL_TEXTURE_MAX_ANISOTROPY_EXT, CCMath.blend(1, CCAppCapabilities.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, theAmount));
	}
	
	/**
	 * <p>
	 * The texture filter function is used whenever the pixel being textured
	 * maps to an area greater or smaller than one texture element. There are two 
	 * defined filter functions. 
	 * <ul>
	 * <li>{@link CCTextureFilter#NEAREST} uses the nearest pixel</li>
	 * <li>{@link CCTextureFilter#LINEAR} uses the nearest four texture elements to compute the texture value.</li>
	 * </ul>
	 * </p>
	 * The default is {@link CCTextureFilter#LINEAR}.       
	 * @param theFilter
	 */
	public void textureFilter(final CCTextureFilter theFilter){
		_myTextureFilter = theFilter;
		updateFilter();
	}
	
	/**
	 * <p>
	 * The texture mipmap filter function is used whenever the pixel being textured
	 * maps to an area greater or smaller than one texture element and mipmap data is defined. 
	 * </p>
	 * <p>A mipmap is an ordered set of arrays representing the same image at 
	 * progressively lower resolutions. If the texture has dimensions 2n x 2m,
	 * there are max(n,m) + 1 mipmaps.</p>
	 * <ul>
	 * <li><code>NEAREST</code> uses the nearest mipmap</li>
	 * <li><code>LINEAR</code> uses the nearest two mipmaps to compute the texture value.</li>
	 * </ul>
	 * The default is <code>NEAREST</code>.
	 *              
	 * @param theFilter
	 */
	public void textureMipmapFilter(final CCTextureMipmapFilter theFilter){
		_myTextureMipmapFilter = theFilter;
		updateFilter();
	}
	
	/**
	 * Defines how colors from the texels are combined with the color of the
	 * underlying geometry.
	 * @param theMode
	 */
	public void textureEnvironmentMode(final CCTextureEnvironmentMode theMode) {
		_myEnvironmentMode = theMode;
	}
	
	/**
	 * Specifies a single symbolic constant indicating how depth values should be 
	 * treated during filtering and texture application.
	 * @param theMode
	 */
	public void depthTextureMode(final CCDepthTextureMode theMode) {
		parameter(GL2.GL_DEPTH_TEXTURE_MODE, theMode.glID);
	}
	
	/**
	 * Textures can also be blended with a constant blending color using the 
	 * <code>BLEND</code> texture environment mode. If you set this environment mode, 
	 * you must also set the texture environment color with this method.
	 * @param theBlendColor blend color for the blend mode
	 */
	public void blendColor(final CCColor theBlendColor) {
		_myBlendColor = theBlendColor;
	}
	
	
}
