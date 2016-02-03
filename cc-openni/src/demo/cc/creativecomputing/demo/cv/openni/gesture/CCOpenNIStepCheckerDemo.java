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
package cc.creativecomputing.demo.cv.openni.gesture;


import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.cv.openni.CCOpenNI;
import cc.creativecomputing.cv.openni.CCOpenNIUserGenerator;
import cc.creativecomputing.cv.openni.skeleton.CCOpenNISkeletonProvider;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.model.collada.CCColladaLoader;
import cc.creativecomputing.model.collada.CCColladaScene;
import cc.creativecomputing.model.collada.CCColladaSkeleton;
import cc.creativecomputing.model.collada.CCColladaSkinController;
import cc.creativecomputing.skeleton.CCSkeleton;
import cc.creativecomputing.skeleton.CCSkeletonManager;
import cc.creativecomputing.skeleton.gesture.CCSkeletonAngleChecker;

/**
 * 
 * @author christianriekoff
 * 
 */
public class CCOpenNIStepCheckerDemo extends CCApp {
	
	@CCControl(name = "gravity", min = -100, max = 100)
	private static float _cGravity = 0;
	@CCControl(name = "init velocity scale", min = 0, max = 1000)
	private static float _cInitVelocityScale = 0;

	private CCArcball _myArcball;

	private CCOpenNI _myOpenNI;
	private CCOpenNIUserGenerator _myUserGenerator;
	
	private CCSkeletonManager _mySkeletonManager;
	private CCSkeletonAngleChecker _myLeftStepChecker;
	private CCSkeletonAngleChecker _myRightStepChecker;

	public void setup() {
		addControls("app", "app", this);
		_myArcball = new CCArcball(this);

		_myOpenNI = new CCOpenNI(this);
		_myOpenNI.openFileRecording("SkeletonRec.oni");

		_myUserGenerator = _myOpenNI.createUserGenerator();
		_myOpenNI.start();
		
		CCColladaLoader myColladaLoader = new CCColladaLoader("skeleton.dae");
		
		CCColladaScene myScene = myColladaLoader.scenes().element(0);
		CCColladaSkinController mySkin = myColladaLoader.controllers().element(0).skin();
		
		CCColladaSkeleton mySkeleton = new CCColladaSkeleton(
			mySkin,
			myScene.node("bvh_import/Hips")
		);
		_mySkeletonManager = new CCSkeletonManager(
			this,
			mySkeleton, 
			new CCOpenNISkeletonProvider(_myUserGenerator)
		);
		
		CCSkeletonAngleChecker.Settings myAngleCheckerSettings = new CCSkeletonAngleChecker.Settings();
		addControls("app", "step", myAngleCheckerSettings);
		
		_myLeftStepChecker = CCSkeletonAngleChecker.createLeftLeg();
		_myRightStepChecker = CCSkeletonAngleChecker.createRightLeg();
		
		_myLeftStepChecker.settings(myAngleCheckerSettings);
		_myRightStepChecker.settings(myAngleCheckerSettings);
		
		_mySkeletonManager.events().add(_myLeftStepChecker);
		_mySkeletonManager.events().add(_myRightStepChecker);
		g.strokeWeight(3);
	}
	
	@Override
	public void update(float theDeltaTime) {
		_mySkeletonManager.update(theDeltaTime);
	}

	public void draw() {
		g.clear();

		g.pushMatrix();
		// set the scene pos
		_myArcball.draw(g);
		//
//		g.translate(0, 0, -1000); // set the rotation center of the scene 1000 in front of the camera
		g.pointSize(0.1f);
		g.color(255, 100, 50, 150);
		g.blend(CCBlendMode.ADD);
		g.noDepthTest();
//		_myRenderer.drawDepthMesh(g);
		
		g.color(255);
		g.strokeWeight(1);

		g.noDepthTest();
//		_myDepthGenerator.drawDepthMap(g);

		g.color(255);
		for (CCSkeleton mySkeleton : _mySkeletonManager.skeletons()) {
			mySkeleton.draw(g);
		}
		_myLeftStepChecker.draw(g);
		_myRightStepChecker.draw(g);
		g.popMatrix();
		
		g.color(0,255,255);
		_myLeftStepChecker.drawValues(g, width);
		g.color(255,255,0);
		_myRightStepChecker.drawValues(g, width);
		
		g.text(frameRate,0,0);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCOpenNIStepCheckerDemo.class);
		myManager.settings().size(1200, 800);
		myManager.settings().antialiasing(8);
//		myManager.settings().frameRate(60);
		myManager.start();
	}
}
