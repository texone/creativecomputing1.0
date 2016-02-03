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

import cc.creativecomputing.cv.openni.CCOpenNIUserJoint;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.skeleton.CCSkeletonJointType;

public class CCOpenNIDirectSkeletonJoint extends CCOpenNISkeletonJoint{
	
	private CCOpenNIUserJoint _myJoint;
	
	CCOpenNIDirectSkeletonJoint(CCOpenNIUserJoint theJoint, CCSkeletonJointType theType) {
		super(theType);
		_myJoint = theJoint;
	}
	
	@Override
	public CCVector3f position() {
		return _myJoint.position();
	}
	
}
