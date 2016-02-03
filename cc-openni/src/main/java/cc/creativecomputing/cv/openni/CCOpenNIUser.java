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
package cc.creativecomputing.cv.openni;

import java.nio.ShortBuffer;
import java.util.ArrayList;

import org.openni.SkeletonCapability;
import org.openni.SkeletonJoint;
import org.openni.SkeletonJointPosition;
import org.openni.StatusException;

import cc.creativecomputing.math.CCMatrix4f;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.util.logging.CCLog;

public class CCOpenNIUser extends CCOpenNIDataProvider{
		private SkeletonCapability _mySkeletonCapability;
		
		private ShortBuffer _myUserRaw;
		
		public CCOpenNIUser(int theID, CCMatrix4f theTransformationMatrix, SkeletonCapability theSkeletonCapability) {
			_myID = theID;
			_myTransformationMatrix = theTransformationMatrix;
			_mySkeletonCapability = theSkeletonCapability;
			
			for(CCOpenNIUserJointType myType:CCOpenNIUserJointType.values()) {
				if(_mySkeletonCapability.isJointActive(myType._myJoint)) {
					_myJointMap.put(myType, new CCOpenNIUserJoint(myType));
				}
			}
			
			_myUserJoints = new ArrayList<CCOpenNIUserJoint>(_myJointMap.values()); 
			
			for(CCOpenNIUserJointType[] myLimbJoints:limbs) {
				_myLimbs.add(new CCOpenNIUserLimb(_myJointMap.get(myLimbJoints[0]), _myJointMap.get(myLimbJoints[1])));
			}
			
			update(0);
		}
		
		public SkeletonJointPosition position(SkeletonJoint theJoint) throws StatusException {
			return _mySkeletonCapability.getSkeletonJointPosition(_myID, theJoint);
		}
		
		private float _mySize = -1;
		
		public float size(){
			return _mySize;
		}
		
		@Override
		public void update(float theDeltaTime) {
			_myIsCalibrated = _mySkeletonCapability.isSkeletonCalibrated(_myID);
			_myIsTracking = _mySkeletonCapability.isSkeletonTracking(_myID);
//			if(!needsUpdate())return;
			
			if(_mySize < 0)_mySize = getCalculatedSize();
			else _mySize = _mySize * 0.95f + 0.05f * getCalculatedSize();

			try {
				for (CCOpenNIUserJoint myJoint : _myJointMap.values()) {
					SkeletonJointPosition myPosition = _mySkeletonCapability.getSkeletonJointPosition(_myID, myJoint.type()._myJoint);
					float myPositionConfidence =  myPosition == null ? 0 : myPosition.getConfidence();
					
					CCVector3f myNewPosition = new CCVector3f(
						myPosition.getPosition().getX(),
						myPosition.getPosition().getY(),
						myPosition.getPosition().getZ()
					);
					
					_myTransformationMatrix.inverseTransform(myNewPosition);
					
					myJoint.updatePosition(
						myNewPosition.x, 
						myNewPosition.y, 
						myNewPosition.z, 
						myPositionConfidence
					);
					
				}
			} catch (StatusException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		private float distance(CCOpenNIUserJointType theJoint1, CCOpenNIUserJointType theJoint2){
			CCVector3f myJoint1 = _myJointMap.get(theJoint1).position();
			CCVector3f myJoint2 = _myJointMap.get(theJoint2).position();
			return  myJoint1.distance(myJoint2);
		}
		
		private float distance(CCOpenNIUserJointType theJoint1, CCOpenNIUserJointType theJoint2, CCOpenNIUserJointType theJoint3){
			CCVector3f myJoint1 = _myJointMap.get(theJoint1).position();
			CCVector3f myJoint2 = CCVecMath.add(_myJointMap.get(theJoint2).position(), _myJointMap.get(theJoint2).position()).scale(0.5f);
			return  myJoint1.distance(myJoint2);
		}
		
		public float getCalculatedSize() {
			float mySize = 0;
			mySize += distance(CCOpenNIUserJointType.HEAD, CCOpenNIUserJointType.NECK);
			mySize += distance(CCOpenNIUserJointType.NECK, CCOpenNIUserJointType.TORSO);
			mySize += distance(CCOpenNIUserJointType.TORSO, CCOpenNIUserJointType.LEFT_HIP, CCOpenNIUserJointType.RIGHT_HIP);
			mySize += distance(CCOpenNIUserJointType.LEFT_HIP, CCOpenNIUserJointType.LEFT_KNEE);
			mySize += distance(CCOpenNIUserJointType.LEFT_KNEE, CCOpenNIUserJointType.LEFT_FOOT);
			return mySize;
		}

		@Override
		public boolean needsUpdate() {
			return true;
		}
		
		public ShortBuffer raw() {
			return _myUserRaw;
		}
		
		void raw(ShortBuffer theUserRaw) {
			_myUserRaw = theUserRaw;
		}

		/**
		 * @param theUpdatePixels
		 */
		public void updatePixels(boolean theUpdatePixels) {
			// TODO Auto-generated method stub
			
		}
	}
