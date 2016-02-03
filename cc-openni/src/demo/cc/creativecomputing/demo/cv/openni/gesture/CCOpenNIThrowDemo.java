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

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.cv.openni.CCOpenNI;
import cc.creativecomputing.cv.openni.CCOpenNIUserGenerator;
import cc.creativecomputing.cv.openni.skeleton.CCOpenNISkeletonProvider;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.model.collada.CCColladaLoader;
import cc.creativecomputing.model.collada.CCColladaScene;
import cc.creativecomputing.model.collada.CCColladaSkeleton;
import cc.creativecomputing.model.collada.CCColladaSkinController;
import cc.creativecomputing.skeleton.CCSkeleton;
import cc.creativecomputing.skeleton.CCSkeletonJoint;
import cc.creativecomputing.skeleton.CCSkeletonManager;
import cc.creativecomputing.skeleton.gesture.CCSkeletonAngleChecker;
import cc.creativecomputing.skeleton.gesture.CCSkeletonAngleChecker.CCSkeletonAngleChangeListener;

/**
 * 
 * @author christianriekoff
 * 
 */
public class CCOpenNIThrowDemo extends CCApp implements CCSkeletonAngleChangeListener{
	private class CCUserParticle{
		private CCVector3f _myVelocity;
		private CCVector3f _myPosition;
		
		public CCUserParticle(CCVector3f theVelocity, CCVector3f thePosition) {
			_myVelocity = theVelocity;
			_myPosition = thePosition;
		}
		
		public void update(float theDeltaTime) {
			_myPosition.add(_myVelocity.clone().scale(theDeltaTime));
			_myVelocity.subtract(new CCVector3f(0,_cGravity,0));
		}
		
		public void draw(CCGraphics g) {
			g.pointSize(20);
			g.point(_myPosition);
		}
	}
	
	@CCControl(name = "gravity", min = -100, max = 100)
	private static float _cGravity = 0;
	@CCControl(name = "init velocity scale", min = 0, max = 1000)
	private static float _cInitVelocityScale = 0;

	private CCArcball _myArcball;

	private CCOpenNI _myOpenNI;
	private CCOpenNIUserGenerator _myUserGenerator;
	
	private CCSkeletonManager _mySkeletonManager;
	private CCSkeletonAngleChecker _myLeftThrowChecker;
	private CCSkeletonAngleChecker _myRightThrowChecker;
	
	private List<CCUserParticle> _myParticles = new ArrayList<CCUserParticle>();

	public void setup() {
		
		addControls("app", "app", this);
		_myArcball = new CCArcball(this);

		_myOpenNI = new CCOpenNI(this);
		_myOpenNI.openFileRecording("SkeletonRec.oni");
		_myUserGenerator = _myOpenNI.createUserGenerator();
		
		_myOpenNI.start();
		
		CCColladaLoader myColladaLoader = new CCColladaLoader("humanoid.dae");
		
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
		
		_myLeftThrowChecker = CCSkeletonAngleChecker.createLeftArm();
		_myRightThrowChecker = CCSkeletonAngleChecker.createRightArm();
		
		_myLeftThrowChecker.settings(myAngleCheckerSettings);
		_myRightThrowChecker.settings(myAngleCheckerSettings);
		
		_mySkeletonManager.events().add(_myLeftThrowChecker);
		_mySkeletonManager.events().add(_myRightThrowChecker);
		g.strokeWeight(3);
	}
	
	@Override
	public void onChange(CCSkeletonJoint theJoint) {
		_myParticles.add(new CCUserParticle(theJoint.velocity().clone().scale(_cInitVelocityScale), theJoint.position().clone()));
	}
	
	@Override
	public void update(float theDeltaTime) {
		
		for(CCUserParticle myUserParticle:_myParticles) {
			myUserParticle.update(theDeltaTime);
		}

		_mySkeletonManager.update(theDeltaTime);
	}

	public void draw() {
		g.clear();

		g.pushMatrix();
		// set the scene pos
		_myArcball.draw(g);
		//
//		g.translate(0, 0, -1000); // set the rotation center of the scene 1000 infront of the camera
		g.pointSize(0.1f);
		g.color(255, 100, 50, 150);
		g.blend(CCBlendMode.ADD);
		g.noDepthTest();
		
		g.color(255);
		g.strokeWeight(1);

		g.noDepthTest();
		g.color(255);
		for (CCSkeleton mySkeleton : _mySkeletonManager.skeletons()) {
			mySkeleton.draw(g);
		}
		_myLeftThrowChecker.draw(g);
		_myRightThrowChecker.draw(g);

		for(CCUserParticle myUserParticle:_myParticles) {
			myUserParticle.draw(g);
		}
		g.popMatrix();
		
		g.color(0,255,255);
		_myLeftThrowChecker.drawValues(g, width);
		g.color(255,255,0);
		_myRightThrowChecker.drawValues(g, width);
		g.text(frameRate,0,0);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCOpenNIThrowDemo.class);
		myManager.settings().size(1200, 800);
		myManager.settings().antialiasing(8);
//		myManager.settings().frameRate(60);
		myManager.start();
	}
}
