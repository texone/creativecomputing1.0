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
package cc.creativecomputing.skeleton;

import java.util.HashMap;
import java.util.Map;

import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix4f;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.signal.filter.CCFilterManager;
import cc.creativecomputing.math.signal.filter.CCFilterManager.CCFilteredValue;

/*
 * 
 */
public class CCSkeletonTransformCalculator {
	
	@CCControl(name = "hand rotation", min = -180, max = 180)
	public static float _cHandRotation = 0;
	
	@CCControl(name = "foot rotation", min = -180, max = 180)
	public static float _cFootRotation = -25;
	
	private static class CCRotation{
		@CCControl(name = "x rotation", min = -180, max = 180)
		public float _cXRotation = 0;

		@CCControl(name = "y rotation", min = -180, max = 180)
		public float _cYRotation = 0;

		@CCControl(name = "z rotation", min = -180, max = 180)
		public float _cZRotation = 0;
	}
	
	private CCSkeleton _mySkeleton;
	
	private class CCSkeletonJointFilter{
		private CCFilteredValue _myXValue;
		private CCFilteredValue _myYValue;
		private CCFilteredValue _myZValue;
		
		private CCVector3f _myLastPosition = new CCVector3f();
		private CCVector3f _myPosition = new CCVector3f();
		
		private CCSkeletonJointFilter(){
			_myFilterManager.addListener(_myXValue = new CCFilteredValue());
			_myFilterManager.addListener(_myYValue = new CCFilteredValue());
			_myFilterManager.addListener(_myZValue = new CCFilteredValue());
		}
		
		private void position(CCVector3f thePosition, float theDeltaTime){
			if(_myLastPosition.equals(thePosition)){
				return;
			}
			_myXValue.value(thePosition.x,theDeltaTime);
			_myYValue.value(thePosition.y,theDeltaTime);
			_myZValue.value(thePosition.z,theDeltaTime);
			
			_myPosition.set(
				_myXValue.value(),
				_myYValue.value(),
				_myZValue.value()
			);
			_myLastPosition.set(thePosition);
		}
		
		private CCVector3f position(){
			return _myPosition;
		}
	}
	
	private Map<String, CCSkeletonJointFilter> _myPositionMap = new HashMap<>();
	
	private CCFilterManager _myFilterManager;

	public CCSkeletonTransformCalculator(CCSkeleton theSkeleton, CCFilterManager theFilter) {
		_mySkeleton = theSkeleton;
		_myFilterManager = theFilter;
		for(CCSkeletonJoint myJoint:_mySkeleton.joints()){
			_myPositionMap.put(myJoint._myID, new CCSkeletonJointFilter());
		}
	}
	
	public CCFilterManager filter(){
		return _myFilterManager;
	}
	
	public void position(String theJointID, CCVector3f thePosition, float theDeltaTime){
		CCSkeletonJointFilter myFilter = _myPositionMap.get(theJointID);
		if(myFilter == null)return;
		myFilter.position(thePosition, theDeltaTime);
	}

	protected CCVector3f direction(String theID0, String theID1) {
		CCVector3f myVector0 = _myPositionMap.get(theID0).position();
		CCVector3f myVector1 = _myPositionMap.get(theID1).position();

		return CCVecMath.subtract(myVector1, myVector0).normalize();
	}

	protected CCMatrix4f buildRotationMatrix(CCVector3f theXAxis, CCVector3f theYAxis, CCVector3f theZAxis) {
		return new CCMatrix4f(theXAxis.normalize(), theYAxis.normalize(), theZAxis.normalize());
	}

	protected CCMatrix4f makeOrientationFromX(CCVector3f theVector) {
		// matrix column
		CCVector3f myXCol = new CCVector3f();
		CCVector3f myYCol = new CCVector3f();
		CCVector3f myZCol = new CCVector3f();

		myXCol = theVector.clone().normalize();

		myYCol.x = 0.0f;
		myYCol.y = myXCol.z;
		myYCol.z = -myXCol.y;

		myYCol.normalize();

		myZCol = myXCol.cross(myYCol);

		return buildRotationMatrix(myXCol, myYCol, myZCol);
	}

