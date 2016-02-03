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
package cc.creativecomputing.demo.cv.openni;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.cv.openni.CCOpenNI;
import cc.creativecomputing.cv.openni.CCOpenNIDepthGenerator;
import cc.creativecomputing.cv.openni.CCOpenNIRenderer;
import cc.creativecomputing.cv.openni.CCOpenNISceneAnalyzer;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.math.util.CCArcball;

public class CCOpenNIRenderDemo extends CCApp {
	
	private CCOpenNI _myOpenNI;
	private CCOpenNIDepthGenerator _myDepthGenerator;
	private CCOpenNISceneAnalyzer _mySceneAnalyzer;
	private CCOpenNIRenderer _myRenderer;
	
	private CCArcball _myArcball;

	@Override
	public void setup() {
		_myOpenNI = new CCOpenNI(this);
		_myOpenNI.openFileRecording("SkeletonRec.oni");
		_myDepthGenerator = _myOpenNI.createDepthGenerator();
		_mySceneAnalyzer = _myOpenNI.createSceneAnalyzer();
		_myOpenNI.start();
		_myRenderer = new CCOpenNIRenderer(_myDepthGenerator.width(), _myDepthGenerator.height());
		
		_myArcball = new CCArcball(this);
		
		addControls("app", "app", _myRenderer);
	}

	@Override
	public void update(final float theDeltaTime) {
	}

	@Override
	public void draw() {
		
		
		g.clear();
		_myArcball.draw(g);
		
		g.blend(CCBlendMode.ADD);
		g.noDepthTest();
		g.pointSize(0.1f);
		g.pushMatrix();
		g.applyMatrix(_myOpenNI.transformationMatrix());
		g.color(255, 100, 50, 150);
		_myRenderer.drawDepthMesh(g, _myDepthGenerator.texture());
		g.color(255, 255, 255, 150);
		_myRenderer.drawSceneMesh(g, _myDepthGenerator.texture(), _mySceneAnalyzer.texture());
		g.popMatrix();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCOpenNIRenderDemo.class);
		myManager.settings().size(1400, 800);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

