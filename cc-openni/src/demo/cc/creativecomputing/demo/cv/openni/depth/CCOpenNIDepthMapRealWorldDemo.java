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
import cc.creativecomputing.cv.openni.CCOpenNI;
import cc.creativecomputing.cv.openni.CCOpenNIDepthGenerator;
import cc.creativecomputing.cv.openni.CCOpenNISceneAnalyzer;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;

public class CCOpenNIDepthMapRealWorldDemo extends CCApp {

	private CCOpenNI _myOpenNI;
	private CCOpenNIDepthGenerator _myDepthGenerator;
	private CCOpenNISceneAnalyzer _mySceneGenerator;
	
	private CCArcball _myArcball;
	
	@Override
	public void setup() {
		_myOpenNI = new CCOpenNI(this);
//		_myOpenNI.openFileRecording("brandspace_record1.oni");
		_myOpenNI.openFileRecording("SkeletonRec.oni");
		_myDepthGenerator = _myOpenNI.createDepthGenerator();
		_mySceneGenerator = _myOpenNI.createSceneAnalyzer();
		_myOpenNI.start();
		
		_myArcball = new CCArcball(this);
	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {
		g.clear();
		_myArcball.draw(g);
		_myOpenNI.drawCamFrustum(g);
		g.color(1f,0f,0f);
		g.line(0,0,0,1000,0,0);
		g.color(0f,1f,0f);
		g.line(0,0,0,0,1000,0);
		g.color(0f,0f,1f);
		g.line(0,0,0,0,0,1000);
		g.color(255);
		g.beginShape(CCDrawMode.POINTS);
		CCVector3f[] myPoints = _myDepthGenerator.depthMapRealWorld(1);
		for(CCVector3f myPoint:myPoints) {
			g.vertex(myPoint);
		}
		g.endShape();
//		_mySceneGenerator.floorPlane().draw(g);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCOpenNIDepthMapRealWorldDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}