	protected CCMatrix4f makeOrientationFromY(CCVector3f theVector) {
		// matrix column
		CCVector3f myXCol = new CCVector3f();
		CCVector3f myYCol = new CCVector3f();
		CCVector3f myZCol = new CCVector3f();
		
		myYCol = theVector.clone().normalize();

		myXCol.x = myYCol.y;
		myXCol.y = -myYCol.x;
		myXCol.z = 0.0f;

		myXCol.normalize();

		myZCol = myXCol.cross(myYCol); // do not normalize
		
		return buildRotationMatrix(myXCol, myYCol, myZCol);
	}

	protected CCMatrix4f makeOrientationFromZ(CCVector3f theVector) {
		// matrix column
		CCVector3f myXCol = new CCVector3f();
		CCVector3f myYCol = new CCVector3f();
		CCVector3f myZCol = new CCVector3f();

		myZCol = theVector.clone().normalize();

		myXCol.x = myZCol.y;
		myXCol.y = -myZCol.x;
		myXCol.z = 0.0f;

		myXCol.normalize();

		myYCol = myZCol.cross(myXCol); // do not normalize

		return buildRotationMatrix(myXCol, myYCol, myZCol);
	}

	protected CCMatrix4f makeOrientationFromXY(CCVector3f theUnnormalizedX, CCVector3f theUnnormalizedY) {
		CCVector3f myX = theUnnormalizedX.clone().normalize();
		CCVector3f myY = theUnnormalizedY.clone().normalize();
		CCVector3f myZ = myY.cross(myX);
		myY = myX.cross(myZ);
		return buildRotationMatrix(myX, myY, myZ);
	}

	protected CCMatrix4f makeOrientationFromXZ(CCVector3f theUnnormalizedX, CCVector3f theUnnormalizedZ) {
		
		CCVector3f myX = theUnnormalizedX.clone().normalize();
		CCVector3f myZ = theUnnormalizedZ.clone().normalize();
		CCVector3f myY = myX.cross(myZ);
		myZ = myY.cross(myX);
		return buildRotationMatrix(myX, myY, myZ);
	}

	protected CCMatrix4f makeOrientationFromYX(CCVector3f theUnnormalizedX, CCVector3f theUnnormalizedY) {
		// matrix column
		CCVector3f myXCol = new CCVector3f();
		CCVector3f myYCol = new CCVector3f();
		CCVector3f myZCol = new CCVector3f();

		CCVector3f myNormalizedX = theUnnormalizedX.clone().normalize();
		CCVector3f myNormalizedY = theUnnormalizedY.clone().normalize();

		myYCol = myNormalizedY;
		myZCol = myNormalizedX.cross(myYCol);
		myXCol = myYCol.cross(myZCol);

		return buildRotationMatrix(myXCol, myYCol, myZCol);
	}

	protected CCMatrix4f makeOrientationFromYZ(CCVector3f theUnnormalizedY, CCVector3f theUnnormalizedZ) {
		// matrix column
		CCVector3f myXCol = new CCVector3f();
		CCVector3f myYCol = new CCVector3f();
		CCVector3f myZCol = new CCVector3f();

		CCVector3f myNormalizedY = theUnnormalizedY.clone().normalize();
		CCVector3f myNormalizedZ = theUnnormalizedZ.clone().normalize();

		myYCol = myNormalizedY;
		myXCol = myYCol.cross(myNormalizedZ);
		myZCol = myXCol.cross(myYCol);

		return buildRotationMatrix(myXCol, myYCol, myZCol);
	}
	
