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
package cc.creativecomputing.graphics.shader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCPBO;
import cc.creativecomputing.graphics.texture.CCFrameBufferObject;
import cc.creativecomputing.graphics.texture.CCFrameBufferObjectAttributes;
import cc.creativecomputing.graphics.texture.CCPixelFormat;
import cc.creativecomputing.graphics.texture.CCPixelInternalFormat;
import cc.creativecomputing.graphics.texture.CCPixelType;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.graphics.texture.CCTextureAttributes;
import cc.creativecomputing.math.CCAABoundingRectangle;

/**
 * @author christianriekoff
 *
 */
public class CCShaderBuffer extends CCFrameBufferObject{
	
	private static CCFrameBufferObjectAttributes floatAttributes(
		final int theNumberOfBits, 
		final int theNumberOfChannels, 
		final int theNumberOfTextures
	) {
		CCTextureAttributes myTextureAttributes = new CCTextureAttributes();
		myTextureAttributes.pixelType(CCPixelType.FLOAT);
		myTextureAttributes.wrap(CCTextureWrap.CLAMP);
		myTextureAttributes.filter(CCTextureFilter.NEAREST);
		
		boolean myIs16Bit;
		
		switch(theNumberOfBits){
		case 16:
			myIs16Bit = true;
			break;
		case 32:
			myIs16Bit = false;
			break;
		default:
			throw new CCShaderException("The given number of bits is not supported. You can only create shader textures with 16 or 32 bit resolution.");
		}
		
		boolean _myIsNvidia = CCGraphics.currentGL().glGetString(GL.GL_VENDOR).startsWith("NVIDIA");
		
		CCPixelFormat _myFormat;
		CCPixelInternalFormat _myInternalFormat;
		
		switch(theNumberOfChannels){
		case 1:
			if(_myIsNvidia) {
				_myInternalFormat = myIs16Bit ? CCPixelInternalFormat.FLOAT_R16_NV : CCPixelInternalFormat.FLOAT_R32_NV;
			} else {
				_myInternalFormat = myIs16Bit ? CCPixelInternalFormat.LUMINANCE_FLOAT16_ATI : CCPixelInternalFormat.LUMINANCE_FLOAT32_ATI;
			}
			_myFormat = CCPixelFormat.LUMINANCE;
			break;
		case 2:
			if(_myIsNvidia) {
				_myInternalFormat = myIs16Bit ? CCPixelInternalFormat.FLOAT_RG16_NV : CCPixelInternalFormat.FLOAT_RG32_NV;
			} else {
				_myInternalFormat = myIs16Bit ? CCPixelInternalFormat.LUMINANCE_ALPHA_FLOAT16_ATI : CCPixelInternalFormat.LUMINANCE_ALPHA_FLOAT32_ATI;
			}
			_myFormat = CCPixelFormat.LUMINANCE_ALPHA;
			break;
		case 3:
			_myInternalFormat = myIs16Bit ? CCPixelInternalFormat.RGB16F : CCPixelInternalFormat.RGB32F;
			_myFormat = CCPixelFormat.RGB;
			break;
		case 4:
			_myInternalFormat = myIs16Bit ? CCPixelInternalFormat.RGBA16F : CCPixelInternalFormat.RGBA32F;
			_myFormat = CCPixelFormat.RGBA;
			break;
		default:
			throw new CCShaderException("The given number of channels is not supported. You can only create shader textures with 1,2,3 or 4 channels.");
		
		}
		
		myTextureAttributes.internalFormat(_myInternalFormat);
		myTextureAttributes.format(_myFormat);
		
		CCFrameBufferObjectAttributes myAttributes = new CCFrameBufferObjectAttributes(myTextureAttributes, theNumberOfTextures);
		myAttributes.enableDepthBuffer(false);
		return myAttributes;
	}
	
