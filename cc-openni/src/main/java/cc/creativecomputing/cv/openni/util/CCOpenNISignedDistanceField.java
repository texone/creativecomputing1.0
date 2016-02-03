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
package cc.creativecomputing.cv.openni.util;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.cv.openni.CCOpenNI;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.graphics.texture.CCFrameBufferObjectAttributes;
import cc.creativecomputing.graphics.texture.CCPixelFormat;
import cc.creativecomputing.graphics.texture.CCPixelInternalFormat;
import cc.creativecomputing.graphics.texture.CCPixelType;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureAttributes;
import cc.creativecomputing.graphics.texture.CCVolumeBuffer;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCVector3f;

/**
 * This class represents a signed distance field to keep depth information in a 3d texture
 * @author christianriekoff
 *
 */
public class CCOpenNISignedDistanceField {
	@CCControl(name = "volume x", min = -5000, max = 5000)
	private float _cX = 0;
	
	@CCControl(name = "volume y", min = -5000, max = 5000)
	private float _cY = 0;
	
	@CCControl(name = "volume z", min = -5000, max = 5000)
	private float _cZ = 0;
	
	@CCControl(name = "volume width", min = 0, max = 5000)
	private float _cWidth = 0;
	
	@CCControl(name = "volume height", min = 0, max = 5000)
	private float _cHeight = 0;
	
	@CCControl(name = "volume depth", min = 0, max = 5000)
	private float _cDepth = 0;
	
	@CCControl(name = "draw volume")
	private boolean _cDrawVolume = true;
	
	@CCControl(name = "color scale", min = 0, max = 1)
	private float _cColorScale = 0;
	
	@CCControl(name = "smoothing", min = 0, max = 1)
	private float _cSmoothing = 0;

	private CCVolumeBuffer _myDistanceBuffer;
	private CCVolumeBuffer _mySwapDistanceBuffer;
	private CCVolumeBuffer _myDirectionsBuffer;
	
	private CCGLSLShader _myRaymarchShader;
	
	private CCGLSLShader _mySDFShader;
	private CCGLSLShader _myDirectionsShader;
	private CCGLSLShader _myVolumeDrawShader;
	
	private CCGraphics _myGraphics;
	private CCTexture2D _myDepthGenerator;
	
	public  CCOpenNISignedDistanceField(CCApp theApp, CCTexture2D theDepthGenerator, int theResX, int theResY, int theResZ) {
		_myGraphics = theApp.g;
		
		CCTextureAttributes myTextureAttributes = new CCTextureAttributes();
		myTextureAttributes.format(CCPixelFormat.RGBA);
		myTextureAttributes.internalFormat(CCPixelInternalFormat.RGBA16F);
		myTextureAttributes.pixelType(CCPixelType.FLOAT);
		
		CCFrameBufferObjectAttributes myAttributes = new CCFrameBufferObjectAttributes(myTextureAttributes);
		_myDistanceBuffer = new CCVolumeBuffer(myAttributes, theResX, theResY, theResZ);
		_mySwapDistanceBuffer = new CCVolumeBuffer(myAttributes, theResX, theResY, theResZ);
		
//		CCFrameBufferObjectAttributes myAttributes2 = new CCFrameBufferObjectAttributes(myTextureAttributes);
		_myDirectionsBuffer = new CCVolumeBuffer(myAttributes, theResX, theResY, theResZ);
		
		_mySDFShader = new CCGLSLShader(
			CCIOUtil.classPath(this, "shader/sdf_vp.glsl"), 
			CCIOUtil.classPath(this, "shader/sdf_fp.glsl")
		);
		_mySDFShader.load();
		
		_myDirectionsShader = new CCGLSLShader(
			CCIOUtil.classPath(this, "shader/direction_vp.glsl"), 
			CCIOUtil.classPath(this, "shader/direction_fp.glsl")
		);
		_myDirectionsShader.load();
		
		_myVolumeDrawShader = new CCGLSLShader(
			CCIOUtil.classPath(this, "shader/volumeDrawer_vp.glsl"), 
			CCIOUtil.classPath(this, "shader/volumeDrawer_fp.glsl")
		);
		_myVolumeDrawShader.load();
		
		_myRaymarchShader = new CCGLSLShader(
			null,
			CCIOUtil.classPath(this, "shader/sdf_raymarch.glsl")
		);
		_myRaymarchShader.load();
		
		_myDepthGenerator = theDepthGenerator;
	}
	
