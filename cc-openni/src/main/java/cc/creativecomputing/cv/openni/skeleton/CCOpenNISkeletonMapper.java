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
package cc.creativecomputing.cv.openni.skeleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.cv.openni.CCOpenNIDataProvider;
import cc.creativecomputing.cv.openni.CCOpenNIUser;
import cc.creativecomputing.cv.openni.CCOpenNIUserJointType;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.skeleton.CCSkeleton;
import cc.creativecomputing.skeleton.CCSkeletonJoint;
import cc.creativecomputing.skeleton.CCSkeletonJointType;
import cc.creativecomputing.skeleton.CCSkeletonTransformCalculator;
import cc.creativecomputing.util.logging.CCLog;

/**
 * Note that this implementation is totally dependent on a provided collada model, instead of that
 * we should implement a pipeline to define a skeleton and export it to bvh files to do vertex skinning.
 * @author christianriekoff
 *
 */
public class CCOpenNISkeletonMapper{
	
	
	private CCOpenNIUser _myUser;
	
	private CCSkeleton _mySkeleton;
	
	private Map<CCSkeletonJointType, CCOpenNISkeletonJoint> _myJointMap;
	private Map<String, CCVector3f> _myMap = new HashMap<>();
	
	private CCSkeletonTransformCalculator _myTransformCalculator;
	
	private List<CCLimb> _myLimbs = new ArrayList<>();
	
	private boolean _myFixLimbLengths = true;
	
	private boolean _myRescaleSize = true;
	
	private CCOpenNISkeletonLegFixer _myLegFixer;
	
	public CCOpenNISkeletonMapper(CCOpenNIUser theUser, CCSkeleton theSkeleton, CCOpenNISkeletonProvider theOpenNISkeletonProvider) {
		_mySkeleton = theSkeleton;
		_mySkeleton.id(theUser.id());
		_myUser = theUser;
		mapJoints(_myUser);
		CCLog.info("ROOT:" + _mySkeleton.rootJoint().id());
		createLimbs(_mySkeleton.rootJoint());
		_myTransformCalculator = new CCSkeletonTransformCalculator(_mySkeleton, theOpenNISkeletonProvider.filter());
		_myLegFixer = theOpenNISkeletonProvider.legFixer();
	}
	
	public void fixLimbLength(boolean theFixLimbLength){
		_myFixLimbLengths = theFixLimbLength;
	}
	
	public void rescaleSize(boolean theRescaleSize){
		_myRescaleSize = theRescaleSize;
	}
	
	private void createLimbs(CCSkeletonJoint theJoint){
		for(CCSkeletonJoint myChild:theJoint.childJoints()){
			_myLimbs.add(new CCLimb(theJoint.id(), myChild.id(), theJoint.position().distance(myChild.position())));
			createLimbs(myChild);
		}
	}
	
