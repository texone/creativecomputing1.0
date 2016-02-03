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

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.d.CCVector3d;



public class CCVecMathD {
	
	/**
	 * Constructs a new vector consisting of the largest components of both vectors.
	 * 
	 * @param theVector2 the b
	 * @param theVector1 the a
	 * 
	 * @return result as new vector
	 */
	public static CCVector3d max(final CCVector3d theVector1, final CCVector3d theVector2) {
		return new CCVector3d(
			CCMath.max(theVector1.x, theVector2.x), 
			CCMath.max(theVector1.y, theVector2.y), 
			CCMath.max(theVector1.z, theVector2.z)
		);
	}

	/**
	 * Constructs a new vector consisting of the smallest components of both vectors.
	 * 
	 * @param theVector2 comparing vector
	 * @param theVector1 the a
	 * 
	 * @return result as new vector
	 */
	public static CCVector3d min(final CCVector3d theVector1, final CCVector3d theVector2) {
		return new CCVector3d(
			CCMath.min(theVector1.x, theVector2.x), 
			CCMath.min(theVector1.y, theVector2.y), 
			CCMath.min(theVector1.z, theVector2.z)
		);
	}
	
	/**
	 * Constructs a new vector consisting of the largest components of both vectors.
	 * 
	 * @param theVector2 the b
	 * @param theVector1 the a
	 * 
	 * @return result as new vector
	 */
	public static CCVector2d max(final CCVector2d theVector1, final CCVector2d theVector2) {
		return new CCVector2d(
			CCMath.max(theVector1.x, theVector2.x), 
			CCMath.max(theVector1.y, theVector2.y)
		);
	}

	/**
	 * Constructs a new vector consisting of the smallest components of both vectors.
	 * 
	 * @param theVector2 comparing vector
	 * @param theVector1 the a
	 * 
	 * @return result as new vector
	 */
	public static CCVector2d min(final CCVector2d theVector1, final CCVector2d theVector2) {
		return new CCVector2d(
			CCMath.min(theVector1.x, theVector2.x), 
			CCMath.min(theVector1.y, theVector2.y)
		);
	}
	
	//Rotate the point (x,y,z) around the vector (u,v,w)
	
	public static CCVector3d rotateAroundVector(final CCVector3d theVector, final CCVector3d theAxis, final double theAngle){
		final double x = theVector.x;
		final double y = theVector.y;
		final double z = theVector.z;
		
		final double u = theAxis.x;
		final double v = theAxis.y;
		final double w = theAxis.z;
		
		final double ux = u * x;
		final double uy = u * y;
		final double uz = u * z;
		
		final double vx = v * x;
		final double vy = v * y;
		final double vz = v * z;
		
		final double wx = w * x;
		final double wy = w * y;
		final double wz = w * z;
		
		final double sa = CCMath.sin(theAngle);
		final double ca = CCMath.cos(theAngle);
		
		return new CCVector3d(
			u * (ux + vy + wz) + (x * (v * v + w * w) - u *(vy + wz)) * ca + (-wy + vz) * sa,
			v * (ux + vy + wz) + (y * (u * u + w * w) - v *(ux + wz)) * ca + ( wx - uz) * sa,
			w * (ux + vy + wz) + (z * (u * u + v * v) - w *(ux + vy)) * ca + (-vx + uy) * sa
		);
	}
	
	/**
	 * Returns the result of v1 + v2
	 * @param theVector1
	 * @param theVector2
	 * @return
	 */
	public static CCVector3d add(final CCVector3d theVector1, final CCVector3d theVector2){
		return new CCVector3d(
			theVector1.x + theVector2.x,
			theVector1.y + theVector2.y,
			theVector1.z + theVector2.z
			
		);
	}
	
	public static CCVector3d add(final CCVector3d ...theVector3fs){
		CCVector3d myResult = new CCVector3d();
		for(CCVector3d myVector:theVector3fs){
			myResult.add(myVector);
		}
		return myResult;
	}
	
	/**
	 * Returns the result of v1 + v2
	 * @param theVector1
	 * @param theVector2
	 * @return
	 */
	public static CCVector2d add(final CCVector2d theVector1, final CCVector2d theVector2){
		return new CCVector2d(
			theVector1.x + theVector2.x,
			theVector1.y + theVector2.y
			
		);
	}
	
	public static CCVector2d add(final CCVector2d ...theVector2fs){
		CCVector2d myResult = new CCVector2d();
		for(CCVector2d myVector:theVector2fs){
			myResult.add(myVector);
		}
		return myResult;
	}
	