	private static CCFrameBufferObjectAttributes intAttributes(
		final int theNumberOfBits, 
		final int theNumberOfChannels, 
		final int theNumberOfTextures
	) {
		CCTextureAttributes myTextureAttributes = new CCTextureAttributes();
		myTextureAttributes.pixelType(CCPixelType.INT);
		myTextureAttributes.wrap(CCTextureWrap.CLAMP);
		myTextureAttributes.filter(CCTextureFilter.NEAREST);
			
		boolean myIs16Bit;
		
			switch(theNumberOfBits){
			case 16:
				myIs16Bit = true;
				break;
			case 32:
				myIs16Bit = false;
				break;
			default:
				throw new CCShaderException("The given number of bits is not supported. You can only create shader textures with 16 or 32 bit resolution.");
			}
			
			CCPixelFormat _myFormat;
			CCPixelInternalFormat _myInternalFormat;
			
			switch(theNumberOfChannels){
			case 1:
				_myInternalFormat = CCPixelInternalFormat.LUMINANCE;
				_myFormat = CCPixelFormat.LUMINANCE_INTEGER;
				break;
			case 2:
				_myInternalFormat = CCPixelInternalFormat.LUMINANCE_ALPHA;;
				_myFormat = CCPixelFormat.LUMINANCE_ALPHA_INTEGER;
				break;
			case 3:
				_myInternalFormat = myIs16Bit ? CCPixelInternalFormat.RGB16I : CCPixelInternalFormat.RGB32I;
				_myFormat = CCPixelFormat.RGB_INTEGER;
				break;
			case 4:
				_myInternalFormat = myIs16Bit ? CCPixelInternalFormat.RGBA16I : CCPixelInternalFormat.RGBA32I;
				_myFormat = CCPixelFormat.RGBA_INTEGER;
				break;
			default:
				throw new CCShaderException("The given number of channels is not supported. You can only create shader textures with 1,2,3 or 4 channels.");
			
			}
			
			myTextureAttributes.internalFormat(_myInternalFormat);
			myTextureAttributes.format(_myFormat);
			
			CCFrameBufferObjectAttributes myAttributes = new CCFrameBufferObjectAttributes(myTextureAttributes, theNumberOfTextures);
			myAttributes.enableDepthBuffer(false);
			return myAttributes;
		}
	
	private static CCFrameBufferObjectAttributes attributes(
		final CCPixelType theType,
		final int theNumberOfBits, 
		final int theNumberOfChannels, 
		final int theNumberOfTextures
	){
		switch(theType){
		case INT:
			return intAttributes(theNumberOfBits, theNumberOfChannels, theNumberOfTextures);
		case FLOAT:
			return floatAttributes(theNumberOfBits, theNumberOfChannels, theNumberOfTextures);
		default:
			throw new CCFrameBufferObjectException("Unsupported pixeltype " + theType);
		}
	}
	
	private CCPBO[] _myPBO = new CCPBO[2];
	private int _myNumberOfChannels;
	private int _myNumberOfBits;
	
	public CCShaderBuffer(
		final int theNumberOfBits, 
		final int theNumberOfChannels, 
		final int theNumberOfTextures, 
		final int theWidth, 
		final int theHeight, 
		final CCTextureTarget theTarget
	){
		this(
			CCPixelType.FLOAT,
			theNumberOfBits,
			theNumberOfChannels,
			theNumberOfTextures,
			theWidth,
			theHeight,
			theTarget
		);
	}
	
	public CCShaderBuffer(
		final CCPixelType theType,
		final int theNumberOfBits, 
		final int theNumberOfChannels, 
		final int theNumberOfTextures, 
		final int theWidth, 
		final int theHeight, final CCTextureTarget theTarget
	){
		super(theTarget, attributes(theType, theNumberOfBits,theNumberOfChannels,theNumberOfTextures), theWidth, theHeight);
			
		_myNumberOfChannels = theNumberOfChannels;
		_myNumberOfBits = theNumberOfBits;
			
//			clear();
	}
	
	public CCShaderBuffer(
		final int theNumberOfBits, 
		final int theNumberOfChannels, final int theNumberOfTextures, 
		final int theWidth,
		final int theHeight
	){
		this(theNumberOfBits,theNumberOfChannels,theNumberOfTextures,theWidth,theHeight,CCTextureTarget.TEXTURE_RECT);
	}
	
	public CCShaderBuffer(
		final int theNumberOfBits, 
		final int theNumberOfChannels, final int theWidth, 
		final int theHeight, final CCTextureTarget theTarget
	){
		this(theNumberOfBits,theNumberOfChannels,1,theWidth,theHeight,theTarget);
	}
	
	public CCShaderBuffer(final int theWidth, final int theHeight, final CCTextureTarget theTarget){
		this(32,3,theWidth,theHeight,theTarget);
	}
	
	public CCShaderBuffer(
		final int theNumberOfBits, 
		final int theNumberOfChannels, 
		final int theWidth, final int theHeight
	){
		this(theNumberOfBits,theNumberOfChannels,1,theWidth,theHeight,CCTextureTarget.TEXTURE_RECT);
	}
	
	public CCShaderBuffer(final int theWidth, final int theHeight){
		this(32,3,theWidth,theHeight,CCTextureTarget.TEXTURE_RECT);
	}
	
	public int numberOfChannels() {
		return _myNumberOfChannels;
	}
	
	public int numberOfBits() {
		return _myNumberOfBits;
	}
	
	public void beginOrtho2D(){
		GL2 gl = CCGraphics.currentGL();
		gl.glPushAttrib(GL2.GL_VIEWPORT_BIT);
		gl.glViewport(0, 0, _myWidth, _myHeight);

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glOrtho(0, _myWidth, 0, _myHeight,-1,1);

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glLoadIdentity();
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.graphics.texture.CCFrameBufferObject#beginDraw()
	 */
	@Override
	public void beginDraw() {
		bindFBO();
		beginOrtho2D();
	}
	
	public void beginDraw(int theAttachment) {
		bindFBO(theAttachment);
		beginOrtho2D();
	}

	public void endOrtho2D(){
		GL2 gl = CCGraphics.currentGL();
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glPopMatrix();

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPopMatrix();

		gl.glPopAttrib();
	}
	
	public void endDraw(){
		endOrtho2D();
		releaseFBO();
	}
	
	public void draw(){
		beginDraw();
	
		GL2 gl = CCGraphics.currentGL();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);
		drawQuad();
		
		endDraw();
	}
	