	/*
	 * MAPPING
	 * Hips				LEFT_HIP <-> RIGHT_HIP
	 * LeftUpLeg		LEFT_HIP
	 * LeftLeg			LEFT_KNEE
	 * LeftFoot			LEFT_FOOT
	 * RightUpLeg		RIGHT_HIP
	 * RightLeg			RIGHT_KNEE
	 * RightFoot		RIGHT_FOOT
	 * Spine			TORSO
	 * LeftShoulder		
	 * LeftArm			LEFT_SHOULDER
	 * LeftForeArm		LEFT_ELBOW
	 * LeftHand			LEFT_HAND
	 * RightShoulder
	 * RightArm			RIGHT_SHOULDER
	 * RightForeArm		RIGHT_ELBOW
	 * RightHand		RIGHT_HAND
	 * Neck				NECK
	 * Head				HEAD
	 */
	private void mapJoints(CCOpenNIDataProvider theUser) {
		CCOpenNISkeletonJoint myLeftHipJoint;
		CCOpenNISkeletonJoint myRightHipJoint;
		CCOpenNISkeletonJoint myLeftLeftJoint;
		CCOpenNISkeletonJoint myRightLegJoint;
		CCOpenNISkeletonJoint myTorsoJoint;
		CCOpenNISkeletonJoint myHeadJoint;
		CCOpenNISkeletonJoint myNeckJoint;
		CCOpenNISkeletonJoint myLeftArmJoint;
		CCOpenNISkeletonJoint myRightArmJoint;
		
		
		_myJointMap = new HashMap<>();
		_myJointMap.put(CCSkeletonJointType.HEAD, myHeadJoint = new CCOpenNIDirectSkeletonJoint(theUser.joint(CCOpenNIUserJointType.HEAD), CCSkeletonJointType.HEAD));
		_myJointMap.put(CCSkeletonJointType.SPINE, myTorsoJoint = new CCOpenNIDirectSkeletonJoint(theUser.joint(CCOpenNIUserJointType.TORSO), CCSkeletonJointType.SPINE));

		_myJointMap.put(CCSkeletonJointType.RIGHT_HAND, new CCOpenNIDirectSkeletonJoint(theUser.joint(CCOpenNIUserJointType.RIGHT_HAND), CCSkeletonJointType.RIGHT_HAND));
		_myJointMap.put(CCSkeletonJointType.RIGHT_ELBOW, new CCOpenNIDirectSkeletonJoint(theUser.joint(CCOpenNIUserJointType.RIGHT_ELBOW), CCSkeletonJointType.RIGHT_ELBOW));
		_myJointMap.put(CCSkeletonJointType.RIGHT_ARM, myRightArmJoint = new CCOpenNIDirectSkeletonJoint(theUser.joint(CCOpenNIUserJointType.RIGHT_SHOULDER), CCSkeletonJointType.RIGHT_ARM));

		_myJointMap.put(CCSkeletonJointType.LEFT_HAND, new CCOpenNIDirectSkeletonJoint(theUser.joint(CCOpenNIUserJointType.LEFT_HAND), CCSkeletonJointType.LEFT_HAND));
		_myJointMap.put(CCSkeletonJointType.LEFT_ELBOW, new CCOpenNIDirectSkeletonJoint(theUser.joint(CCOpenNIUserJointType.LEFT_ELBOW), CCSkeletonJointType.LEFT_ELBOW));
		_myJointMap.put(CCSkeletonJointType.LEFT_ARM, myLeftArmJoint = new CCOpenNIDirectSkeletonJoint(theUser.joint(CCOpenNIUserJointType.LEFT_SHOULDER), CCSkeletonJointType.LEFT_ARM));

		
		_myJointMap.put(CCSkeletonJointType.LEFT_KNEE, myLeftLeftJoint = new CCOpenNIDirectSkeletonJoint(theUser.joint(CCOpenNIUserJointType.LEFT_KNEE), CCSkeletonJointType.LEFT_KNEE));
		_myJointMap.put(CCSkeletonJointType.RIGHT_KNEE, myRightLegJoint = new CCOpenNIDirectSkeletonJoint(theUser.joint(CCOpenNIUserJointType.RIGHT_KNEE), CCSkeletonJointType.RIGHT_KNEE));
		CCOpenNISkeletonJoint myLeftFootJoint = new CCOpenNIDirectSkeletonJoint(theUser.joint(CCOpenNIUserJointType.LEFT_FOOT), CCSkeletonJointType.LEFT_FOOT);
		CCOpenNISkeletonJoint myRightFootJoint = new CCOpenNIDirectSkeletonJoint(theUser.joint(CCOpenNIUserJointType.RIGHT_FOOT), CCSkeletonJointType.RIGHT_FOOT);
		
		CCOpenNISkeletonJoint myBlendRightHipJoint = new CCOpenNIDirectSkeletonJoint(theUser.joint(CCOpenNIUserJointType.RIGHT_HIP), CCSkeletonJointType.RIGHT_HIP);
		CCOpenNISkeletonJoint myBlendLeftHipJoint = new CCOpenNIDirectSkeletonJoint(theUser.joint(CCOpenNIUserJointType.LEFT_HIP), CCSkeletonJointType.LEFT_HIP);
		
		_myJointMap.put(CCSkeletonJointType.RIGHT_HIP, myRightHipJoint = new CCOpenNIBlendedSkeletonJoint(myBlendRightHipJoint, myRightLegJoint, 0.1f, CCSkeletonJointType.RIGHT_HIP));
		_myJointMap.put(CCSkeletonJointType.LEFT_HIP, myLeftHipJoint = new CCOpenNIBlendedSkeletonJoint(myBlendLeftHipJoint, myLeftLeftJoint, 0.1f, CCSkeletonJointType.LEFT_HIP));
		
		_myJointMap.put(CCSkeletonJointType.LEFT_FOOT, new CCOpenNIBlendedSkeletonJoint(myLeftLeftJoint, myLeftFootJoint, 1.1f, CCSkeletonJointType.LEFT_FOOT));
		_myJointMap.put(CCSkeletonJointType.RIGHT_FOOT, new CCOpenNIBlendedSkeletonJoint(myRightLegJoint, myRightFootJoint, 1.1f, CCSkeletonJointType.RIGHT_FOOT));
		
		_myJointMap.put(CCSkeletonJointType.HIPS, new CCOpenNIBlendedSkeletonJoint(myLeftHipJoint, myRightHipJoint, 0.5f, CCSkeletonJointType.HIPS));
		_myJointMap.put(CCSkeletonJointType.NECK, myNeckJoint = new CCOpenNIBlendedSkeletonJoint(myHeadJoint, myTorsoJoint, 0.4f, CCSkeletonJointType.NECK));
		_myJointMap.put(CCSkeletonJointType.LEFT_SHOULDER, new CCOpenNIBlendedSkeletonJoint(myNeckJoint, myLeftArmJoint, 0.3f, CCSkeletonJointType.LEFT_SHOULDER));
		_myJointMap.put(CCSkeletonJointType.RIGHT_SHOULDER,new CCOpenNIBlendedSkeletonJoint(myNeckJoint, myRightArmJoint, 0.3f, CCSkeletonJointType.RIGHT_SHOULDER));
		
	}
	
