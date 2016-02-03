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
package cc.creativecomputing.demo.cv.openni.depth;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.cv.openni.CCOpenNI;
import cc.creativecomputing.cv.openni.CCOpenNIDepthGenerator;
import cc.creativecomputing.cv.openni.CCOpenNIRenderer;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.export.CCScreenCapture;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.util.CCArcball;

/**
 * 
 * @author christianriekoff
 * 
 */
public class CCOpenNIDepthSmoothingDemo extends CCApp {

	private CCArcball _myArcball;

	private CCOpenNI _myOpenNI;
	private CCOpenNIRenderer _myRenderer;
	private CCOpenNIDepthGenerator _myDepthGenerator;
	
	private CCShaderBuffer _myShaderTexture;
	private CCShaderBuffer _myTempShaderTexture;
	private CCGLSLShader _myDepthSmoothShader;
	
	@CCControl(name = "smoothing", min = 0, max = 1)
	private float _cSmoothing = 0;
	
	@CCControl(name = "threshold", min = 0, max = 1)
	private float _cThreshold = 0;
	
	@CCControl(name = "draw depth maps")
	private boolean _cDrawDepthMaps = false;

	public void setup() {
		addControls("app", "app", this);
		_myArcball = new CCArcball(this);

		_myOpenNI = new CCOpenNI(this);
		_myOpenNI.openFileRecording("Captured2.oni");
		_myDepthGenerator = _myOpenNI.createDepthGenerator();
		
		_myDepthSmoothShader = new CCGLSLShader(
			CCIOUtil.classPath(this, "depthSmooth_vp.glsl"), 
			CCIOUtil.classPath(this, "depthSmooth_fp.glsl")
		);
		_myDepthSmoothShader.load();
		_myShaderTexture = new CCShaderBuffer(32, 3, _myDepthGenerator.width(), _myDepthGenerator.height(), CCTextureTarget.TEXTURE_2D);
		_myShaderTexture.beginDraw();
		g.clearColor(0.1f);
		g.clear();
		_myShaderTexture.endDraw();
		_myShaderTexture.attachment(0).mustFlipVertically(true);

		g.clearColor(0);
		
		_myTempShaderTexture = new CCShaderBuffer(32, 3, _myDepthGenerator.width(), _myDepthGenerator.height(), CCTextureTarget.TEXTURE_2D);
		_myTempShaderTexture.beginDraw();
		g.clearColor(0.1f);
		g.clear();
		_myTempShaderTexture.endDraw();
		_myTempShaderTexture.attachment(0).mustFlipVertically(true);

		_myOpenNI.start();
		_myRenderer = new CCOpenNIRenderer(_myDepthGenerator.width() / 2, _myDepthGenerator.height() / 2); 
		g.strokeWeight(3);
//		g.perspective(95, width / (float) height, 10, 150000);

		g.clearColor(0f);
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.CCApp#update(float)
	 */
	@Override
	public void update(float theDeltaTime) {
		g.noBlend();
		g.texture(0,_myDepthGenerator.texture());
		g.texture(1,_myShaderTexture.attachment(0));
		_myDepthSmoothShader.start();
		_myDepthSmoothShader.uniform1i("depthData", 0);
		_myDepthSmoothShader.uniform1i("prevDepthData", 1);
		_myDepthSmoothShader.uniform1f("smoothing", _cSmoothing);
		_myDepthSmoothShader.uniform1f("threshold", _cThreshold);
		_myTempShaderTexture.draw();
		_myDepthSmoothShader.end();
		g.noTexture();
		
		CCShaderBuffer mySwap = _myTempShaderTexture;
		_myTempShaderTexture = _myShaderTexture;
		_myShaderTexture = mySwap;
	}
	
	int _myScale = 1;

	public void draw() {
		g.clear();

		// set the scene pos
		g.pushMatrix();
		_myArcball.draw(g);
		//
//		g.translate(0, 0, -1000); // set the rotation center of the scene 1000 infront of the camera
		g.pointSize(0.1f);
		g.color(255, 100, 50, 150);
		g.blend(CCBlendMode.ADD);
		g.noDepthTest();
		g.pushMatrix();
		g.applyMatrix(_myOpenNI.transformationMatrix());
		if(mousePressed)_myRenderer.drawDepthMesh(g, _myShaderTexture.attachment(0));
		else 
			_myRenderer.drawDepthMesh(g, _myDepthGenerator.texture());
		g.popMatrix();
		g.color(255);
		g.strokeWeight(1);
		g.popMatrix();
		
		if(_cDrawDepthMaps){
			g.noDepthTest();
			g.noBlend();
			g.image(_myDepthGenerator.texture(), -width/2, -height/2, 320 * _myScale, 240 * _myScale);
			g.image(_myShaderTexture.attachment(0), -width/2 + 320 * _myScale, -height/2, 320 * _myScale, 240 * _myScale);
		}
	}
	
	@Override
	public void keyPressed(CCKeyEvent theKeyEvent) {
		switch(theKeyEvent.keyCode()){
		case VK_S:
			CCScreenCapture.capture("export2/frame" + frameCount+".png", width, height);
			break;
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCOpenNIDepthSmoothingDemo.class);
		myManager.settings().size(1900, 800);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