	public  CCOpenNISignedDistanceField(CCApp theApp, CCTexture2D theDepthGenerator, int theRes){
		this(theApp, theDepthGenerator, theRes, theRes, theRes);
	}
	
	public CCVector3f offset() {
		return new CCVector3f(_cX, _cY, _cZ);
	}
	
	public CCVector3f scale() {
		return new CCVector3f(_cWidth, _cHeight, _cDepth);
	}
	
	public CCVolumeBuffer directions() {
		return _myDirectionsBuffer;
	}
	
	public CCVolumeBuffer distances(){
		return _myDistanceBuffer;
	}
	
	public void update(float theDeltaTime) {
		_myGraphics.noBlend();
//		_myVolumeBuffer.textureFilter(CCTextureFilter.NEAREST);
		_myGraphics.texture(0,_myDepthGenerator);
		_myGraphics.texture(1, _myDistanceBuffer);
	
		_mySwapDistanceBuffer.beginDraw(_myGraphics);
		_myGraphics.clearColor(0,0);
		_myGraphics.clear();
		_mySDFShader.start();
		
		_mySDFShader.uniform1i("depthData", 0);
		_mySDFShader.uniform1i("cubeSampler", 1);
		_mySDFShader.uniform2f("center", CCOpenNI.centerX, CCOpenNI.centerY);
		_mySDFShader.uniform2f("scale", CCOpenNI.scaleX, CCOpenNI.scaleY);
		_mySDFShader.uniform2f("depthDimension", _myDepthGenerator.width(), _myDepthGenerator.height());
		_mySDFShader.uniform1f("res", 640 / _myDepthGenerator.width());
		
		_mySDFShader.uniform3f("volumePosition", _cX, _cY, _cZ);
		_mySDFShader.uniform3f("volumeScale", _cWidth, _cHeight, _cDepth);
		_mySDFShader.uniform3f("indexSpace", 1f / _myDistanceBuffer.width(), 1f / _myDistanceBuffer.height(), 1f / _myDistanceBuffer.depth());
		_mySDFShader.uniform1f("colorScale", _cColorScale);
		_mySDFShader.uniform1f("smoothing", _cSmoothing);
		for (int z = 0; z < _myDistanceBuffer.depth(); z++) {
			// attach texture slice to FBO
			// render
			_mySDFShader.uniform1f("z", (float)(z + 0.5f)/_mySwapDistanceBuffer.depth());
			_mySwapDistanceBuffer.drawSlice(_myGraphics, z);
		}
		_mySDFShader.end();
		_mySwapDistanceBuffer.endDraw(_myGraphics);
		_myGraphics.noTexture();
		_myGraphics.gl.glFinish();// IMPORTANT: since we cheat and read/write in the same tex !
		 
		CCVolumeBuffer myVolumeBuffer = _myDistanceBuffer;
		_myDistanceBuffer = _mySwapDistanceBuffer;
		_mySwapDistanceBuffer = myVolumeBuffer;
		
		_myGraphics.noBlend();
//		_myVolumeBuffer.textureFilter(CCTextureFilter.NEAREST);
		_myGraphics.texture(0, _myDistanceBuffer);
	
		_myDirectionsBuffer.beginDraw(_myGraphics);
		_myGraphics.clearColor(0,0);
		_myGraphics.clear();
		_myDirectionsShader.start();
		
		_myDirectionsShader.uniform1i("cubeSampler", 0);
		_myDirectionsShader.uniform3f("indexSpace", 1f / _myDistanceBuffer.width(), 1f / _myDistanceBuffer.height(), 1f / _myDistanceBuffer.depth());
		for (int z = 0; z < _myDirectionsBuffer.depth(); z++) {
			// attach texture slice to FBO
			// render
			_myDirectionsShader.uniform1f("z", (float)(z + 0.5f)/_myDirectionsBuffer.depth());
			_myDirectionsBuffer.drawSlice(_myGraphics, z);
		}
		_myDirectionsShader.end();
		_myDirectionsBuffer.endDraw(_myGraphics);
		_myGraphics.noTexture();
		_myGraphics.gl.glFinish();// IMPORTANT: since we cheat and read/write in the same tex !
	}
	
