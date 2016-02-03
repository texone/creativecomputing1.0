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
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureCubeMap;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.util.logging.CCLog;

/**
 * 
 * @author christianriekoff
 * 
 */
public class CCOpenNINormalMapDemo extends CCApp {

	private CCArcball _myArcball;

	private CCOpenNI _myOpenNI;
	private CCOpenNIRenderer _myRenderer;
	private CCOpenNIDepthGenerator _myDepthGenerator;
	
	private CCShaderBuffer _myShaderTexture;
	private CCShaderBuffer _myTempShaderTexture;
	private CCGLSLShader _myDepthSmoothShader;
	
	private CCGLSLShader _myNormalMapShader;
	
	@CCControl(name = "smoothing", min = 0, max = 1)
	private float _cSmoothing = 0;
	
	@CCControl(name = "threshold", min = 0, max = 1)
	private float _cThreshold = 0;
	
	@CCControl(name = "normal radius", min = 0, max = 50)
	private float _cNormalRadius = 0;
	
	@CCControl(name = "refraction", min = 0, max = 1)
	private float _cRefraction = 0;
	
	@CCControl(name = "min depth", min = 0, max = 1)
	private float _cMinDepth = 0;
	
	@CCControl(name = "max depth", min = 0, max = 1)
	private float _cMaxDepth = 0;
	

	
	@CCControl(name = "dispertion r", min = 0.7f, max = 1.1f)
	private float _cDispertionR = 0.9f;
	@CCControl(name = "dispertion g", min = 0.7f, max = 1.1f)
	private float _cDispertionG = 0.97f;
	@CCControl(name = "dispertion b", min = 0.7f, max = 1.1f)
	private float _cDispertionB = 1.04f;

	@CCControl(name = "bias", min = 0f, max = 1f)
	private float _cBias = 0.9f;
	@CCControl(name = "scale", min = -5, max = 5)
	private float _cScale = 0.7f;
	@CCControl(name = "power", min = 0f, max = 20f)
	private float _cPower = 1.1f;
	
	@CCControl(name = "draw depth maps")
	private boolean _cDrawDepthMaps = false;
	
	private CCTexture2D _myBackground;
	

	private CCTextureCubeMap _myCubeMap;

	public void setup() {
		addControls("app", "app", this);
		_myArcball = new CCArcball(this);

		_myOpenNI = new CCOpenNI(this);
		_myOpenNI.openFileRecording("SkeletonRec.oni");
		_myDepthGenerator = _myOpenNI.createDepthGenerator();
		
		_myDepthSmoothShader = new CCGLSLShader(
			CCIOUtil.classPath(this, "depthSmooth_vp.glsl"), 
			CCIOUtil.classPath(this, "depthSmooth_fp.glsl")
		);
		_myDepthSmoothShader.load();
		
		_myNormalMapShader  = new CCGLSLShader(
			null, 
			CCIOUtil.classPath(this, "normalMap_fp.glsl")
		);
		_myNormalMapShader.load();
		
		_myShaderTexture = new CCShaderBuffer(32, 3, _myDepthGenerator.width(), _myDepthGenerator.height(), CCTextureTarget.TEXTURE_2D);
		_myShaderTexture.beginDraw();
		g.clearColor(0);
		g.clear();
		_myShaderTexture.endDraw();
		_myShaderTexture.attachment(0).mustFlipVertically(true);

		g.clearColor(0);
		
		_myTempShaderTexture = new CCShaderBuffer(32, 3, _myDepthGenerator.width(), _myDepthGenerator.height(), CCTextureTarget.TEXTURE_2D);
		_myTempShaderTexture.beginDraw();
		g.clearColor(0);
		g.clear();
		_myTempShaderTexture.endDraw();
		_myTempShaderTexture.attachment(0).mustFlipVertically(true);

		_myOpenNI.start();
		_myRenderer = new CCOpenNIRenderer(_myDepthGenerator.width(), _myDepthGenerator.height()); 
		g.strokeWeight(3);
//		g.perspective(95, width / (float) height, 10, 150000);
		
		_myBackground = new CCTexture2D(CCTextureIO.newTextureData("waltz3.jpg"));
		
		_myCubeMap = new CCTextureCubeMap(CCTextureIO.loadCubeMapData(
			"stormydays/pos_x.png",
			"stormydays/neg_x.png",
			"stormydays/pos_y.png",
			"stormydays/neg_y.png",
			"stormydays/pos_z.png",
			"stormydays/neg_z.png"	
		));
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
		
//		_myNormalMapShader.checkUpdates();
	}
	
	int _myScale = 3;

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
		else _myRenderer.drawDepthMesh(g, _myDepthGenerator.texture());
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
		
//		CCLog.info(_myDepthGenerator.width() +":" + _myDepthGenerator.height());
		
		g.clear();
		
		_myShaderTexture.attachment(0).textureFilter(CCTextureFilter.LINEAR);
		
		_myNormalMapShader.start();
		
		g.texture(0, _myShaderTexture.attachment(0));
		g.texture(1, _myCubeMap);
		_myNormalMapShader.uniform1i("depthTexture", 0);
		_myNormalMapShader.uniform1i("cubeMap", 1);
		_myNormalMapShader.uniform2f("resolution", width, height);
		_myNormalMapShader.uniform2f("center", CCOpenNI.centerX, CCOpenNI.centerY);
		_myNormalMapShader.uniform2f("scale", CCOpenNI.scaleX, CCOpenNI.scaleY);
		_myNormalMapShader.uniform1f("normalRadius", _cNormalRadius);
		_myNormalMapShader.uniform1f("refraction", _cRefraction);
		_myNormalMapShader.uniform1f("minDepth", _cMinDepth);
		_myNormalMapShader.uniform1f("maxDepth", _cMaxDepth);
		_myNormalMapShader.uniform1f("res", 640f / width);
		
		_myNormalMapShader.uniform3f("glassChromaticDispertion", _cDispertionR, _cDispertionG, _cDispertionB);
		_myNormalMapShader.uniform1f("glassBias", _cBias);
		_myNormalMapShader.uniform1f("glassScale", _cScale);
		_myNormalMapShader.uniform1f("glassPower", _cPower);
		
		g.beginShape(CCDrawMode.QUADS);
		g.textureCoords(0.0f, 0.0f);
		g.vertex(-width / 2, -height / 2);
		g.textureCoords(1.0f, 0.0f);
		g.vertex( width / 2, -height / 2);
		g.textureCoords(1.0f, 1.0f);
		g.vertex( width / 2,  height / 2);
		g.textureCoords(0.0f, 1.0f);
		g.vertex(-width / 2,  height / 2);
        g.endShape();
        
        g.noTexture();
        
        _myNormalMapShader.end();
		g.blend();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCOpenNINormalMapDemo.class);
		myManager.settings().size(320 * 3, 240 * 3);
		myManager.settings().antialiasing(8);
		myManager.settings().alwaysOnTop(true);
		myManager.start();
	}
}
