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

import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.skeleton.CCSkeletonJointType;

public abstract class CCOpenNISkeletonJoint{
	
	private CCSkeletonJointType _myType;
	
	CCOpenNISkeletonJoint(CCSkeletonJointType theType) {
		_myType = theType;
	}
	
	public abstract CCVector3f position();
	
	public CCSkeletonJointType type() {
		return _myType;
	}
}
