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
import cc.creativecomputing.cv.openni.util.CCOpenNISignedDistanceField;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.util.CCArcball;

/**
 * 
 * @author christianriekoff
 * 
 */
public class CCOpenNISDFRaymarchDemo extends CCApp {
	

	@CCControl(name = "z move", min = -10000, max = 10000)
	private float _cZMove = 0;

	
	@CCControl(name = "draw depthmap")
	private boolean _cDrawDepthMap = true;
	
	@CCControl(name = "update depthmap")
	private boolean _cUpdateDepthMap = true;


	private CCArcball _myArcball;

	private CCOpenNI _myOpenNI;
	private CCOpenNIRenderer _myRenderer;
	private CCOpenNIDepthGenerator _myDepthGenerator;
	
	private CCOpenNISignedDistanceField _mySignedDistanceField;
	

	public void setup() {
		_myArcball = new CCArcball(this);

		_myOpenNI = new CCOpenNI(this);
		_myOpenNI.openFileRecording("SkeletonRec.oni");
		_myDepthGenerator = _myOpenNI.createDepthGenerator();
		_myOpenNI.start();
		_myRenderer = new CCOpenNIRenderer(_myDepthGenerator.width(), _myDepthGenerator.height()); 
		
		_mySignedDistanceField = new CCOpenNISignedDistanceField(this, _myDepthGenerator.texture(), 64);
		addControls("app", "app", 1, _mySignedDistanceField);
		addControls("app", "draw", this);
		g.strokeWeight(3);
		
		
//		g.perspective(95, width / (float) height, 10, 150000);
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.CCApp#update(float)
	 */
	@Override
	public void update(float theDeltaTime) {
		_myDepthGenerator.stopUpdateTexture(!_cUpdateDepthMap);
		_mySignedDistanceField.update(theDeltaTime);
	}

	public void draw() {
		g.clearColor(0);
		g.clear();

		// set the scene pos
		g.pushMatrix();
		_myArcball.draw(g);
		//
		g.translate(0, 0, _cZMove); // set the rotation center of the scene 1000 infront of the camera
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
		_mySignedDistanceField.draw(g);
		g.popMatrix();
		g.clearDepthBuffer();
		
		_mySignedDistanceField.drawDistances(g, -width / 2, -height / 2, 320, 240);
		
		g.blend();
		
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCOpenNISDFRaymarchDemo.class);
		myManager.settings().size(1200, 800);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
