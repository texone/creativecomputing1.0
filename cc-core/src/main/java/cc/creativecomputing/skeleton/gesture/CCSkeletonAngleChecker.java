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
package cc.creativecomputing.skeleton.gesture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCListenerManager;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.skeleton.CCSkeleton;
import cc.creativecomputing.skeleton.CCSkeletonJoint;
import cc.creativecomputing.skeleton.CCSkeletonJointType;
import cc.creativecomputing.skeleton.CCSkeletonManager.CCSkeletonManagerListener;

/**
 * Use the angle checker to detect abrupt changes in the angle between skeleton joints. 
 * This can be used to detect motions like steps and throws. Note that there might be
 * better ways out there to do this but dependent on the right settings this seems to 
 * work so far.
 * <p>
 * @author christian riekoff
 *
 */
public class CCSkeletonAngleChecker implements CCSkeletonManagerListener{
	
	public static class Settings{
		@CCControl(name = "threshold", min = 0, max = 10000)
		private float _cThreshold = 0;

		@CCControl(name = "change thresh", min = 0, max = 50)
		private float _cChangeThresh = 1;

		@CCControl(name = "max time", min = 0, max = 1)
		private float _cMaxTime = 1;

		@CCControl(name = "draw scale", min = 0, max = 10)
		private float _cDrawScale = 1;

		@CCControl(name = "draw debug", min = 0, max = 10)
		private boolean _cDrawDebug = false;
		
		public float threshold(){
			return _cThreshold;
		}
		
		public void threshold(float theThreshold){
			_cThreshold = theThreshold;
		}
		
		public float changeThreshold(){
			return _cChangeThresh;
		}
		
		public void changeThreshold(float theThreshold){
			_cChangeThresh = theThreshold;
		}
		
		public float maxTime(){
			return _cMaxTime;
		}
		
		public void maxTime(float theMaxTime){
			_cMaxTime = theMaxTime;
		}
		
		public float drawScale(){
			return _cDrawScale;
		}
		
		public void drawScale(float theDrawScale){
			_cDrawScale = theDrawScale;
		}
	}

	/**
	 * The listener interface for receiving "interesting" extremes in angle changes.
	 * <P>
	 * The class that is interested in processing an angle change implements this 
	 * interface and its onChange method.
	 * <P>
	 * The listener object created from that class is then registered with the angle 
	 * checker using {@linkplain CCSkeletonAngleChecker#events()}. A change event is 
	 * generated when an extreme angle change is detected. Look at {@linkplain CCSkeletonAngleChecker}
	 * for more information on the process. When a change event occurs, the
	 * {@linkplain #onChange(CCSkeletonJoint)} in the listener object is invoked, and the
	 * {@linkplain CCSkeletonJoint} is passed to it.
	 * 
	 * @author christian riekoff
	 * 
	 */
	public static interface CCSkeletonAngleChangeListener {
		/**
		 * Invoked when an angle extreme has been detected
		 * @param theJoint last joint of the three joints for angle detected
		 */
		public void onChange(CCSkeletonJoint theJoint);
	}
	
	/**
	 * Creates an angle checker on the left leg. This can be used to check when a skeleton makes a step.
	 * @return an angle checker on the left leg
	 */
	public static CCSkeletonAngleChecker createLeftLeg() {
		return new CCSkeletonAngleChecker(CCSkeletonJointType.LEFT_HIP, CCSkeletonJointType.LEFT_KNEE, CCSkeletonJointType.LEFT_FOOT);
	}
	
	/**
	 * Creates an angle checker on the right leg. This can be used to check when a skeleton makes a step.
	 * @return an angle checker on the right leg
	 */
	public static CCSkeletonAngleChecker createRightLeg() {
		return new CCSkeletonAngleChecker(CCSkeletonJointType.RIGHT_HIP, CCSkeletonJointType.RIGHT_KNEE, CCSkeletonJointType.RIGHT_FOOT);
	}
	
	/**
	 * Creates an angle checker on the left arm. This can be used to check when a skeleton makes a throw.
	 * @return an angle checker on the left arm
	 */
	public static CCSkeletonAngleChecker createLeftArm() {
		return new CCSkeletonAngleChecker(CCSkeletonJointType.LEFT_SHOULDER, CCSkeletonJointType.LEFT_ELBOW, CCSkeletonJointType.LEFT_HAND);
	}
	
	/**
	 * Creates an angle checker on the left arm. This can be used to check when a skeleton makes a throw.
	 * @return an angle checker on the left arm
	 */
	public static CCSkeletonAngleChecker createRightArm() {
		return new CCSkeletonAngleChecker(CCSkeletonJointType.RIGHT_SHOULDER, CCSkeletonJointType.RIGHT_ELBOW, CCSkeletonJointType.RIGHT_HAND);
	}

	/**
	 * class to calculate the angle between the three joints and detection of extremes
	 * @author christianr
	 *
	 */
	private class CCSkeletonAngleCalculator{
		private CCSkeleton _mySkeleton;
		
		private List<Float> _myAngles = new ArrayList<Float>();
		
		private boolean _myIsRising = false;
		private boolean _myHasExtrema = false;
		private float _myLastExtrema = 0;
		private float _myRiseTime = 0;
		private float _myLastAngle = 0;
		