	/*
	 * MAPPING 
	 * Hips LEFT_HIP <-> RIGHT_HIP 
	 * LeftUpLeg LEFT_HIP 
	 * LeftLeg LEFT_KNEE 
	 * LeftFoot LEFT_FOOT 
	 * RightUpLeg RIGHT_HIP
	 * RightLeg RIGHT_KNEE 
	 * RightFoot RIGHT_FOOT 
	 * Spine TORSO 
	 * LeftShoulder 
	 * LeftArm LEFT_SHOULDER 
	 * LeftForeArm LEFT_ELBOW
	 * LeftHand LEFT_HAND 
	 * RightShoulder 
	 * RightArm RIGHT_SHOULDER 
	 * RightForeArm RIGHT_ELBOW 
	 * RightHand RIGHT_HAND 
	 * Neck NECK
	 * Head HEAD
	 */
	
	public static class CCUpperRotationSettings{
	
	//	@CCControl(name = "head rotation", min = -180, max = 180)
	//	private CCRotation _cHeadRotation = new CCRotation();
	//
	//	@CCControl(name = "Neck rotation", min = -180, max = 180)
	//	private CCRotation _cNeckRotation = new CCRotation();
	
	//	@CCControl(name = "Spine rotation", min = -180, max = 180)
	//	private CCRotation _cSpineRotation = new CCRotation();
		@CCControl(name = "apply")
		private boolean _cApply = true;
	
		@CCControl(name = "Hips rotation", min = -180, max = 180)
		private CCRotation _cHipsRotation = new CCRotation();
		
	
		@CCControl(name = "right hand rotation", min = -180, max = 180)
		private CCRotation _cRightHandRotation = new CCRotation();
	
		@CCControl(name = "right forearm rotation", min = -180, max = 180)
		private CCRotation _cRightForeRotation = new CCRotation();
	
		@CCControl(name = "right arm rotation", min = -180, max = 180)
		private CCRotation _cRightArmRotation = new CCRotation();
	
	//	@CCControl(name = "right shoulder rotation", min = -180, max = 180)
	//	private CCRotation _cRightShoulderRotation = new CCRotation();
		
	
		@CCControl(name = "left hand rotation", min = -180, max = 180)
		private CCRotation _cLeftHandRotation = new CCRotation();
	
		@CCControl(name = "left forearm rotation", min = -180, max = 180)
		private CCRotation _cLeftForeRotation = new CCRotation();
	
		@CCControl(name = "left arm rotation", min = -180, max = 180)
		private CCRotation _cLeftArmRotation = new CCRotation();
	
	//	@CCControl(name = "left shoulder rotation", min = -180, max = 180)
	//	private CCRotation _cLeftShoulderRotation = new CCRotation();
	}
	
	public static class CCLowerRotationSettings{
		@CCControl(name = "right foot rotation", min = -180, max = 180)
		private CCRotation _cRightFootRotation = new CCRotation();
		@CCControl(name = "right leg rotation", min = -180, max = 180)
		private CCRotation _cRightLegRotation = new CCRotation();
		@CCControl(name = "right upleg rotation", min = -180, max = 180)
		private CCRotation _cRightUplegRotation = new CCRotation();
		

		@CCControl(name = "left foot rotation", min = -180, max = 180)
		private CCRotation _cLeftFootRotation = new CCRotation();
		@CCControl(name = "left leg rotation", min = -180, max = 180)
		private CCRotation _cLeftLegRotation = new CCRotation();
		@CCControl(name = "left upleg rotation", min = -180, max = 180)
		private CCRotation _cLeftUplegRotation = new CCRotation();
		
	}
	
	public static CCUpperRotationSettings upperSettings = new CCUpperRotationSettings();
	public static CCLowerRotationSettings lowerSettings = new CCLowerRotationSettings();

	private void applyRotation(CCMatrix4f theMatrix, CCRotation theRotation){
		if(!upperSettings._cApply)return;
		theMatrix.rotateX(CCMath.radians(theRotation._cXRotation));
		theMatrix.rotateY(CCMath.radians(theRotation._cYRotation));
		theMatrix.rotateZ(CCMath.radians(theRotation._cZRotation));
	}

