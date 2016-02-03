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
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.cv.openni.CCOpenNI;
import cc.creativecomputing.cv.openni.CCOpenNIDepthGenerator;
import cc.creativecomputing.cv.openni.CCOpenNIUserGenerator;
import cc.creativecomputing.cv.openni.skeleton.CCOpenNISkeletonProvider;
import cc.creativecomputing.cv.openni.util.CCOpenNIFloorPlaneDetector;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.model.collada.CCColladaSkeletonUtil;
import cc.creativecomputing.skeleton.CCSkeleton;
import cc.creativecomputing.skeleton.CCSkeletonManager;
import cc.creativecomputing.skeleton.CCSkeletonManager.CCSkeletonManagerListener;
import cc.creativecomputing.skeleton.util.CCSkeletonInteractionArea;
import cc.creativecomputing.util.logging.CCLog;

public class CCOpenNIInteractionAreaDemo extends CCApp {
	
	@CCControl(name = "direct alpha", min = 0, max = 1)
	private float _cDirectAlpha;
	
	@CCControl(name = "box alpha", min = 0, max = 1)
	private float _cBoxAlpha;
	
	@CCControl(name = "openNI rotate X", min = -180, max = 180)
	private float _cRotateX = 0;

	@CCControl(name = "openNI translate X", min = -5000f, max = 5000f)
	private float _cOpenNIX = 0;

	@CCControl(name = "openNI translate Y", min = -5000f, max = 5000f)
	private float _cOpenNIY = 0;

	@CCControl(name = "openNI translate Z", min = -5000f, max = 5000f)
	private float _cOpenNIZ = 0;

	@CCControl(name = "openNI scale", min = 0.1f, max = 1f)
	private float _cOpenNIScale = 0;
	
	@CCControl(name = "use floor plane detector")
	private boolean _cUseFloorPlaneDetector = true;

	private CCOpenNI _myOpenNI;
	private CCOpenNIDepthGenerator _myDepthGenerator;
	private CCOpenNIUserGenerator _myUserGenerator;
	
	private CCArcball _myArcball;
	
	private CCOpenNIFloorPlaneDetector _myFloorPlaneDetector;
	private CCSkeletonInteractionArea _myInteractionArea;
	private CCSkeletonManager _mySkeletonManager;
	
	@Override
	public void setup() {
		_myOpenNI = new CCOpenNI(this);
		_myOpenNI.openFileRecording("SkeletonRec.oni");
		_myDepthGenerator = _myOpenNI.createDepthGenerator();
		
		_myUserGenerator = _myOpenNI.createUserGenerator();
		_myOpenNI.start();
		
		CCColladaSkeletonUtil myColladaSkeletonUtil = new CCColladaSkeletonUtil("humanoid.dae", "bvh_import/Hips");
		
		CCSkeleton _mySkeleton = myColladaSkeletonUtil.skeleton();
		
		_mySkeletonManager = new CCSkeletonManager(
			this,
			_mySkeleton, 
			new CCOpenNISkeletonProvider(_myUserGenerator)
		);
		
		_myInteractionArea = new CCSkeletonInteractionArea(_mySkeletonManager);
		_myInteractionArea.events().add(new CCSkeletonManagerListener() {
			
			@Override
			public void update(float theDeltaTime) {
			}
			
			@Override
			public void onNewSkeleton(CCSkeleton theSkeleton) {
				CCLog.info("ENTER");
			}
			
			@Override
			public void onLostSkeleton(CCSkeleton theSkeleton) {
				CCLog.info("LEAVE");
			}
		});
		
		_myArcball = new CCArcball(this);
		
		_myFloorPlaneDetector = new CCOpenNIFloorPlaneDetector(_myOpenNI);
		
		addControls("app", "floorplane", _myFloorPlaneDetector);
		addControls("app", "area",1, _myInteractionArea);
		addControls("app", "app",0, this);
	}

	@Override
	public void update(final float theDeltaTime) {
		_myFloorPlaneDetector.update(theDeltaTime);
		_mySkeletonManager.update(theDeltaTime);
		_myInteractionArea.update(theDeltaTime);
		if(_cUseFloorPlaneDetector) {
			_myOpenNI.transformationMatrix().set(_myFloorPlaneDetector.transformation());
		}else {
			_myOpenNI.transformationMatrix().reset();
			_myOpenNI.transformationMatrix().translate(_cOpenNIX, _cOpenNIY, _cOpenNIZ);
			_myOpenNI.transformationMatrix().rotateX(CCMath.radians(_cRotateX));
			_myOpenNI.transformationMatrix().scale(_cOpenNIScale);
		}
	}
	
	int myBlends = 0;

	@Override
	public void draw() {
		g.clear();
		g.pushMatrix();
		_myArcball.draw(g);
				
		g.scale(0.1f);
		
		CCVector3f[] myPoints = _myDepthGenerator.depthMapRealWorld(4);
		g.color(255);
		g.beginShape(CCDrawMode.POINTS);
		for(CCVector3f myPoint:myPoints) {
			g.vertex(myPoint);
		}
		g.endShape();
			
		g.color(255,0,0);
		_myInteractionArea.draw(g);
		_myFloorPlaneDetector.draw(g);
			
		for(CCSkeleton mySkeleton:_mySkeletonManager.skeletons()){
			g.color(255);
			mySkeleton.draw(g);
			mySkeleton.drawOrientations(g, 10);
			g.color(255);
		}
			
		_myOpenNI.drawCamFrustum(g);
		
		g.popMatrix();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCOpenNIInteractionAreaDemo.class);
		myManager.settings().size(1500, 800);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