	/**
	 * Returns the result of v1 - v2
	 * @param theVector1
	 * @param theVector2
	 * @return
	 */
	public static CCVector3d subtract(final CCVector3d theVector1, final CCVector3d theVector2){
		return new CCVector3d(
			theVector1.x - theVector2.x,
			theVector1.y - theVector2.y,
			theVector1.z - theVector2.z
			
		);
	}
	
	/**
	 * Returns the result of v1 - v2
	 * @param theVector1
	 * @param theVector2
	 * @return
	 */
	public static CCVector2d subtract(final CCVector2d theVector1, final CCVector2d theVector2){
		return new CCVector2d(
			theVector1.x - theVector2.x,
			theVector1.y - theVector2.y
			
		);
	}

	public static CCVector2d multiply(CCVector2d v1, double scalar){
		return new CCVector2d(v1.x*scalar, v1.y*scalar);
	}
	
	public static CCVector3d multiply(CCVector3d v1, double scalar){
		return new CCVector3d(v1.x*scalar, v1.y*scalar, v1.z*scalar);
	}
	
	public static CCVector3d divide(CCVector3d v1, double scalar){
		return new CCVector3d(v1.x/scalar, v1.y/scalar, v1.z/scalar);
	}
	
	public static CCVector2d divide(CCVector2d v1, double scalar){
		return new CCVector2d(v1.x/scalar, v1.y/scalar);
	}
	
	/**
	 * Returns the result of the cross product of v1 and v2
	 * @param theVector1
	 * @param theVector2
	 * @return
	 */
	public static CCVector3d cross(final CCVector3d theVector1, final CCVector3d theVector2){
		return new CCVector3d(
			theVector1.y * theVector2.z - theVector1.z * theVector2.y, 
			theVector1.z * theVector2.x - theVector1.x * theVector2.z, 
			theVector1.x * theVector2.y - theVector1.y * theVector2.x
		);
	}
	
	public static double dot(final CCVector3d theVector1, final CCVector3d theVector2){
		return theVector1.dot(theVector2);
	}
	
	/**
	 * Calculates the normal to the plane defined by the three given vectors
	 * @param theV1 vector1 of the plane
	 * @param theV2 vector2 of the plane
	 * @param theV3 vector3 of the plane
	 * @return normal of the plane
	 */
	public static CCVector3d normal(final CCVector3d theV1, final CCVector3d theV2, final CCVector3d theV3) {
		CCVector3d v21 = subtract(theV2, theV1);
		CCVector3d v31 = subtract(theV3, theV1);
		
		CCVector3d normal = v21.cross(v31);
		return normal.normalize();
	}
	
	/**
	 * Evaluates quadratic bezier at point t for points a, b, c, d.
	 * t varies between 0 and 1, and a and d are the on curve points,
	 * b and c are the control points. this can be done once with the
	 * x coordinates and a second time with the y coordinates to get
	 * the location of a bezier curve at t.
	 * <P>
	 * For instance, to convert the following example:<PRE>
	 * stroke(255, 102, 0);
	 * line(85, 20, 10, 10);
	 * line(90, 90, 15, 80);
	 * stroke(0, 0, 0);
	 * bezier(85, 20, 10, 10, 90, 90, 15, 80);
	 *
	 * // draw it in gray, using 10 steps instead of the default 20
	 * // this is a slower way to do it, but useful if you need
	 * // to do things with the coordinates at each step
	 * stroke(128);
	 * beginShape(LINE_STRIP);
	 * for (int i = 0; i <= 10; i++) {
	 *   double t = i / 10.0f;
	 *   double x = bezierPoint(85, 10, 90, 15, t);
	 *   double y = bezierPoint(20, 10, 90, 80, t);
	 *   vertex(x, y);
	 * }
	 * endShape();</PRE>
	 */
	static public CCVector3d bezierPoint(
		final CCVector3d theStartPoint, final CCVector3d theStartAnchor, 
		final CCVector3d theEndAnchor, final CCVector3d theEndPoint, 
		final double t
	) {
		double t1 = 1.0f - t;
		return new CCVector3d(
			theStartPoint.x * t1 * t1 * t1 + 3 * theStartAnchor.x * t * t1 * t1 + 3 * theEndAnchor.x * t * t * t1 + theEndPoint.x * t * t * t,
			theStartPoint.y * t1 * t1 * t1 + 3 * theStartAnchor.y * t * t1 * t1 + 3 * theEndAnchor.y * t * t * t1 + theEndPoint.y * t * t * t,
			theStartPoint.z * t1 * t1 * t1 + 3 * theStartAnchor.z * t * t1 * t1 + 3 * theEndAnchor.z * t * t * t1 + theEndPoint.z * t * t * t
		);
	}
	static public CCVector2d bezierPoint(CCVector2d a, CCVector2d b, CCVector2d c, CCVector2d d, double t) {
		double t1 = 1.0f - t;
		return new CCVector2d(
			a.x * t1 * t1 * t1 + 3 * b.x * t * t1 * t1 + 3 * c.x * t * t * t1 + d.x * t * t * t,
			a.y * t1 * t1 * t1 + 3 * b.y * t * t1 * t1 + 3 * c.y * t * t * t1 + d.y * t * t * t
		);
	}
	
	
	
