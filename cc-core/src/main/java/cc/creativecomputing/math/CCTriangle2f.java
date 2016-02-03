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

/**
 * @author info
 *
 */
public class CCTriangle2f {

	protected CCVector2f _myPointA;
	protected CCVector2f _myPointB;
	protected CCVector2f _myPointC;
	
	protected CCVector2f _myCenter;
	
	public CCTriangle2f(final CCVector2f thePointA, final CCVector2f thePointB, final CCVector2f thePointC) {
		_myPointA = thePointA;
		_myPointB = thePointB;
		_myPointC = thePointC;
		
		_myCenter = new CCVector2f();
		_myCenter.add(_myPointA);
		_myCenter.add(_myPointB);
		_myCenter.add(_myPointC);
		_myCenter.scale(1f/3);
	}
	
	public CCVector2f a() {
		return _myPointA;
	}
	
	public CCVector2f b() {
		return _myPointB;
	}
	
	public CCVector2f c() {
		return _myPointC;
	}
	

	/**
	 * Returns true if the given point lies inside the triangle
	 * @param thePoint
	 * @return
	 */
	public boolean isInside(final CCVector2f thePoint) {
		// Compute vectors        
		CCVector2f v0 = CCVecMath.subtract(_myPointC, _myPointA);
		CCVector2f v1 = CCVecMath.subtract(_myPointB, _myPointA);
		CCVector2f v2 = CCVecMath.subtract(thePoint, _myPointA);

		// Compute dot products
		float dot00 = v0.dot(v0);
		float dot01 = v0.dot(v1);
		float dot02 = v0.dot(v2);
		float dot11 = v1.dot(v1);
		float dot12 = v1.dot(v2);

		// Compute barycentric coordinates
		float invDenom = 1 / (dot00 * dot11 - dot01 * dot01);
		float u = (dot11 * dot02 - dot01 * dot12) * invDenom;
		float v = (dot00 * dot12 - dot01 * dot02) * invDenom;

		// Check if point is in triangle
		return (u > 0) && (v > 0) && (u + v < 1);
	}
}
