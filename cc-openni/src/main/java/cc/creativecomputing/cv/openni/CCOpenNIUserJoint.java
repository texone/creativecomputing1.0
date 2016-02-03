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

import cc.creativecomputing.math.CCVector3f;

/**
 * Each UserJoint object manages a joint in the 3D scene. which involves two main tasks: 
 * <li>updating the joint by moving it to the position supplied by the SkeletonCapability object; 
 * <li>making the joint invisible or visible. 
 * @author christianriekoff
 *
 */
public class CCOpenNIUserJoint{
	
	private CCVector3f _myPosition;
	
	private float _myPositionConfidence;
	
	private CCOpenNIUserJointType _myType;
	
	private boolean _myIsVisible;
	
	public CCOpenNIUserJoint(CCOpenNIUserJointType theType) {
		
		_myType = theType;
		
		_myPosition = new CCVector3f();
	}
	
	void updatePosition(float theX, float theY, float theZ, float theConfidence) {
		_myPositionConfidence = theConfidence;
		
		_myIsVisible = _myPositionConfidence <= 0;
		
		_myPosition.set(theX,theY,theZ);
	}
	
	public boolean isVisible() {
		return _myIsVisible;
	}
	
	public CCVector3f position() {
		return _myPosition;
	}
	
	public float positionConfidence() {
		return _myPositionConfidence;
	}
	
	public CCOpenNIUserJointType type() {
		return _myType;
	}
	
	@Override
	public String toString() {
		return "CCUserJoint{type:"+_myType+" position:"+_myPosition+"}";
	}
}