	static public CCVector3d round(final CCVector3d theVector){
		return new CCVector3d(
			CCMath.round(theVector.x),
			CCMath.round(theVector.y),
			CCMath.round(theVector.z)
		);
	}
	
	static public CCVector2d round(final CCVector2d theVector){
		return new CCVector2d(
			CCMath.round(theVector.x),
			CCMath.round(theVector.y)
		);
	}
	
	static public CCVector3d round(final CCVector3d theVector, int thePlaces){
		return new CCVector3d(
			CCMath.round(theVector.x,thePlaces),
			CCMath.round(theVector.y,thePlaces),
			CCMath.round(theVector.z,thePlaces)
		);
	}
	
	static public CCVector2d round(final CCVector2d theVector, int thePlaces){
		return new CCVector2d(
			CCMath.round(theVector.x,thePlaces),
			CCMath.round(theVector.y,thePlaces)
		);
	}
	
	/**
	 * Use this function to see if a vector is inside a bounding volume.
	 * The first parameter is the vector you want to test. The bounding volume
	 * is described by the next two vectors. If the vector are 2D the volume is a
	 * rectangle, if they are 3D the volume is a box. This function takes care to find
	 * the minimum and maximum x,y and z values and checks if the given test vector is 
	 * inside this range.
	 * @shortdesc Use this function to see if a vector is inside a bounding value.
	 * @param theTest the vector to test
	 * @param theVector1 the vector 1 
	 * @param theVector2 the vector 2
	 * @see #isInsideBox(CCVector2d, CCVector2d, double)
	 * @see #isInsideCircle(CCVector2d, CCVector2d, double)
	 * @return true if the test vector is inside the bounding volume otherwise false
	 */
	static public boolean isInside(final CCVector2d theTest, final CCVector2d theVector1, final CCVector2d theVector2){
		double minX, maxX;
		
		if(theVector1.x > theVector2.x){
			minX = theVector2.x;
			maxX = theVector1.x;
		}else{
			minX = theVector1.x;
			maxX = theVector2.x;
		}
		
		double minY, maxY;
		
		if(theVector1.y > theVector2.y){
			minY = theVector2.y;
			maxY = theVector1.y;
		}else{
			minY = theVector1.y;
			maxY = theVector2.y;
		}
		
		return 
			theTest.x > minX && theTest.x < maxX && 
			theTest.y > minY && theTest.y < maxY;
	}
	
	/**
	 * @param theTest
	 * @param theVector1
	 * @param theVector2
	 * @return
	 */
	static public boolean isInside(final CCVector3d theTest, final CCVector3d theVector1, final CCVector3d theVector2){
		double minX, maxX;
		
		if(theVector1.x > theVector2.x){
			minX = theVector2.x;
			maxX = theVector1.x;
		}else{
			minX = theVector1.x;
			maxX = theVector2.x;
		}
		
		double minY, maxY;
		
		if(theVector1.y > theVector2.y){
			minY = theVector2.y;
			maxY = theVector1.y;
		}else{
			minY = theVector1.y;
			maxY = theVector2.y;
		}
		
		double minZ, maxZ;
		
		if(theVector1.z > theVector2.z){
			minZ = theVector2.z;
			maxZ = theVector1.z;
		}else{
			minZ = theVector1.z;
			maxZ = theVector2.z;
		}
		
		return 
			theTest.x > minX && theTest.x < maxX && 
			theTest.y > minY && theTest.y < maxY && 
			theTest.z > minZ && theTest.z < maxZ;
	}
	
	/**
	 * Use this function to see if a vector 1 is inside a given volume
	 * around a vector 2. 
	 * @param theTest1
	 * @param theTest2
	 * @param theRange
	 * @return
	 */
	static public boolean isInsideBox(final CCVector2d theTest1, final CCVector2d theTest2, final double theRange){
		return 
			theTest1.x > theTest2.x - theRange &&
			theTest1.x < theTest2.x + theRange &&
			theTest1.y > theTest2.y - theRange &&
			theTest1.y < theTest2.y + theRange;
	}
	