		private CCSkeletonAngleCalculator(CCSkeleton theSkeleton){
			_mySkeleton = theSkeleton;
		}
		
		public void update(float theDeltaTime) {
			
			CCVector3f myVec0 = _mySkeleton.joint(_myJointType0).position().clone();
			CCVector3f myVec1 = _mySkeleton.joint(_myJointType1).position().clone();
			CCVector3f myVec2 = _mySkeleton.joint(_myJointType2).position().clone();

			if (_myAngles.size() > 1000)
				_myAngles.remove(0);
			
			float myAngle = CCMath.degrees(CCVecMath.angle(CCVecMath.subtract(myVec1, myVec2).normalize(), CCVecMath.subtract(myVec1, myVec0).normalize()));
			
			if(_myLastAngle == 0) {
				_myLastAngle = myAngle;
				_myLastExtrema = myAngle;
				return;
			}
			
			float myChange = 0;
			_myRiseTime += theDeltaTime;
			
			if(_myIsRising) {
				myChange = (_myLastAngle - _myLastExtrema);
				if(_myLastAngle > myAngle) {
					_myIsRising = false;
					_myLastExtrema = _myLastAngle;
				}
			}else {
				if(_myLastAngle < myAngle) {
					_myIsRising = true;
					 _myHasExtrema = false;
					_myRiseTime = 0;
					_myLastExtrema = _myLastAngle;
				}
			}

			_myLastAngle = myAngle;
			
			if(
				!_myHasExtrema && 
				myChange > _mySettings.changeThreshold() && 
				myAngle > _mySettings.threshold() && 
				_myRiseTime < _mySettings.maxTime()
			) { // && _myTimeSinceLastThrow > _cThrowBreak
				_myAngles.add(300f);
				_myHasExtrema = true;
				_myEvents.proxy().onChange(_mySkeleton.joint(_myJointType2));
			}else {
				_myAngles.add(myAngle);
			}
		}
		
		public void drawValues(CCGraphics g, int theWidth) {
			float myX = 0;
			
			myX = 0;
			g.beginShape(CCDrawMode.LINE_STRIP);
			for (float myValue : _myAngles) {
				g.vertex(myX * 1 - theWidth / 2, myValue * _mySettings.drawScale());
				myX++;
			}
			g.endShape();
			g.line(-theWidth / 2, _mySettings.threshold() * _mySettings.drawScale(), theWidth / 2, _mySettings.threshold() * _mySettings.drawScale());
			
			g.color(255);
			g.line(-theWidth / 2, 0, theWidth / 2, 0);

		}

		public void draw(CCGraphics g) {
			g.ellipse(_mySkeleton.joint(_myJointType2).position(), _myRadius * 100);
		}
	}
	
	private CCSkeletonJointType _myJointType0;
	private CCSkeletonJointType _myJointType1;
	private CCSkeletonJointType _myJointType2;
	
	private Settings _mySettings;

	private float _myRadius = 0;

	private CCListenerManager<CCSkeletonAngleChangeListener> _myEvents = CCListenerManager.create(CCSkeletonAngleChangeListener.class);
	private Map<Integer, CCSkeletonAngleCalculator> _mySkeletonMap = new HashMap<>();
	
	public CCSkeletonAngleChecker(
		CCSkeletonJointType theJointType0, 
		CCSkeletonJointType theJointType1, 
		CCSkeletonJointType theJointType2
	) {
		_myJointType0 = theJointType0;
		_myJointType1 = theJointType1;
		_myJointType2 = theJointType2;
		
		_mySettings = new Settings();
	}
	
	public CCSkeletonAngleChecker(
		CCSkeleton theSkeleton, 
		CCSkeletonJointType theJointType0, 
		CCSkeletonJointType theJointType1, 
		CCSkeletonJointType theJointType2
	){
		this(theJointType0, theJointType1, theJointType2);
		onNewSkeleton(theSkeleton);
	}

	public CCListenerManager<CCSkeletonAngleChangeListener> events() {
		return _myEvents;
	}
	
	public Settings settings(){
		return _mySettings;
	}
	
	public void settings(Settings theSettings){
		_mySettings = theSettings;
	}
	
	@Override
	public void onNewSkeleton(CCSkeleton theSkeleton) {
		_mySkeletonMap.put(theSkeleton.id(), new CCSkeletonAngleCalculator(theSkeleton));
	}
	
	@Override
	public void onLostSkeleton(CCSkeleton theSkeleton) {
		_mySkeletonMap.remove(theSkeleton.id());
	}

	public void update(float theDeltaTime) {
		for(CCSkeletonAngleCalculator myProperties:_mySkeletonMap.values()){
			myProperties.update(theDeltaTime);
		}
	}

	public void drawValues(CCGraphics g, int theWidth) {
		if(!_mySettings._cDrawDebug)return;
		
		for(CCSkeletonAngleCalculator myProperties:_mySkeletonMap.values()){
			myProperties.drawValues(g, theWidth);
		}
	}

	public void draw(CCGraphics g) {
		for(CCSkeletonAngleCalculator myProperties:_mySkeletonMap.values()){
			myProperties.draw(g);
		}
	}
}
