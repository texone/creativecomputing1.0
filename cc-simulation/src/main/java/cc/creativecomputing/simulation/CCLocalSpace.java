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
package cc.creativecomputing.simulation;


import cc.creativecomputing.math.CCVector3f;

/**
 * Class representing a localspace
 * @author christianr
 *
 */
public class CCLocalSpace{

	/**
	 * position of the local space
	 */
	public CCVector3f position;

	/**
	 * Forward direction of the local space
	 */
	public CCVector3f forward;

	/**
	 * Side direction of the local space
	 */
	public CCVector3f side;

	/**
	 * up direction of the local space
	 */
	public CCVector3f up;

	/**
	 * used for better performance in calculations
	 */
	static CCVector3f component = new CCVector3f();

	public CCLocalSpace(){
		setToIdentity();
	}

	public CCLocalSpace(final CCVector3f initialPosition){
		setToIdentity();
		position = initialPosition.clone();
	}

	/**
	 * Initializes a new local space where all values are set to zero
	 *
	 */
	public void setToIdentity(){
		position = new CCVector3f(0.0F, 0.0F, 0.0F);
		forward = new CCVector3f(0.0F, 0.0F, 1.0F);
		side = new CCVector3f(1.0F, 0.0F, 0.0F);
		up = new CCVector3f(0.0F, 1.0F, 0.0F);
	}

	
	public CCVector3f globalizePosition(final CCVector3f theLocalPosition){
		final CCVector3f myResult = globalizeDirection(theLocalPosition);
		myResult.add(position);
		return myResult;
	}

	public CCVector3f globalizeDirection(final CCVector3f theLocalVector){
		float x = side.x * theLocalVector.x;
		float y = side.y * theLocalVector.x;
		float z = side.z * theLocalVector.x;
		
		x += up.x * theLocalVector.y;
		y += up.y * theLocalVector.y;
		z += up.z * theLocalVector.y;
		
		x += forward.x * theLocalVector.z;
		y += forward.y * theLocalVector.z;
		z += forward.z * theLocalVector.z;
		
		return new CCVector3f(x,y,z);
	}

	public CCVector3f localizePosition(final CCVector3f theGlobalVector){
		final CCVector3f myResult = new CCVector3f();
			component.set(theGlobalVector);
			component.subtract(position);
			myResult.set(component.dot(side), component.dot(up), component.dot(forward));
		return myResult;
	}

}
