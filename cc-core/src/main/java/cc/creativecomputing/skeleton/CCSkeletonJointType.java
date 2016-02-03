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

public enum CCSkeletonJointType {
	HEAD("Head"), 
	NECK("Neck"), 
	SPINE("Spine"), 
	HIPS("Hips"),

	RIGHT_HAND("RightHand"), 
	RIGHT_ELBOW("RightForeArm"), 
	RIGHT_ARM("RightArm"),
	RIGHT_SHOULDER("RightShoulder"),

	LEFT_HAND("LeftHand"), 
	LEFT_ELBOW("LeftForeArm"), 
	LEFT_ARM("LeftArm"), 
	LEFT_SHOULDER("LeftShoulder"),

	RIGHT_HIP("RightUpLeg"), 
	RIGHT_KNEE("RightLeg"), 
	RIGHT_FOOT("RightFoot"), 

	LEFT_HIP("LeftUpLeg"), 
	LEFT_KNEE("LeftLeg"), 
	LEFT_FOOT("LeftFoot");

	private String _myColladaID;

	private CCSkeletonJointType(String theColladaID) {
		_myColladaID = theColladaID;
	}
	
	public String colladaID(){
		return _myColladaID;
	}
	
	public static String[] toStrings(CCSkeletonJointType[] theJointTypes){
		String[] myStrings = new String[theJointTypes.length];
		for(int i = 0; i < theJointTypes.length;i++){
			myStrings[i] = theJointTypes[i].colladaID();
		}
		return myStrings;
	}
}