	private class CCLimb{
		private String myID1;
		private String myID2;
		private float _myLength;
		
		private CCVector3f _myDirection;
		
		public CCLimb(String theID1, String theID2, float theLength){
			myID1 = theID1;
			myID2 = theID2;
			_myLength = theLength;
		}
		
		private void calcDirection(){
			_myDirection = CCVecMath.subtract(_myMap.get(myID2), _myMap.get(myID1)).normalize();
		}
		
		private void calcPosition(){
			CCVector3f myPos = _myMap.get(myID2);
			myPos.set(_myDirection);
			myPos.scale(_myLength * _myRescale);
			myPos.add(_myMap.get(myID1).position());
		}
	}
	
	private float _myRescale = 1f;
	
	public void update(final float theDeltaTime) {
		_myLegFixer.update(theDeltaTime);
		_myLegFixer.fixPositions(_myUser);
		
		for(CCOpenNISkeletonJoint myJoint:_myJointMap.values()){
			_myMap.put(myJoint.type().colladaID(), myJoint.position().clone());
		}
		
		if(_myFixLimbLengths){
			if(_myRescaleSize){
				_myRescale = _myUser.getCalculatedSize() / 161.1f;
			}else{
				_myRescale = 10;
			}
			for(CCLimb myLimb:_myLimbs){
				myLimb.calcDirection();
			}
			for(CCLimb myLimb:_myLimbs){
				myLimb.calcPosition();
			}
		}
		for(CCOpenNISkeletonJoint myJoint:_myJointMap.values()) {
			_myTransformCalculator.position(myJoint.type().colladaID(), _myMap.get(myJoint.type().colladaID()), theDeltaTime);
		}
		_myTransformCalculator.update(theDeltaTime);
	}
	
	public void time(float theTime) {
		
	}
	
	public CCSkeleton skeleton() {
		return _mySkeleton;
	}
	
}
