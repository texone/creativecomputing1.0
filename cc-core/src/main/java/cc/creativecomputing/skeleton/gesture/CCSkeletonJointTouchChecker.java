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

import java.util.HashMap;
import java.util.Map;

import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCListenerManager;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.skeleton.CCSkeleton;
import cc.creativecomputing.skeleton.CCSkeletonJoint;
import cc.creativecomputing.skeleton.CCSkeletonJointType;
import cc.creativecomputing.skeleton.CCSkeletonManager.CCSkeletonManagerListener;

/**
 * Use the touch checker to detect touches of skeleton joints. 
 * This can be used to detect touches like claps. Note that there might be
 * better ways out there to do this but dependent on the right settings this seems to 
 * work so far.
 * <p>
 * @author christian riekoff
 *
 */
public class CCSkeletonJointTouchChecker implements CCSkeletonManagerListener{
	
	public static class Settings{
		@CCControl(name = "clap handdistance", min = 0, max = 100)
		private float _cClapDistance = 0;
		
		@CCControl(name = "clap time", min = 0, max = 10)
		private float _cClapTime = 0;
	}

	/**
	 * The listener interface for receiving "interesting" touches of joints.
	 * <P>
	 * The class that is interested in processing a touch implements this 
	 * interface and its onTouch method.
	 * <P>
	 * The listener object created from that class is then registered with the touch 
	 * checker using {@linkplain CCSkeletonJointTouchChecker#events()}. A change event is 
	 * generated when a touch is detected. Look at {@linkplain CCSkeletonJointTouchChecker}
	 * for more information on the process. When a change event occurs, the
	 * {@linkplain #onTouch(CCSkeletonJoint)} in the listener object is invoked, and the
	 * {@linkplain CCSkeletonJoint} is passed to it.
	 * 
	 * @author christian riekoff
	 * 
	 */
	public static interface CCSkeletonJointTouchListener {
		/**
		 * Invoked when a touch has been detected
		 * @param thePosition the position of the touch
		 */
		public void onTouch(CCVector3f thePosition);
	}

	/**
	 * class to calculate the angle between the three joints and detection of extremes
	 * @author christianr
	 *
	 */
	private class CCSkeletonTouchCalculator{
		private CCSkeleton _mySkeleton;
		
		private boolean _myIsInClap = false;
		@SuppressWarnings("unused")
		private float _myClapTime = 0;
		
		private CCSkeletonTouchCalculator(CCSkeleton theSkeleton){
			_mySkeleton = theSkeleton;
		}
		
		public void update(float theDeltaTime) {
			
			CCVector3f myLeftHandPosition  = _mySkeleton.joint(_myJointType0).position();
			CCVector3f myRightHandPosition  = _mySkeleton.joint(_myJointType1).position();
			
			if(myLeftHandPosition.distance(myRightHandPosition)< _mySettings._cClapDistance) {
				if(!_myIsInClap) {
					CCVector3f myClapPosition = CCVecMath.add( myLeftHandPosition, myRightHandPosition).scale(0.5f);
					_myEvents.proxy().onTouch(myClapPosition);
				}
				_myIsInClap = true;
			}else {
				_myIsInClap = false;
			}
			
			_myClapTime += theDeltaTime;
//			if(!_mySetTargets && _myClapTime > _mySettings._cClapTime) {
//				_mySetTargets = true;
//			}
		}
		
	}
	
	private CCSkeletonJointType _myJointType0;
	private CCSkeletonJointType _myJointType1;
	
	private Settings _mySettings;

	private CCListenerManager<CCSkeletonJointTouchListener> _myEvents = CCListenerManager.create(CCSkeletonJointTouchListener.class);
	private Map<Integer, CCSkeletonTouchCalculator> _mySkeletonMap = new HashMap<>();
	
	public CCSkeletonJointTouchChecker(
		CCSkeletonJointType theJointType0, 
		CCSkeletonJointType theJointType1
	) {
		_myJointType0 = theJointType0;
		_myJointType1 = theJointType1;
		
		_mySettings = new Settings();
	}
	
	public CCSkeletonJointTouchChecker(
		CCSkeleton theSkeleton, 
		CCSkeletonJointType theJointType0, 
		CCSkeletonJointType theJointType1
	){
		this(theJointType0, theJointType1);
		onNewSkeleton(theSkeleton);
	}

	public CCListenerManager<CCSkeletonJointTouchListener> events() {
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
		_mySkeletonMap.put(theSkeleton.id(), new CCSkeletonTouchCalculator(theSkeleton));
	}
	
	@Override
	public void onLostSkeleton(CCSkeleton theSkeleton) {
		_mySkeletonMap.remove(theSkeleton.id());
	}

	public void update(float theDeltaTime) {
		for(CCSkeletonTouchCalculator myProperties:_mySkeletonMap.values()){
			myProperties.update(theDeltaTime);
		}
	}
}
