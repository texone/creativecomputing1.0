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
import cc.creativecomputing.cv.openni.util.CCOpenNIFloorPlaneDetector;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;

public class CCOpenNICalcFloorPlaneDemo extends CCApp {

	private CCOpenNI _myOpenNI;
	private CCOpenNIDepthGenerator _myDepthGenerator;
	
	private CCArcball _myArcball;
	
	
	private CCOpenNIFloorPlaneDetector _myFloorPlaneDetector;
	
	@Override
	public void setup() {
		_myOpenNI = new CCOpenNI(this);
		_myOpenNI.openFileRecording("SkeletonRec.oni");
		_myDepthGenerator = _myOpenNI.createDepthGenerator();
		_myOpenNI.start();
		
		_myArcball = new CCArcball(this);
		
		_myFloorPlaneDetector = new CCOpenNIFloorPlaneDetector(_myOpenNI);
		
		addControls("app", "app", _myFloorPlaneDetector);
	}

	@Override
	public void update(final float theDeltaTime) {
		_myFloorPlaneDetector.update(theDeltaTime);
	}
	
	int myBlends = 0;

	@Override
	public void draw() {
		g.clear();
		g.pushMatrix();
		_myArcball.draw(g);
		g.scale(0.1f);
		g.pushMatrix();
		g.applyMatrix(_myFloorPlaneDetector.transformation().clone().invert());
		g.color(255);
		g.beginShape(CCDrawMode.POINTS);
		CCVector3f[] myPoints = _myDepthGenerator.depthMapRealWorld(4);
		for(CCVector3f myPoint:myPoints) {
			g.vertex(myPoint);
		}
		g.endShape();

		_myFloorPlaneDetector.draw(g);
		g.popMatrix();
		_myOpenNI.drawCamFrustum(g);
		
		g.popMatrix();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCOpenNICalcFloorPlaneDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}

