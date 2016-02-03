package cc.creativecomputing.demo.cv.openni.skeleton;

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
import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.cv.openni.CCOpenNI;
import cc.creativecomputing.cv.openni.CCOpenNIUser;
import cc.creativecomputing.cv.openni.CCOpenNIUserGenerator;
import cc.creativecomputing.cv.openni.skeleton.CCOpenNISkeletonProvider;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.model.collada.CCColladaLoader;
import cc.creativecomputing.model.collada.CCColladaScene;
import cc.creativecomputing.model.collada.CCColladaSkeleton;
import cc.creativecomputing.model.collada.CCColladaSkinController;
import cc.creativecomputing.skeleton.CCSkeleton;
import cc.creativecomputing.skeleton.CCSkeletonManager;

public class CCOpenNISkeletonProviderDemo extends CCApp  {

	private CCArcball _myArcball;

	private CCSkeletonManager _mySkeletonManager;
	
	private CCOpenNI _myOpenNI;
	private CCOpenNIUserGenerator _myUserGenerator;

	public void setup() {
		_myArcball = new CCArcball(this);
		
		CCColladaLoader myColladaLoader = new CCColladaLoader("humanoid.dae");
		
		CCColladaScene myScene = myColladaLoader.scenes().element(0);
		CCColladaSkinController mySkinController = myColladaLoader.controllers().element(0).skin();
		
		CCColladaSkeleton mySkeleton = new CCColladaSkeleton(
			mySkinController,
			myScene.node("bvh_import/Hips")
		);
		
		_myOpenNI = new CCOpenNI(this);
		_myOpenNI.openFileRecording("SkeletonRec.oni");
		_myUserGenerator = _myOpenNI.createUserGenerator();
		_myOpenNI.start();
		
		_mySkeletonManager = new CCSkeletonManager(
			this,
			mySkeleton, 
			new CCOpenNISkeletonProvider(_myUserGenerator)
		);
	}

	@Override
	public void update(float theDeltaTime) {
		_mySkeletonManager.update(theDeltaTime);
	}
	
	public void draw() {
		g.clearColor(0, 0, 0);
		g.clear();
		_myArcball.draw(g);

		for(CCSkeleton mySkeleton:_mySkeletonManager.skeletons()){
			mySkeleton.draw(g);
		}
		for(CCOpenNIUser myOpenNIUser:_myUserGenerator.user()){
			myOpenNIUser.drawSkeleton(g);
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCOpenNISkeletonProviderDemo.class);
		myManager.settings().size(1500, 900);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}