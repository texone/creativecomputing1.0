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
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCPlane3f;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.model.collada.CCColladaLoader;
import cc.creativecomputing.model.collada.CCColladaScene;
import cc.creativecomputing.model.collada.CCColladaSkeleton;
import cc.creativecomputing.model.collada.CCColladaSkinController;
import cc.creativecomputing.net.CCUDPIn;
import cc.creativecomputing.net.codec.CCNetXMLCodec;
import cc.creativecomputing.skeleton.CCSkeleton;
import cc.creativecomputing.skeleton.CCSkeletonJointType;
import cc.creativecomputing.skeleton.CCSkeletonManager;
import cc.creativecomputing.skeleton.gesture.CCSkeletonTouchInteractionManager;
import cc.creativecomputing.skeleton.gesture.CCSkeletonTouchInteractionManager.CCTouchSource;
import cc.creativecomputing.skeleton.gesture.CCSkeletonTouchLocation;
import cc.creativecomputing.skeleton.gesture.CCSkeletonTouchLocation.CCSkeletonTouchLocationListener;
import cc.creativecomputing.skeleton.util.CCSkeletonReceiver;

public class CCOpenNITouchInteractionManagerDemo extends CCApp {
	
	private static boolean USE_SKELETON_SENDER = false;
	
	@CCControl(name = "openNI rotate X", min = -180, max = 180)
	private float _cRotateX = 0;

	@CCControl(name = "openNI translate X", min = -5000f, max = 5000f)
	private float _cOpenNIX = 0;

	@CCControl(name = "openNI translate Y", min = -5000f, max = 5000f)
	private float _cOpenNIY = 0;

	@CCControl(name = "openNI translate Z", min = -5000f, max = 5000f)
	private float _cOpenNIZ = 0;

	@CCControl(name = "openNI scale", min = 0.1f, max = 5f)
	private float _cOpenNIScale = 0;

	private CCOpenNI _myOpenNI;
	private CCOpenNIUserGenerator _myUserGenerator;
	
	private CCSkeletonManager _mySkeletonManager;
	private CCSkeletonTouchInteractionManager _myTouchInteractionManager;
	
	private CCSkeletonReceiver _myReceiver;
	
	private CCArcball _myArcball;
	
	@Override
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
		
		if(USE_SKELETON_SENDER){
			_myReceiver = new CCSkeletonReceiver(new CCUDPIn<>(new CCNetXMLCodec(), "127.0.0.1", 9000));
			_mySkeletonManager = new CCSkeletonManager(
				this,
				mySkeleton, 
				_myReceiver
			);
			_myReceiver.start();
		}else{
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
		
		_myTouchInteractionManager = new CCSkeletonTouchInteractionManager(
			this, 
			CCSkeletonJointType.LEFT_HAND, 
			CCSkeletonJointType.RIGHT_HAND
		);
		
		_mySkeletonManager.events().add(_myTouchInteractionManager);
		_myTouchInteractionManager.createMouseSource();
		
		CCSkeletonTouchLocationListener myListener = new CCSkeletonTouchLocationListener() {

			@Override
			public void onOver(CCSkeletonTouchLocation theLocation) {
//				CCLog.info("ON OVER:" + theLocation.name());
			}

			@Override
			public void onOut(CCSkeletonTouchLocation theLocation) {
//				CCLog.info("ON OUT:" + theLocation.name());
			}

			@Override
			public void onSelect(CCSkeletonTouchLocation theLocation) {
//				CCLog.info("ON SELECT:" + theLocation.name());
			}

			@Override
			public void onDeselect(CCSkeletonTouchLocation theLocation) {
//				CCLog.info("ON DESELECT:" + theLocation.name());
			}
		};
		
		
		for(int i = 0; i < 100;i++) {
			CCSkeletonTouchLocation myLocation = new CCSkeletonTouchLocation("loc:" + i, CCVecMath.random3f(300));
			myLocation.events().add(myListener);
			_myTouchInteractionManager.locations().add(myLocation);
		}
	}

	@Override
	public void update(final float theDeltaTime) {

		if(USE_SKELETON_SENDER){
			_myReceiver.update(theDeltaTime);
		}else{
			_myOpenNI.transformationMatrix().reset();
			_myOpenNI.transformationMatrix().translate(_cOpenNIX, _cOpenNIY, _cOpenNIZ);
			_myOpenNI.transformationMatrix().rotateX(CCMath.radians(_cRotateX));
			_myOpenNI.transformationMatrix().scale(_cOpenNIScale);
		}
		
		_mySkeletonManager.update(theDeltaTime);
		g.pushMatrix();
		_myArcball.draw(g);
		g.scale(0.5);
		g.camera().updateProjectionInfos();
		_myTouchInteractionManager.updateLocations(theDeltaTime);
		g.popMatrix();
		
	}

	@Override
	public void draw() {
		g.clear();
		g.color(255);
		_myTouchInteractionManager.draw(g);
		g.clearDepthBuffer();
		
		
		g.pushMatrix();
		_myArcball.draw(g);
		g.scale(0.5);
		g.color(255,0,0);
		g.pointSize(3);
		g.beginShape(CCDrawMode.POINTS);
		for(CCSkeletonTouchLocation myLocation:_myTouchInteractionManager.locations()) {
			g.vertex(myLocation.position());
		}
		g.endShape();
		for(CCSkeletonTouchLocation myLocation:_myTouchInteractionManager.locations()) {
			g.ellipse(myLocation.position(), myLocation.sourceProximity() * 100);
		}
		g.color(255);
		for (CCSkeleton mySkeleton : _mySkeletonManager.skeletons()) {
			mySkeleton.draw(g);
			CCPlane3f myBodyPlane = mySkeleton.bodyPlane();
			myBodyPlane.drawScale(100);
			myBodyPlane.draw(g);
		}
		for(CCTouchSource mySource:_myTouchInteractionManager.sources()){
			g.ellipse(mySource.position(), mySource.locationProximity() * 100);
		}
		g.popMatrix();
		
		
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCOpenNITouchInteractionManagerDemo.class);
		myManager.settings().size(1000, 800);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

