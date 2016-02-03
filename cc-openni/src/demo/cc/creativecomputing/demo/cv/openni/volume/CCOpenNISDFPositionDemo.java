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
package cc.creativecomputing.demo.cv.openni.volume;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.cv.openni.CCOpenNI;
import cc.creativecomputing.cv.openni.CCOpenNIDepthGenerator;
import cc.creativecomputing.cv.openni.CCOpenNIRenderer;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.graphics.texture.CCFrameBufferObjectAttributes;
import cc.creativecomputing.graphics.texture.CCPixelFormat;
import cc.creativecomputing.graphics.texture.CCPixelInternalFormat;
import cc.creativecomputing.graphics.texture.CCPixelType;
import cc.creativecomputing.graphics.texture.CCTextureAttributes;
import cc.creativecomputing.graphics.texture.CCVolumeBuffer;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.util.logging.CCLog;

/**
 * 
 * @author christianriekoff
 * 
 */
public class CCOpenNISDFPositionDemo extends CCApp {
	
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
	
	@CCControl(name = "draw depthmap")
	private boolean _cDrawDepthMap = true;
	
	@CCControl(name = "update depthmap")
	private boolean _cUpdateDepthMap = true;
	
	@CCControl(name = "color scale", min = 0, max = 1)
	private float _cColorScale = 0;
	
	private CCVolumeBuffer _myVolumeBuffer;

	private CCArcball _myArcball;

	private CCOpenNI _myOpenNI;
	private CCOpenNIDepthGenerator _myDepthGenerator;
	private CCOpenNIRenderer _myRenderer;
	
	private CCGLSLShader _mySDFShader;
//	private CCGLSLShader _myVolumeDrawShader;

	public void setup() {
		addControls("app", "app", this);
		_myArcball = new CCArcball(this);

		_myOpenNI = new CCOpenNI(this);
		_myOpenNI.openFileRecording("SkeletonRec.oni");
		_myDepthGenerator = _myOpenNI.createDepthGenerator();
		_myOpenNI.start();
		_myRenderer = new CCOpenNIRenderer(_myDepthGenerator.width(), _myDepthGenerator.height()); 

		g.strokeWeight(3);
		
		CCTextureAttributes myTextureAttributes = new CCTextureAttributes();
		myTextureAttributes.format(CCPixelFormat.RGBA);
		myTextureAttributes.internalFormat(CCPixelInternalFormat.RGBA16F);
		myTextureAttributes.pixelType(CCPixelType.FLOAT);
		
		CCFrameBufferObjectAttributes myAttributes = new CCFrameBufferObjectAttributes(myTextureAttributes);
		_myVolumeBuffer = new CCVolumeBuffer(myAttributes, 16, 16, 16);
		
		_mySDFShader = new CCGLSLShader(
				CCIOUtil.classPath(this, "positioncheck_vp.glsl"), 
				CCIOUtil.classPath(this, "positioncheck_fp.glsl")
			);
		_mySDFShader.load();
//		g.perspective(95, width / (float) height, 10, 150000);
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.CCApp#update(float)
	 */
	@Override
	public void update(float theDeltaTime) {
		
	}

	public void draw() {
		g.clear();

		// set the scene pos
		_myArcball.draw(g);
		//
//		g.translate(0, 0, -1000); // set the rotation center of the scene 1000 infront of the camera
		if(_cDrawDepthMap) {
			g.pointSize(0.1f);
			g.color(255, 100, 50, 150);
			g.blend(CCBlendMode.ADD);
			g.noDepthTest();
			g.pushMatrix();
			g.applyMatrix(_myOpenNI.transformationMatrix());
			_myRenderer.drawDepthMesh(g, _myDepthGenerator.texture());
			g.popMatrix();
			g.color(255,100);
		}
		
		g.depthTest();
		g.strokeWeight(1);
		if (_cDrawVolume) {
			g.blend(CCBlendMode.BLEND);
			g.texture(0, _myDepthGenerator.texture());
			_mySDFShader.start();

			_mySDFShader.uniform1i("depthData", 0);
			_mySDFShader.uniform2f("center", CCOpenNI.centerX, CCOpenNI.centerY);
			_mySDFShader.uniform2f("scale", CCOpenNI.scaleX, CCOpenNI.scaleY);
			_mySDFShader.uniform2f("depthDimension", _myDepthGenerator.width(), _myDepthGenerator.height());
			_mySDFShader.uniform1f("res", 640 / _myDepthGenerator.width());

			_mySDFShader.uniform3f("volumePosition", _cX, _cY, _cZ);
			_mySDFShader.uniform3f("volumeScale", _cWidth, _cHeight, _cDepth);
			_mySDFShader.uniform1f("colorScale", _cColorScale);
			g.beginShape(CCDrawMode.POINTS);
			for (float x = 0; x < _myVolumeBuffer.width(); x++) {
				for (float y = 0; y < _myVolumeBuffer.height(); y++) {
					for (float z = 0; z < _myVolumeBuffer.depth(); z++) {
						CCLog.info(x+":"+y+":"+z);
						float myX = (x + 0.5f) / _myVolumeBuffer.width();
						float myY = (y + 0.5f) / _myVolumeBuffer.height();
						float myZ = (z + 0.5f) / _myVolumeBuffer.depth();
						g.textureCoords(0, 0f,0f);
						g.vertex(myX, myY, myZ,0);
						g.vertex(myX, myY, myZ,1);
//						g.textureCoords(0, 1f,0f);
//						g.vertex(myX, myY, myZ);
					}
				}
			}
			g.endShape();
			_mySDFShader.end();
			g.noTexture();
		}
		
		

		g.noDepthTest();
		
		CCLog.info(frameRate);
		
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCOpenNISDFPositionDemo.class);
		myManager.settings().size(1200, 800);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
