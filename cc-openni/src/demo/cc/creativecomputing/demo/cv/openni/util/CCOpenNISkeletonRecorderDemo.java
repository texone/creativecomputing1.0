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
package cc.creativecomputing.demo.cv.openni.util;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.cv.openni.CCOpenNI;
import cc.creativecomputing.cv.openni.CCOpenNIUser;
import cc.creativecomputing.cv.openni.CCOpenNIUserGenerator;
import cc.creativecomputing.cv.openni.skeleton.CCOpenNISkeletonProvider;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.model.collada.CCColladaSkinController;
import cc.creativecomputing.model.collada.CCColladaLoader;
import cc.creativecomputing.model.collada.CCColladaScene;
import cc.creativecomputing.model.collada.CCColladaSkeleton;
import cc.creativecomputing.skeleton.CCSkeletonManager;
import cc.creativecomputing.skeleton.util.CCSkeletonRecorder;

/**
 * 
 * @author christianriekoff
 * 
 */
public class CCOpenNISkeletonRecorderDemo extends CCApp {
	
	@CCControl(name = "translate x", min = -2000, max = 2000)
	private float _cTranslateX = 0;
	
	@CCControl(name = "translate y", min = -2000, max = 2000)
	private float _cTranslateY = 0;
	
	@CCControl(name = "translate z", min = -2000, max = 2000)
	private float _cTranslateZ = 0;
	
	@CCControl(name = "rotate y", min = 0, max = CCMath.TWO_PI)
	private float _cRotateY = 0;
	

	private CCArcball _myArcball;

	private CCOpenNI _myOpenNI;
	private CCOpenNIUserGenerator _myUserGenerator;
	private CCSkeletonRecorder _myRecorder;

	public void setup() {
		addControls("app", "app", this);
		_myArcball = new CCArcball(this);
		
		CCColladaLoader myColladaLoader = new CCColladaLoader("humanoid.dae");
		
		CCColladaScene myScene = myColladaLoader.scenes().element(0);
		CCColladaSkinController mySkin = myColladaLoader.controllers().element(0).skin();
		CCColladaSkeleton mySkeleton = new CCColladaSkeleton(
			mySkin,
			myScene.node("bvh_import/Hips")
		);
	
		_myOpenNI = new CCOpenNI(this);
		//_myOpenNI.openFileRecording("demo/cv/openni/SkeletonRec.oni");

		_myUserGenerator = _myOpenNI.createUserGenerator();
		_myOpenNI.start();
		
		CCSkeletonManager mySkeletonManager = new CCSkeletonManager(
			this,
			mySkeleton, 
			new CCOpenNISkeletonProvider(_myUserGenerator)
		);
		_myRecorder = new CCSkeletonRecorder(mySkeletonManager, "export/skeleton/records");
		
		g.strokeWeight(3);
//		g.perspective(95, width / (float) height, 10, 150000);
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.CCApp#update(float)
	 */
	@Override
	public void update(float theDeltaTime) {
		_myOpenNI.transformationMatrix().reset();
		_myOpenNI.transformationMatrix().translate(_cTranslateX, _cTranslateY, _cTranslateZ);
		_myOpenNI.transformationMatrix().rotateY(_cRotateY);
		
		_myRecorder.update(theDeltaTime);
	}

	public void draw() {
		g.clear();

		g.pushMatrix();
		// set the scene pos
		_myArcball.draw(g);

		g.color(255);
		g.rotateY(180);
		for (CCOpenNIUser myUser : _myUserGenerator.user()) {
			myUser.drawSkeleton(g);
//			myUser.drawOrientations(g, 50);
//			myUser.drawVelocities(g,500);
		}
		
		
		g.popMatrix();
		
		g.text(frameRate,0,0);
		g.image(_myOpenNI.createDepthGenerator().texture(),-width/2, 0);
		g.color(255,0,0);
		if(_myRecorder.isRecording())g.rect(0,0,30,30);
	}

	
	@Override
	public void keyPressed(CCKeyEvent theKeyEvent) {
		switch(theKeyEvent.keyCode()) {
		case VK_R:
			_myRecorder.startRecording();
			break;
		case VK_S:
			_myRecorder.endRecording();
			break;
		default:
			break;
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCOpenNISkeletonRecorderDemo.class);
		myManager.settings().size(1200, 800);
		myManager.settings().antialiasing(8);
//		myManager.settings().frameRate(60);
		myManager.start();
	}
}
