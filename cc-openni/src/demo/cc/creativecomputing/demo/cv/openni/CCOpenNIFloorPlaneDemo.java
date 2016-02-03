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
import cc.creativecomputing.cv.openni.CCOpenNIUser;
import cc.creativecomputing.cv.openni.CCOpenNIUserGenerator;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.util.logging.CCLog;

/**
 * 
 * @author christianriekoff
 * 
 */
public class CCOpenNIFloorPlaneDemo extends CCApp {

	private CCArcball _myArcball;

	private CCOpenNI _myOpenNI;
	private CCOpenNIDepthGenerator _myDepthGenerator;
	private CCOpenNIUserGenerator _myUserGenerator;
	private CCOpenNISceneAnalyzer _mySceneAnalyzer;
	private CCOpenNIRenderer _myRenderer;

	public void setup() {
		addControls("app", "app", this);
		_myArcball = new CCArcball(this);

		_myOpenNI = new CCOpenNI(this);
		_myOpenNI.openFileRecording("SkeletonRec.oni");

		_myDepthGenerator = _myOpenNI.createDepthGenerator();
		_myUserGenerator = _myOpenNI.createUserGenerator();
		_mySceneAnalyzer = _myOpenNI.createSceneAnalyzer();
		_myRenderer = new CCOpenNIRenderer(_myDepthGenerator.width(), _myDepthGenerator.height()); 
		_myOpenNI.start();
		g.strokeWeight(3);
//		g.perspective(95, width / (float) height, 10, 150000);
	}


	public void draw() {
		g.clear();

		// set the scene pos
		_myArcball.draw(g);
		//
//		g.translate(0, 0, -1000); // set the rotation center of the scene 1000 infront of the camera
		g.pointSize(0.1f);
		g.color(255, 100, 50, 150);
		g.blend(CCBlendMode.ADD);
		g.noDepthTest();
		
		g.pushMatrix();
		g.applyMatrix(_myOpenNI.transformationMatrix());
		_myRenderer.drawDepthMesh(g, _myDepthGenerator.texture());
		g.popMatrix();
		
		g.color(255);
		g.strokeWeight(1);

		g.noDepthTest();
//		_myDepthGenerator.drawDepthMap(g);

		g.color(255);
		for (CCOpenNIUser myUser : _myUserGenerator.user()) {
			myUser.drawSkeleton(g);
//			myUser.drawOrientations(g, 50);
//			myUser.drawVelocities(g,500);
			g.color(255);
//			for(CCUserJoint myJoint:myUser.joints()) {
//				CCVector3f myAcc = myJoint.acceleration();
//				if(myAcc.length() > _cMinAcc) {
//					myJoint.drawAcceleration(g, 500);
//				}
//			}
		}
		_myOpenNI.drawCamFrustum(g);

		g.color(255);
		CCLog.info(_mySceneAnalyzer.floorPlane());
		_mySceneAnalyzer.floorPlane().drawScale(5000);
		_mySceneAnalyzer.floorPlane().draw(g);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCOpenNIFloorPlaneDemo.class);
		myManager.settings().size(1200, 800);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
