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
package cc.creativecomputing.math;

public class CCLine3f {

	protected CCVector3f _myStart;
	protected CCVector3f _myEnd;
	

	public CCLine3f(final CCVector3f theStart, final CCVector3f theEnd) {
		_myStart = theStart;
		_myEnd = theEnd;
	}

	public CCLine3f(
		final float theStartX, final float theStartY, final float theStartZ,
		final float theEndX, final float theEndY, final float theEndZ
	) {
		this(new CCVector3f(theStartX, theStartY, theStartZ), new CCVector3f(theEndX, theEndY, theEndZ));
	}

	public CCLine3f(final CCLine3f theSegment) {
		this(theSegment._myStart, theSegment._myEnd);
	}

	public CCLine3f() {
		this(0, 0, 0, 0, 0, 0);
	}

	/**
	 * @return the start
	 */
	public CCVector3f start() {
		return _myStart;
	}

	/**
	 * @return the end
	 */
	public CCVector3f end() {
		return _myEnd;
	}

	public float length() {
		return _myStart.distance(_myEnd);
	}
	
	@Override
	public boolean equals(final Object theSegment) {
		if(!(theSegment instanceof CCLine3f))return false;
		
		CCLine3f mySegment = (CCLine3f)theSegment;
		return 
			mySegment.start().equals(start()) && mySegment.end().equals(end()) ||
			mySegment.start().equals(end()) && mySegment.end().equals(start());
	}
	
	public float closestPointBlend(CCVector3f thePoint){
		return closestPointBlend(thePoint.x, thePoint.y, thePoint.z);
	}
	
	public float closestPointBlend(final float theX, final float theY, final float theZ){
		return CCMath.saturate(( 
	    	(theX - _myStart.x) * ( _myEnd.x - _myStart.x) +
	        (theY - _myStart.y) * ( _myEnd.y - _myStart.y)  +
	        (theZ - _myStart.z) * ( _myEnd.z - _myStart.z)
	    ) / _myStart.distanceSquared(_myEnd));
	}
	
	/**
	 * Returns the point on the line that is closest to the given point
	 * @param theX x coord of the point
	 * @param theY y coord of the point
	 * @param theZ z coord of the point
	 * @return the closest point
	 */
	public CCVector3f closestPoint(final float theX, final float theY, final float theZ){ 
	 
	    float myBlend = closestPointBlend(theX, theY, theZ);
	    
	    return CCVecMath.blend(myBlend, _myStart, _myEnd);
	 
//	    if( _myU < 0.0f) {
//	    	return _myStart.clone();
//	    }
//	    
//	    if(_myU > 1.0f ) {
//	    	return _myEnd.clone();
//	    }
//	 
//	    return new CCVector3f(
//	    	_myStart.x + _myU * (_myEnd.x - _myStart.x),
//	    	_myStart.y + _myU * (_myEnd.y - _myStart.y),
//	    	_myStart.z + _myU * (_myEnd.z - _myStart.z)
//	    );
	}
	
	/**
	 * Returns the point on the line that is closest to the given point
	 * @param thePoint the point to use for searching
	 * @return the closest point on the line to the given point
	 */
	public CCVector3f closestPoint(CCVector3f thePoint){
		return closestPoint(thePoint.x, thePoint.y, thePoint.z);
	}
	
	/**
	 * Returns the distance of a point to a segment defined by the two end points
	 * @param theX
	 * @param theY
	 * @param theZ
	 * @return
	 */
	public float distance(float theX, float theY, float theZ){
		CCVector3f myClosestPoint = closestPoint(theX, theY, theZ);
		return myClosestPoint.distance(theX, theY, theZ);
	}
	
	/**
	 * Returns the distance of a point to a segment defined by the two end points
	 * @param thePoint
	 * @param theEndPoint1
	 * @param theEndPoint2
	 * @return
	 */
	public float distance(final CCVector3f theVector){ 
	    return distance(theVector.x, theVector.y, theVector.z);
	}

	

	public CCLine3f closestLineBetween(final CCLine3f theOtherLine) {

		CCVector3f p13 = CCVecMath.subtract(_myStart,theOtherLine._myStart);
		CCVector3f p43 = CCVecMath.subtract(theOtherLine._myEnd, theOtherLine._myStart);
		
		if (
			Math.abs(p43.x) <= Float.MIN_NORMAL && 
			Math.abs(p43.y) <= Float.MIN_NORMAL && 
			Math.abs(p43.z) <= Float.MIN_NORMAL
		) {
			return null;
		}

		CCVector3f p21 = CCVecMath.subtract(_myEnd, _myStart);
		
		if (
			Math.abs(p21.x) <= Float.MIN_NORMAL && 
			Math.abs(p21.y) <= Float.MIN_NORMAL && 
			Math.abs(p21.z) <= Float.MIN_NORMAL
		) {
			return null;
		}
		
		float d4321 = p43.dot(p21);
		float d4343 = p43.dot(p43);
		float d2121 = p21.dot(p21);

		float denom = d2121 * d4343 - d4321 * d4321;
		if (Math.abs(denom) < Float.MIN_NORMAL) {
			return(null);
		}
		
		float d1343 = p13.dot(p43);
		float d1321 = p13.dot(p21);
		
		float numer = d1343 * d4321 - d1321 * d4343;

		float mua = numer / denom;
		float mub = (d1343 + d4321 * (mua)) / d4343;

		return new CCLine3f(
			_myStart.x + mua * p21.x,
			_myStart.y + mua * p21.y,
			_myStart.z + mua * p21.z,
			theOtherLine._myStart.x + mub * p43.x,
			theOtherLine._myStart.y + mub * p43.y,
			theOtherLine._myStart.z + mub * p43.z
		);
	}

	/**
 	 * Returns a string representation of the vector
	 */
	public String toString(){
		return "CCLine3f _myStart:[ "+_myStart+" ] end:[ " + _myEnd + " ]";
	}
}
