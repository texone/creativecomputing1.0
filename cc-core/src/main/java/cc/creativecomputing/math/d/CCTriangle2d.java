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
package cc.creativecomputing.math.d;

/**
 * @author info
 *
 */
public class CCTriangle2d {

	protected CCVector2d[] _myPoints;
	
	protected CCVector2d _myCenter;
	
	public CCTriangle2d(final CCVector2d thePointA, final CCVector2d thePointB, final CCVector2d thePointC) {
		_myPoints = new CCVector2d[3];
		_myPoints[0] = thePointA;
		_myPoints[1] = thePointB;
		_myPoints[2] = thePointC;
		
		_myCenter = new CCVector2d();
		_myCenter.add(_myPoints[0]);
		_myCenter.add(_myPoints[1]);
		_myCenter.add(_myPoints[2]);
		_myCenter.scale(1f/3);
	}
	
	public CCTriangle2d(double theAX, double theAY, double theBX, double theBY, double theCX, double theCY){
		this(new CCVector2d(theAX, theAY), new CCVector2d(theBX, theBY), new CCVector2d(theCX, theCY));
	}
	
	public CCTriangle2d(){
		this(new CCVector2d(),new CCVector2d(),new CCVector2d());
	}
	
	public CCVector2d a() {
		return _myPoints[0];
	}
	
	public CCVector2d b() {
		return _myPoints[1];
	}
	
	public CCVector2d c() {
		return _myPoints[2];
	}
	
	public CCVector2d center() {
		return _myCenter;
	}
	
	public CCVector2d[] points(){
		return _myPoints;
	}
	
	/**
	 * In geometry, the barycentric coordinate system is a coordinate system in 
	 * which the location of a point is specified as the center of mass, or barycenter, 
	 * of masses placed at the vertices of a simplex (a triangle, tetrahedron, etc).
	 * In the context of a triangle, barycentric coordinates are also known as 
	 * areal coordinates, because the coordinates of P with respect to triangle 
	 * ABC are proportional to the (signed) areas of PBC, PCA and PAB
	 * @param thePoint the point to convert to barycentric coordinates
	 * @return the given point as barycentric coordinates
	 */
	public CCVector2d toBarycentricCoordinates(final CCVector2d thePoint){
		// Compute vectors        
		CCVector2d v0 = CCVecMathD.subtract(c(), a());
		CCVector2d v1 = CCVecMathD.subtract(b(), a());
		CCVector2d v2 = CCVecMathD.subtract(thePoint, a());

		// Compute dot products
		double dot00 = v0.dot(v0);
		double dot01 = v0.dot(v1);
		double dot02 = v0.dot(v2);
		double dot11 = v1.dot(v1);
		double dot12 = v1.dot(v2);

		// Compute barycentric coordinates
		double invDenom = 1 / (dot00 * dot11 - dot01 * dot01);
		double u = (dot11 * dot02 - dot01 * dot12) * invDenom;
		double v = (dot00 * dot12 - dot01 * dot02) * invDenom;
		
		return new CCVector2d(u,v);
	}
	
	public CCVector2d toTriangleCoordinates(final CCVector2d thePoint){
		// Compute vectors        
		CCVector2d v0 = CCVecMathD.subtract(c(), a());
		CCVector2d v1 = CCVecMathD.subtract(b(), a());
		
		CCVector2d myResult = a().clone();
		myResult.add(v0.scale(thePoint.x));
		myResult.add(v1.scale(thePoint.y));
		return myResult;
	}

	/**
	 * Returns true if the given point lies inside the triangle
	 * @param thePoint point to test
	 * @param theIsBaryCentric if true the given point is not converted to barycentric coordinates for testing
	 * @return true if the point is inside otherwise false
	 */
	public boolean isInside(CCVector2d thePoint, boolean theIsBaryCentric) {
		if(!theIsBaryCentric)thePoint = toBarycentricCoordinates(thePoint);

		// Check if point is in triangle
		return (thePoint.x > 0) && (thePoint.y > 0) && (thePoint.x + thePoint.y < 1);
	}
	
	/**
	 * Returns true if the given point lies inside the triangle
	 * @param thePoint
	 * @return true if the point is inside otherwise false
	 */
	public boolean isInside(CCVector2d thePoint){
		return isInside(thePoint, false);
	}
	
	/**
	 * Returns true if the given point lies inside the triangle
	 * @param theX x coord of the point to check
	 * @param theY y coord of the point to check
	 * @return
	 */
	public boolean isInside(final double theX, final double theY) {
		return isInside(new CCVector2d(theX, theY));
	}
	
	@Override
	public String toString(){
		return "CCTriangle2d["+a()+","+b()+","+c()+","+"]";
	}
}
