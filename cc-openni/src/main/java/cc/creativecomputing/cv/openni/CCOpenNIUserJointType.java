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

import org.openni.SkeletonJoint;

public enum CCOpenNIUserJointType{
		HEAD (SkeletonJoint.HEAD),
		NECK (SkeletonJoint.NECK),
		TORSO (SkeletonJoint.TORSO),
//		WAIST (SkeletonJoint.WAIST),
//		LEFT_COLLAR (SkeletonJoint.LEFT_COLLAR),
		LEFT_SHOULDER (SkeletonJoint.LEFT_SHOULDER),
		LEFT_ELBOW (SkeletonJoint.LEFT_ELBOW),
//		LEFT_WRIST (SkeletonJoint.LEFT_WRIST),
		LEFT_HAND (SkeletonJoint.LEFT_HAND),
//		LEFT_FINGERTIP (SkeletonJoint.LEFT_FINGER_TIP),
//		RIGHT_COLLAR (SkeletonJoint.RIGHT_COLLAR),
		RIGHT_SHOULDER (SkeletonJoint.RIGHT_SHOULDER),
		RIGHT_ELBOW (SkeletonJoint.RIGHT_ELBOW),
//		RIGHT_WRIST (SkeletonJoint.RIGHT_WRIST),
		RIGHT_HAND (SkeletonJoint.RIGHT_HAND),
//		RIGHT_FINGERTIP (SkeletonJoint.RIGHT_FINGER_TIP),
		LEFT_HIP (SkeletonJoint.LEFT_HIP),
		LEFT_KNEE (SkeletonJoint.LEFT_KNEE),
//		LEFT_ANKLE (SkeletonJoint.LEFT_ANKLE),
		LEFT_FOOT (SkeletonJoint.LEFT_FOOT),
		RIGHT_HIP (SkeletonJoint.RIGHT_HIP),
		RIGHT_KNEE (SkeletonJoint.RIGHT_KNEE),
//		RIGHT_ANKLE (SkeletonJoint.RIGHT_ANKLE),
		RIGHT_FOOT (SkeletonJoint.RIGHT_FOOT);
			
		SkeletonJoint _myJoint;
		
		CCOpenNIUserJointType(SkeletonJoint theJoint){
			_myJoint = theJoint;
		}
	}
	/*
	Not supported:WAIST
	Not supported:LEFT_COLLAR
	Not supported:LEFT_WRIST
	Not supported:LEFT_FINGERTIP
	Not supported:RIGHT_COLLAR
	Not supported:RIGHT_WRIST
	Not supported:RIGHT_FINGERTIP
	Not supported:LEFT_ANKLE
	Not supported:RIGHT_ANKLE
	*/