	static public boolean isInsideBox(final CCVector3d theTest1, final CCVector3d theTest2, final double theRange){
		return 
			theTest1.x > theTest2.x - theRange &&
			theTest1.x < theTest2.x + theRange &&
			theTest1.y > theTest2.y - theRange &&
			theTest1.y < theTest2.y + theRange &&
			theTest1.z > theTest2.z - theRange &&
			theTest1.z < theTest2.z + theRange;
	}
	
	/**
	 * 
	 * @param theTest1
	 * @param theTest2
	 * @param theRange
	 * @see #isInside(CCVector2d, CCVector2d, CCVector2d)
	 * @see #isInsideBox(CCVector2d, CCVector2d, double)
	 * @return true if the vector 1 is inside the given range of vector2
	 */
	static public boolean isInsideCircle(final CCVector2d theTest1, final CCVector2d theTest2, final double theRange){
		return theTest1.distance(theTest2) < theRange;
	}
	
	static public boolean isInsideCircle(final CCVector3d theTest1, final CCVector3d theTest2, final double theRange){
		return theTest1.distance(theTest2) < theRange;
	}
	
	/**
	 * Use this function to blend between two vectors. The blend value
	 * determines the result of the interpolation. Smaller blend values
	 * result in vectors nearer to vector1, bigger values in vectors nearer
	 * to vector2.
	 * @shortdesc Use this function to blend between two vectors.
	 * @param theBlend blend between two vectors
	 * @param theVector1 first vector to for blend
	 * @param theVector2 second vector for blend
	 * @return The result of the interpolation between vector1 and vector2.
	 */
	static public CCVector2d blend(final double theBlend, final CCVector2d theVector1, final CCVector2d theVector2){
		return new CCVector2d(
			theVector1.x * (1 - theBlend) + theVector2.x * theBlend,
			theVector1.y * (1 - theBlend) + theVector2.y * theBlend
		);
	}
	
	static public CCVector3d blend(final double theBlend, final CCVector3d theVector1, final CCVector3d theVector2){
		return new CCVector3d(
			theVector1.x * (1 - theBlend) + theVector2.x * theBlend,
			theVector1.y * (1 - theBlend) + theVector2.y * theBlend,
			theVector1.z * (1 - theBlend) + theVector2.z * theBlend
		);
	}
	
	//////////////////////////////////////////////////////////////
	//
	// CREATE RANDOM VECTORS
	//
	//////////////////////////////////////////////////////////////
	
	/**
	 * Use this method to create random vectors. 
	 * @param theX
	 * @param theY
	 * @return
	 */
	static public CCVector2d random(final double theX, final double theY){
		return new CCVector2d(CCMath.random(theX),CCMath.random(theY));
	}
	
	static public CCVector2d random(
		final double theMinX, final double theMaxX,
		final double theMinY, final double theMaxY
	){
		return new CCVector2d(
			CCMath.random(theMinX, theMaxX),
			CCMath.random(theMinY, theMaxY)
		);
	}
	
	static public CCVector3d random(final double theX, final double theY, final double theZ){
		return new CCVector3d(CCMath.random(theX),CCMath.random(theY),CCMath.random(theZ));
	}
	
	static public CCVector3d random(
		final double theMinX, final double theMaxX,
		final double theMinY, final double theMaxY,
		final double theMinZ, final double theMaxZ
	){
		return new CCVector3d(
			CCMath.random(theMinX, theMaxX),
			CCMath.random(theMinY, theMaxY),
			CCMath.random(theMinZ, theMaxZ)
		);
	}
	
	public static CCVector3d random3d() {
		return new CCVector3d().randomize().normalize();
	}
	
	public static CCVector3d random3d(final double theScale) {
		return random3d().scale(theScale);
	}
	
	public static CCVector2d random2d() {
		return new CCVector2d().randomize().normalize();
	}
	
	public static CCVector2d random2d(final double theScale) {
		return random2d().scale(theScale);
	}

	/**
	 * Returns the angle between the two given vectors
	 * @param theV1
	 * @param theV2
	 */
	public static double angle(final CCVector2d theV1, CCVector2d theV2) {
		return CCMath.atan2(theV1.y - theV2.y, theV1.x - theV2.x);
	}

	/**
	 * @param theVector
	 * @param theMin
	 * @param theMax
	 */
	public static void constrain(CCVector3d theVector, double theMin, double theMax) {
		theVector.x = CCMath.constrain(theVector.x, theMin, theMax);
		theVector.y = CCMath.constrain(theVector.y, theMin, theMax);
		theVector.z = CCMath.constrain(theVector.z, theMin, theMax);
	}
}