	public void draw(int theAttachmentID){
		beginDraw(theAttachmentID);
	
		GL2 gl = CCGraphics.currentGL();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);
		drawQuad();
		
		endDraw();
	}
	
	public void drawQuad() {

		GL2 gl = CCGraphics.currentGL();
		switch (_myTarget) {
		case TEXTURE_2D:
			gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord2f(0, 0);
			gl.glVertex2f(0, 0);
			gl.glTexCoord2f(1f, 0);
			gl.glVertex2f(_myWidth, 0);
			gl.glTexCoord2f(1f, 1f);
			gl.glVertex2f(_myWidth, _myHeight);
			gl.glTexCoord2f(0, 1f);
			gl.glVertex2f(0, _myHeight);
			gl.glEnd();
			break;

		default:
			gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord2f(0, 0);
			gl.glVertex2f(0, 0);
			gl.glTexCoord2f(_myWidth, 0);
			gl.glVertex2f(_myWidth, 0);
			gl.glTexCoord2f(_myWidth, _myHeight);
			gl.glVertex2f(_myWidth, _myHeight);
			gl.glTexCoord2f(0, _myHeight);
			gl.glVertex2f(0, _myHeight);
			gl.glEnd();
			break;
		}
	}
	
	public void draw(CCAABoundingRectangle theRectangle) {
		beginDraw();
		GL2 gl = CCGraphics.currentGL();
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(theRectangle.min().x, theRectangle.min().y);
		gl.glVertex2f(theRectangle.min().x, theRectangle.min().y);
		gl.glTexCoord2f(theRectangle.max().x, theRectangle.min().y);
		gl.glVertex2f(theRectangle.max().x, theRectangle.min().y);
		gl.glTexCoord2f(theRectangle.max().x, theRectangle.max().y);
		gl.glVertex2f(theRectangle.max().x, theRectangle.max().y);
		gl.glTexCoord2f(theRectangle.min().x, theRectangle.max().y);
		gl.glVertex2f(theRectangle.min().x, theRectangle.max().y);
		gl.glEnd();
		endDraw();
	}
	
	public void clear() {
		beginDraw();
		GL gl = CCGraphics.currentGL();
		gl.glClearStencil(0);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);
		endDraw();
	}
	
	
	
	private int i = 0;
	
	public FloatBuffer getPBOData(final int theAttachment) {
		return getPBOData(theAttachment, 0, 0, _myWidth, _myHeight);
	}
	
	/**
	 * @param theAttachment
	 * @param theX
	 * @param theY
	 * @param theWidth
	 * @param theHeight
	 * @return
	 */
	public FloatBuffer getPBOData(final int theAttachment, final int theX, final int theY, final int theWidth, final int theHeight) {
		if(_myPBO[0] == null){
			_myPBO[0] = new CCPBO(_myNumberOfChannels * theWidth * theHeight * (_myNumberOfBits == 16 ? 2 : 4));
			_myPBO[1] = new CCPBO(_myNumberOfChannels * theWidth * theHeight * (_myNumberOfBits == 16 ? 2 : 4));
		}
		
		GL2 gl = CCGraphics.currentGL();
		gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, _myFrameBuffers[0]);
		gl.glReadBuffer(_myDrawBuffers[theAttachment]);
		
		_myPBO[i % 2].beginPack();
		gl.glReadPixels(theX, theY, theWidth, theHeight,_myAttachments[theAttachment].format().glID,GL.GL_FLOAT,0);
		_myPBO[i % 2].endPack();
		
		ByteBuffer myResult = _myPBO[(i + 1) % 2].mapReadBuffer();
		myResult.order(ByteOrder.LITTLE_ENDIAN);
		_myPBO[(i + 1) % 2].unmapReadBuffer();
		gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
		i++;
		return myResult.asFloatBuffer();
	}
	
	/**
	 * Read data from a floatbuffer
	 * @param theData
	 */
	public void loadData(final FloatBuffer theData){
		theData.rewind();
		GL gl = CCGraphics.currentGL();
		gl.glEnable(_myTarget.glID);
		gl.glBindTexture(_myTarget.glID,_myAttachments[0].id());
		gl.glTexImage2D(_myTarget.glID,0,_myAttachments[0].internalFormat().glID,_myWidth,_myHeight,0,_myAttachments[0].format().glID,GL.GL_FLOAT,theData);
		gl.glBindTexture(_myTarget.glID,0);
		gl.glDisable(_myTarget.glID);
	}

}