	public void drawDistances(CCGraphics g, int theX, int theY, int theWidth, int theHeight){
		_myRaymarchShader.start();
		_myRaymarchShader.uniform2f("resolution", theWidth, theHeight);
		_myRaymarchShader.uniform1i("depthVolume", 0);
		_myRaymarchShader.uniform1f("depth", _cDepth);
		
		_myRaymarchShader.uniform3f("voxelOffset", _cX, _cY, _cY);
		_myRaymarchShader.uniform3f("voxelScale", _cWidth, _cHeight, _cDepth);
		
		g.texture(0, _myDistanceBuffer);
		
//		_myRaymarchShader.uniform1f("marchStepSize", _cMarchStepSize);
//		_myRaymarchShader.uniform1i("marchSteps", _cMarchSteps);
		

//		_myRaymarchShader.uniform3f("cameraPosition", _cOriginX, _cOriginY, _cOriginZ);
//		_myShader.uniformMatrix4f("cameraTransformation", _myCameraTransformation);
//		_myShader.uniform3f("cameraPosition", _myCameraPosition);
		
		g.beginShape(CCDrawMode.QUADS);
		g.textureCoords(0.0f, 0.0f);
		g.vertex(theX, theY);
		g.textureCoords(1.0f, 0.0f);
		g.vertex(theX + theWidth, theY);
		g.textureCoords(1.0f, 1.0f);
		g.vertex(theX + theWidth, theY + theWidth);
		g.textureCoords(0.0f, 1.0f);
		g.vertex(theX, theY + theWidth);
        g.endShape();
        
        g.noTexture();
        
        _myRaymarchShader.end();
	}
	
	public void draw(CCGraphics g) {
		
		
		g.blend();
		g.depthTest();
		g.strokeWeight(1);
		if(_cDrawVolume) {
			g.texture(0,_myDirectionsBuffer);
			_myVolumeDrawShader.start();
			_myVolumeDrawShader.uniform1i("cubeSampler", 0);
			g.beginShape(CCDrawMode.LINES);
			for(float x = 0; x < _myDistanceBuffer.width();x++) {
				for(float y = 0; y < _myDistanceBuffer.height();y++) {
					for(float z = 0; z < _myDistanceBuffer.depth() - 1;z++) {
						float myX = (x + 0.5f) / _myDistanceBuffer.width();
						float myY = (y + 0.5f) / _myDistanceBuffer.height();
						float myZ = (z + 0.5f) / _myDistanceBuffer.depth();
						g.textureCoords(0, myX, myY, myZ, 0);
						g.vertex(
							_cX + myX * _cWidth,
							_cY + myY * _cHeight,
							_cZ + myZ * _cDepth
						);
						g.textureCoords(0, myX, myY, myZ, 1);
						g.vertex(
							_cX + myX * _cWidth,
							_cY + myY * _cHeight,
							_cZ + myZ * _cDepth
						);
					}
				}
			}
			g.endShape();
			_myVolumeDrawShader.end();
			g.noTexture();
		}
	}
}
