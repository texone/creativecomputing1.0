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

import java.nio.FloatBuffer;

import javax.media.opengl.GL2;

import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.texture.CCPixelFormat;
import cc.creativecomputing.graphics.texture.CCPixelType;
import cc.creativecomputing.math.CCMath;

/**
 * This class represents a 1D texture. It contains one row of 
 * pixels. This is useful to store gradients, or data that is 
 * passed to a shader.
 * @author christian riekoff
 *
 */
public class CCTexture1D extends CCTexture{


	/**
	 * Creates a new 1D texture
	 */
	public CCTexture1D() {
		super(CCTextureTarget.TEXTURE_1D);
	}

	/**
	 * Creates a new 1D texture
	 * @param theGenerateMipmaps if <code>true</code> textures are automatically
	 * generated from passed texture data
	 */
	public CCTexture1D(final CCTextureAttributes theTextureAttributes) {
		super(CCTextureTarget.TEXTURE_1D, theTextureAttributes);
	}
	
	public CCTexture1D(CCTextureData theData){
		this();
		data(theData);
	}
	
	public void dataImplementation(final CCTextureData theData) {
		if(theData.isDataCompressed()) {
			CCGraphics.currentGL().glCompressedTexImage1D(
				_myTarget.glID, 0, theData.internalFormat().glID, 
				theData.width(), 0, theData.buffer().capacity(), theData.buffer()
			);
		}else {
			CCGraphics.currentGL().glTexImage1D(
				_myTarget.glID, 0, theData.internalFormat().glID,
				theData.width(), 0, 
				theData.pixelFormat().glID, theData.pixelType().glID, theData.buffer()
			);
		}
	}
	
	
	/**
	 * Replaces the content of the texture with pixels from the framebuffer. You can use this method
	 * to copy pixels from the framebuffer to a texture.
	 * @param g
	 * @param theDestX
	 * @param theSrcX
	 * @param theSrcY
	 * @param theWidth
	 */
	public void updateData(final CCGraphics g, int theDestX, int theSrcX, int theSrcY, int theWidth) {
		theSrcX = CCMath.constrain(theSrcX, 0, g.width);
		theSrcY = CCMath.constrain(theSrcY, 0, g.height);
		theDestX = CCMath.constrain(theDestX, 0, _myWidth);
		theWidth = CCMath.min(theWidth, g.width - theSrcX, _myWidth - theDestX);
		
		bind();
		g.gl.glCopyTexSubImage1D(
			_myTarget.glID, 0, 
			theDestX, 
			theSrcX, theSrcY, 
			theWidth
		);
	}
	
	/**
	 * Replaces the texture content with the given data.
	 * @param theData the new pixel values for the texture
	 * @param theDestX the x offset (in pixels) relative to the left side of this texture 
	 * 	where the update will be applied
	 * @param theSrcX the x offset (in pixels) relative to the left side of the supplied 
	 *  TextureData from which to fetch the update rectangle
	 * @param theWidth the width (in pixels) of the rectangle to be updated
	 */
	public void updateData(CCTextureData theData, int theDestX, int theSrcX, int theWidth) {
		theSrcX = CCMath.constrain(theSrcX, 0, theData.width());
		theDestX = CCMath.constrain(theDestX, 0, _myWidth);
		theWidth = CCMath.min(theWidth, theData.width() - theSrcX, _myWidth - theDestX);
			
		bind();
		GL2 gl = CCGraphics.currentGL();
		theData.pixelStorageModes().unpackStorage();

		gl.glPixelStorei(GL2.GL_UNPACK_SKIP_PIXELS, theSrcX);
		
		gl.glTexSubImage1D(
			_myTarget.glID, 0, 
			theDestX, theWidth, 
			theData.pixelFormat().glID, theData.pixelType().glID, theData.buffer()
		);
			
	}

	/**
	 * Replaces the texture content with the given data.
	 * @param theData the new pixel values for the texture
	 * @param theDestX the x offset (in pixels) relative to the left side of this texture 
	 * 	where the update will be applied
	 * @param theWidth the width (in pixels) of the rectangle to be updated
	 */
	public void updateData(CCTextureData theData, final int theDestX, final int theWidth) {
		updateData(theData, theDestX, 0, theWidth);
	}
	
	/**
	 * Replaces the texture content with the given data.
	 * @param theData the new pixel values for the texture
	 */
	public void updateData(CCTextureData theData) {
		updateData(theData,0,0, theData.width());
	}
	
	/**
	 * Sets the pixel at the given index o the given color.
	 * @param theX position of the pixel from the left side
	 * @param theColor the new color of the pixel
	 */
	public void setPixel(final int theX, final CCColor theColor) {
		
		FloatBuffer myBuffer = FloatBuffer.allocate(4);
		myBuffer.put(theColor.red());
		myBuffer.put(theColor.green());
		myBuffer.put(theColor.blue());
		myBuffer.put(theColor.alpha());
		myBuffer.rewind();
		
		GL2 gl = CCGraphics.currentGL();
		gl.glTexSubImage1D(
			_myTarget.glID, 0, 
			theX, 1, 
			CCPixelFormat.RGBA.glID, CCPixelType.FLOAT.glID, myBuffer
		);
	}
	
	/**
	 * Returns the color for the pixel at the given index
	 * @param theX position of the pixel from the left side
	 * @return the color of the pixel
	 */
	public CCColor getPixel(final int theX) {
		FloatBuffer myBuffer = FloatBuffer.allocate(4 * _myWidth);
		GL2 gl = CCGraphics.currentGL();
		gl.glGetTexImage(_myTarget.glID, 0, CCPixelFormat.RGBA.glID, CCPixelType.FLOAT.glID, myBuffer);
		myBuffer.rewind();
		
		return new CCColor(
			myBuffer.get(theX * 4),
			myBuffer.get(theX * 4 + 1),
			myBuffer.get(theX * 4 + 2),
			myBuffer.get(theX * 4 + 3)
		);
	}
}