	public CCMatrix4f calculateJointOrientation(String theID) {
		CCMatrix4f myOrientation = new CCMatrix4f();

		CCVector3f myX;
		CCVector3f myY;
		CCVector3f myZ;
		try {
			switch(theID) {
			case "Head":
				myX = direction("Neck", "Head");
				myZ = direction("LeftArm", "RightArm");
				myOrientation = makeOrientationFromXZ(myX, myZ);
//				applyRotation(myOrientation, settings._cHeadRotation);
				myOrientation.scale(1, -1, 1);
				break;
			case "Neck":
				myX = direction("RightArm", "LeftArm");
				myY = direction("Neck", "Head");
				myOrientation = makeOrientationFromYX(myX, myY);
//				applyRotation(myOrientation, settings._cNeckRotation);
				myOrientation.scale(1, 1, 1);
				break;	
			case "Spine":
				myX = direction("LeftShoulder", "RightShoulder");
				myY = direction("Spine", "Neck");
				myOrientation = makeOrientationFromYX(myX, myY);
//				applyRotation(myOrientation, settings._cSpineRotation);
				myOrientation.scale(-1, 1, -1);
				break;
			case "Hips":
				myX = direction("LeftUpLeg", "RightUpLeg");
				myY = direction("Hips", "Spine");
				myOrientation = makeOrientationFromXY(myX, myY);
				applyRotation(myOrientation, upperSettings._cHipsRotation);
				myOrientation.scale(-1, 1, 1);
				break;
				
				
			case "RightHand":
				myX = direction("RightForeArm", "RightHand");
				myZ = direction("RightUpLeg","LeftUpLeg").cross(direction("Hips", "Spine")).normalize();
				myOrientation = makeOrientationFromXZ(myX, myZ);
				applyRotation(myOrientation, upperSettings._cRightHandRotation);
				myOrientation.scale(1, 1, -1);
				break;
			case "RightForeArm":
				myY = direction("RightHand", "RightForeArm");
				myZ = direction("LeftUpLeg", "RightUpLeg").cross(direction("Hips", "Spine")).normalize();
				myOrientation = makeOrientationFromYZ(myY, myZ);
				applyRotation(myOrientation, upperSettings._cRightForeRotation);
				myOrientation.scale( -1, 1, -1);
				break;
			case "RightArm":
				myY = direction("RightForeArm", "RightArm");
				myZ = direction("LeftUpLeg", "RightUpLeg").cross(direction("Hips", "Spine")).normalize();
				myOrientation = makeOrientationFromYZ(myY, myZ);
				applyRotation(myOrientation, upperSettings._cRightArmRotation);
				myOrientation.scale( -1, 1, -1);
				break;
			case "RightShoulder":
				myX = direction("LeftShoulder", "RightShoulder");
				myY = direction("Hips", "Spine");
				myOrientation = makeOrientationFromXY(myX, myY);
//				applyRotation(myOrientation, settings._cRightShoulderRotation);
				myOrientation.scale( -1, 1, 1);
				break;
				

			case "LeftHand":
				myX = direction("LeftForeArm", "LeftHand");
				myZ = direction("LeftUpLeg", "RightUpLeg").cross(direction("Hips", "Spine")).normalize();
				myOrientation = makeOrientationFromXZ(myX, myZ);
				applyRotation(myOrientation, upperSettings._cLeftHandRotation);
				myOrientation.scale(1, 1, -1);
				break;
			case "LeftForeArm":
				myY = direction("LeftHand", "LeftForeArm");
				myZ = direction("LeftUpLeg", "RightUpLeg").cross(direction("Hips", "Spine")).normalize();
				myOrientation = makeOrientationFromYZ(myY, myZ);
				applyRotation(myOrientation, upperSettings._cLeftForeRotation);
				myOrientation.scale( -1, 1, -1);
				break;
			case "LeftArm":
				myY = direction("LeftForeArm", "LeftArm");
				myZ = direction("LeftUpLeg", "RightUpLeg").cross(direction("Hips", "Spine")).normalize();
				myOrientation = makeOrientationFromYZ(myY, myZ);
				applyRotation(myOrientation, upperSettings._cLeftArmRotation);
				myOrientation.scale( -1, 1, -1);
				break;
			case "LeftShoulder":
				myX = direction("LeftShoulder", "RightShoulder");
				myY = direction("Hips", "Spine");
				myOrientation = makeOrientationFromXY(myX, myY);
//				applyRotation(myOrientation, settings._cLeftShoulderRotation);
				myOrientation.scale( -1, 1, 1);
				break;
				

			case "RightFoot":
				myY = direction("RightFoot", "RightLeg");
				myX = direction("LeftUpLeg", "RightUpLeg").cross(direction("Hips", "Spine")).normalize();
				myOrientation = makeOrientationFromXY(myX, myY);
				applyRotation(myOrientation, lowerSettings._cRightFootRotation);
				myOrientation.scale(-1, 1, 1);
				break;
			case "RightLeg":
				myY = direction("RightFoot", "RightLeg");
				myZ = direction("LeftUpLeg", "RightUpLeg").cross(direction("Hips", "Spine")).normalize();
				myOrientation = makeOrientationFromYZ(myY, myZ);
				applyRotation(myOrientation, lowerSettings._cRightLegRotation);
				myOrientation.scale( -1, 1, -1);
				break;
			case "RightUpLeg":
				myX = direction("LeftUpLeg", "RightUpLeg");
				myY = direction("RightLeg", "RightUpLeg");
				myOrientation = makeOrientationFromYX(myX, myY);
				applyRotation(myOrientation, lowerSettings._cRightUplegRotation);
				myOrientation.scale(-1, 1, -1);
				break;

				
			case "LeftFoot":
				myY = direction("LeftFoot", "LeftLeg");
				myX = direction("LeftUpLeg", "RightUpLeg").cross(direction("Hips", "Spine")).normalize();
				myOrientation = makeOrientationFromXY(myX, myY);
				applyRotation(myOrientation, lowerSettings._cLeftFootRotation);
				myOrientation.scale(-1, 1, 1);
				break;
			case "LeftLeg":
				myY = direction("LeftFoot", "LeftLeg");
				myZ = direction("LeftUpLeg", "RightUpLeg").cross(direction("Hips", "Spine")).normalize();
				myOrientation = makeOrientationFromYZ(myY, myZ);
				applyRotation(myOrientation, lowerSettings._cLeftLegRotation);
				myOrientation.scale( -1, 1, -1);
				
				//myOrientation = makeOrientationFromY( myY, false);
				//myOrientation.scale(1, 1, 1);
				break;
			case "LeftUpLeg":
				myX = direction("LeftUpLeg", "RightUpLeg");
				myY = direction("LeftLeg", "LeftUpLeg");
				myOrientation = makeOrientationFromYX(myX, myY);
				applyRotation(myOrientation, lowerSettings._cLeftUplegRotation);
				myOrientation.scale(-1, 1, -1);
				break;
			}
		}catch(NullPointerException ne) {
			
		}
		
		return myOrientation;
	}
	
	public void update(float theDeltaTime){
		for(CCSkeletonJoint myJoint:_mySkeleton.joints()) {
			try {
				CCMatrix4f myWorldMatrix = new CCMatrix4f();
				myWorldMatrix.translate(_myPositionMap.get(myJoint.id()).position());
				myWorldMatrix.apply(calculateJointOrientation(myJoint.id()));
				myJoint.worldMatrix(myWorldMatrix);
			}catch(NullPointerException e) {
				e.printStackTrace();
			}
		}
		_mySkeleton.update(theDeltaTime);
	}
}
