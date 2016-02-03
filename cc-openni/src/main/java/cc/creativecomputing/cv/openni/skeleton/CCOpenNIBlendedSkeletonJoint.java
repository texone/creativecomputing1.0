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

import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.skeleton.CCSkeletonJointType;

public class CCOpenNIBlendedSkeletonJoint extends CCOpenNISkeletonJoint{
	private CCOpenNISkeletonJoint _myJoint1;
	private CCOpenNISkeletonJoint _myJoint2;
	private float _myBlend;
	
	CCOpenNIBlendedSkeletonJoint(CCOpenNISkeletonJoint theJoint1, CCOpenNISkeletonJoint theJoint2, float theBlend, CCSkeletonJointType theType) {
		super(theType);
		_myJoint1 = theJoint1;
		_myJoint2 = theJoint2;
		_myBlend = theBlend;
	}
	
	@Override
	public CCVector3f position() {
		return CCVecMath.blend(_myBlend, _myJoint1.position(), _myJoint2.position());
	}
	
	
}
