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

import cc.creativecomputing.math.d.CCVector2d;
import cc.creativecomputing.math.d.CCVector3d;

public class CCVecMath {
	
	/**
	 * Constructs a new vector consisting of the largest components of both vectors.
	 * 
	 * @param theVector2 the b
	 * @param theVector1 the a
	 * 
	 * @return result as new vector
	 */
	public static CCVector3f max(final CCVector3f theVector1, final CCVector3f theVector2) {
		return new CCVector3f(
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
	public static CCVector3f min(final CCVector3f theVector1, final CCVector3f theVector2) {
		return new CCVector3f(
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
	public static CCVector2f max(final CCVector2f theVector1, final CCVector2f theVector2) {
		return new CCVector2f(
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
	public static CCVector2f min(final CCVector2f theVector1, final CCVector2f theVector2) {
		return new CCVector2f(
			CCMath.min(theVector1.x, theVector2.x), 
			CCMath.min(theVector1.y, theVector2.y)
		);
	}
	
	//Rotate the point (x,y,z) around the vector (u,v,w)
	
	public static CCVector3f rotateAroundVector(final CCVector3f theVector, final CCVector3f theAxis, final float theAngle){
		
		final float x = theVector.x;
		final float y = theVector.y;
		final float z = theVector.z;
		
		final float u = theAxis.x;
		final float v = theAxis.y;
		final float w = theAxis.z;
		
		final float ux = u * x;
		final float uy = u * y;
		final float uz = u * z;
		
		final float vx = v * x;
		final float vy = v * y;
		final float vz = v * z;
		
		final float wx = w * x;
		final float wy = w * y;
		final float wz = w * z;
		
		final float sa = CCMath.sin(theAngle);
		final float ca = CCMath.cos(theAngle);
		
		return new CCVector3f(
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
	public static CCVector3f add(final CCVector3f theVector1, final CCVector3f theVector2){
		return new CCVector3f(
			theVector1.x + theVector2.x,
			theVector1.y + theVector2.y,
			theVector1.z + theVector2.z
			
		);
	}
	
	public static CCVector3f add(final CCVector3f ...theVector3fs){
		CCVector3f myResult = new CCVector3f();
		for(CCVector3f myVector:theVector3fs){
			myResult.add(myVector);
		}
		return myResult;
	}
	
	public static CCVector3d add(final CCVector3d ...theVector3ds){
		CCVector3d myResult = new CCVector3d();
		for(CCVector3d myVector:theVector3ds){
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
	public static CCVector2f add(final CCVector2f theVector1, final CCVector2f theVector2){
		return new CCVector2f(
			theVector1.x + theVector2.x,
			theVector1.y + theVector2.y
			
		);
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
	
	public static CCVector2f add(final CCVector2f ...theVector2fs){
		CCVector2f myResult = new CCVector2f();
		for(CCVector2f myVector:theVector2fs){
			myResult.add(myVector);
		}
		return myResult;
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
	public static CCVector3f subtract(final CCVector3f theVector1, final CCVector3f theVector2){
		return new CCVector3f(
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
	public static CCVector2f subtract(final CCVector2f theVector1, final CCVector2f theVector2){
		return new CCVector2f(
			theVector1.x - theVector2.x,
			theVector1.y - theVector2.y
			
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

	public static CCVector2f multiply(CCVector2f v1, float scalar){
		return new CCVector2f(v1.x*scalar, v1.y*scalar);
	}
	
	public static CCVector3f multiply(CCVector3f v1, float scalar){
		return new CCVector3f(v1.x*scalar, v1.y*scalar, v1.z*scalar);
	}
	
	public static CCVector3f divide(CCVector3f v1, float scalar){
		return new CCVector3f(v1.x/scalar, v1.y/scalar, v1.z/scalar);
	}
	
	public static CCVector2f divide(CCVector2f v1, float scalar){
		return new CCVector2f(v1.x/scalar, v1.y/scalar);
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
	public static CCVector3f cross(final CCVector3f theVector1, final CCVector3f theVector2){
		return new CCVector3f(
			theVector1.y * theVector2.z - theVector1.z * theVector2.y, 
			theVector1.z * theVector2.x - theVector1.x * theVector2.z, 
			theVector1.x * theVector2.y - theVector1.y * theVector2.x
		);
	}
	public static CCVector3f cross(final CCVector3f theVector1, final CCVector3f theVector2, final CCVector3f theStore){
		return theStore.set(
			theVector1.y * theVector2.z - theVector1.z * theVector2.y, 
			theVector1.z * theVector2.x - theVector1.x * theVector2.z, 
			theVector1.x * theVector2.y - theVector1.y * theVector2.x
		);
	}
	
	public static float dot(final CCVector3f theVector1, final CCVector3f theVector2){
		return theVector1.dot(theVector2);
	}
	
	public static float dot(final CCVector2f theVector1, final CCVector2f theVector2){
		return theVector1.dot(theVector2);
	}
	
	/**
	 * Calculates the normal to the plane defined by the three given vectors
	 * @param theV1 vector1 of the plane
	 * @param theV2 vector2 of the plane
	 * @param theV3 vector3 of the plane
	 * @return normal of the plane
	 */
	public static CCVector3f normal(final CCVector3f theV1, final CCVector3f theV2, final CCVector3f theV3) {
		CCVector3f v21 = subtract(theV2, theV1);
		CCVector3f v31 = subtract(theV3, theV1);
		
		CCVector3f normal = v21.cross(v31);
		return normal.normalize();
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
	 * Reflects the Vector according to the given normal
	 * @param theVector the vector to reflect
	 * @param theNormal the normal to use for reflection
	 * @return the Reflected input vector
	 */
	public static CCVector3f reflect(CCVector3f theVector, CCVector3f theNormal){
		float myDot = theVector.dot(theNormal);
		CCVector3f myResult = theNormal.clone();
		myResult.scale(-2 * myDot);
		myResult.add(theVector);
		return myResult;
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
	 *   float t = i / 10.0f;
	 *   float x = bezierPoint(85, 10, 90, 15, t);
	 *   float y = bezierPoint(20, 10, 90, 80, t);
	 *   vertex(x, y);
	 * }
	 * endShape();</PRE>
	 */
	static public CCVector3f bezierPoint(
		final CCVector3f theStartPoint, final CCVector3f theStartAnchor, 
		final CCVector3f theEndAnchor, final CCVector3f theEndPoint, 
		final float t
	) {
		float t1 = 1.0f - t;
		return new CCVector3f(
			theStartPoint.x * t1 * t1 * t1 + 3 * theStartAnchor.x * t * t1 * t1 + 3 * theEndAnchor.x * t * t * t1 + theEndPoint.x * t * t * t,
			theStartPoint.y * t1 * t1 * t1 + 3 * theStartAnchor.y * t * t1 * t1 + 3 * theEndAnchor.y * t * t * t1 + theEndPoint.y * t * t * t,
			theStartPoint.z * t1 * t1 * t1 + 3 * theStartAnchor.z * t * t1 * t1 + 3 * theEndAnchor.z * t * t * t1 + theEndPoint.z * t * t * t
		);
	}
	static public CCVector2f bezierPoint(CCVector2f a, CCVector2f b, CCVector2f c, CCVector2f d, float t) {
		float t1 = 1.0f - t;
		return new CCVector2f(
			a.x * t1 * t1 * t1 + 3 * b.x * t * t1 * t1 + 3 * c.x * t * t * t1 + d.x * t * t * t,
			a.y * t1 * t1 * t1 + 3 * b.y * t * t1 * t1 + 3 * c.y * t * t * t1 + d.y * t * t * t
		);
	}
	
	 /**Interpolate a spline between at least 4 control points following the Catmull-Rom equation.
     * here is the interpolation matrix
     * m = [ 0.0  1.0  0.0   0.0 ]
     *     [-T    0.0  T     0.0 ]
     *     [ 2T   T-3  3-2T  -T  ]
     *     [-T    2-T  T-2   T   ]
     * where T is the tension of the curve
     * the result is a value between p1 and p2, t=0 for p1, t=1 for p2
     * @param theU value from 0 to 1
     * @param theT The tension of the curve
     * @param theP0 control point 0
     * @param theP1 control point 1
     * @param theP2 control point 2
     * @param theP3 control point 3
     * @param store a Vector3f to store the result
     * @return catmull-Rom interpolation
     */
    public static CCVector3f catmulRomPoint(CCVector3f theP0, CCVector3f theP1, CCVector3f theP2, CCVector3f theP3, float theU, float theT) {
        return new CCVector3f(
        	CCMath.catmullRomPoint(theP0.x, theP1.x, theP2.x, theP3.x, theU, theT),
        	CCMath.catmullRomPoint(theP0.y, theP1.y, theP2.y, theP3.y, theU, theT),
        	CCMath.catmullRomPoint(theP0.z, theP1.z, theP2.z, theP3.z, theU, theT)
        );
    }

	
	static public CCVector3f round(final CCVector3f theVector){
		return new CCVector3f(
			CCMath.round(theVector.x),
			CCMath.round(theVector.y),
			CCMath.round(theVector.z)
		);
	}
	
	static public CCVector2f round(final CCVector2f theVector){
		return new CCVector2f(
			CCMath.round(theVector.x),
			CCMath.round(theVector.y)
		);
	}
	
	static public CCVector3f round(final CCVector3f theVector, int thePlaces){
		return new CCVector3f(
			CCMath.round(theVector.x,thePlaces),
			CCMath.round(theVector.y,thePlaces),
			CCMath.round(theVector.z,thePlaces)
		);
	}
	
	static public CCVector2f round(final CCVector2f theVector, int thePlaces){
		return new CCVector2f(
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
	 * @see #isInsideBox(CCVector2f, CCVector2f, float)
	 * @see #isInsideCircle(CCVector2f, CCVector2f, float)
	 * @return true if the test vector is inside the bounding volume otherwise false
	 */
	static public boolean isInside(final CCVector2f theTest, final CCVector2f theVector1, final CCVector2f theVector2){
		float minX, maxX;
		
		if(theVector1.x > theVector2.x){
			minX = theVector2.x;
			maxX = theVector1.x;
		}else{
			minX = theVector1.x;
			maxX = theVector2.x;
		}
		
		float minY, maxY;
		
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
	static public boolean isInside(final CCVector3f theTest, final CCVector3f theVector1, final CCVector3f theVector2){
		float minX, maxX;
		
		if(theVector1.x > theVector2.x){
			minX = theVector2.x;
			maxX = theVector1.x;
		}else{
			minX = theVector1.x;
			maxX = theVector2.x;
		}
		
		float minY, maxY;
		
		if(theVector1.y > theVector2.y){
			minY = theVector2.y;
			maxY = theVector1.y;
		}else{
			minY = theVector1.y;
			maxY = theVector2.y;
		}
		
		float minZ, maxZ;
		
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
	static public boolean isInsideBox(final CCVector2f theTest1, final CCVector2f theTest2, final float theRange){
		return 
			theTest1.x > theTest2.x - theRange &&
			theTest1.x < theTest2.x + theRange &&
			theTest1.y > theTest2.y - theRange &&
			theTest1.y < theTest2.y + theRange;
	}
	
	static public boolean isInsideBox(final CCVector3f theTest1, final CCVector3f theTest2, final float theRange){
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
	 * @see #isInside(CCVector2f, CCVector2f, CCVector2f)
	 * @see #isInsideBox(CCVector2f, CCVector2f, float)
	 * @return true if the vector 1 is inside the given range of vector2
	 */
	static public boolean isInsideCircle(final CCVector2f theTest1, final CCVector2f theTest2, final float theRange){
		return theTest1.distance(theTest2) < theRange;
	}
	
	static public boolean isInsideCircle(final CCVector3f theTest1, final CCVector3f theTest2, final float theRange){
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
	static public CCVector1f blend(final float theBlend, final CCVector1f theVector1, final CCVector1f theVector2){
		return new CCVector1f(
			theVector1.x * (1 - theBlend) + theVector2.x * theBlend
		);
	}
	
	static public CCVector2f blend(final float theBlend, final CCVector2f theVector1, final CCVector2f theVector2){
		return new CCVector2f(
			theVector1.x * (1 - theBlend) + theVector2.x * theBlend,
			theVector1.y * (1 - theBlend) + theVector2.y * theBlend
		);
	}
	
	static public CCVector2d blend(final double theBlend, final CCVector2d theVector1, final CCVector2d theVector2){
		return new CCVector2d(
			theVector1.x * (1 - theBlend) + theVector2.x * theBlend,
			theVector1.y * (1 - theBlend) + theVector2.y * theBlend
		);
	}
	
	static public CCVector3f blend(final float theBlend, final CCVector3f theVector1, final CCVector3f theVector2){
		return new CCVector3f(
			theVector1.x * (1 - theBlend) + theVector2.x * theBlend,
			theVector1.y * (1 - theBlend) + theVector2.y * theBlend,
			theVector1.z * (1 - theBlend) + theVector2.z * theBlend
		);
	}
	
	static public CCVector3f blend(float theBlendU, float theBlendV, CCVector3f theA, CCVector3f theB, CCVector3f theC) {
		return new CCVector3f(
			CCMath.blend(theBlendU, theBlendV, theA.x, theB.x, theC.x),
			CCMath.blend(theBlendU, theBlendV, theA.y, theB.y, theC.y),
			CCMath.blend(theBlendU, theBlendV, theA.z, theB.z, theC.z)
		);
	}
	
	static public CCVector4f blend(float theBlendU, float theBlendV, CCVector4f theA, CCVector4f theB, CCVector4f theC) {
		return new CCVector4f(
			CCMath.blend(theBlendU, theBlendV, theA.x, theB.x, theC.x),
			CCMath.blend(theBlendU, theBlendV, theA.y, theB.y, theC.y),
			CCMath.blend(theBlendU, theBlendV, theA.z, theB.z, theC.z),
			CCMath.blend(theBlendU, theBlendV, theA.w, theB.w, theC.w)
		);
	}
	
	static public CCVector3d blend(final double theBlend, final CCVector3d theVector1, final CCVector3d theVector2){
		return new CCVector3d(
			theVector1.x * (1 - theBlend) + theVector2.x * theBlend,
			theVector1.y * (1 - theBlend) + theVector2.y * theBlend,
			theVector1.z * (1 - theBlend) + theVector2.z * theBlend
		);
	}
	
	/**
	 * Goes the given distance from vector a to vector b and returns the vector at this point.
	 * @param theDistance distance to move from vector a towards vector b
	 * @param theA the vector to go from
	 * @param theB the vector to go to
	 * @return vector at the given distance in direction to the vector b
	 */
	static public CCVector3f goFromTo(final float theDistance, final CCVector3f theA, final CCVector3f theB){
		CCVector3f myMove = CCVecMath.subtract(theB, theA).normalize();
		return new CCVector3f(
			theA.x + myMove.x * theDistance,
			theA.y + myMove.y * theDistance,
			theA.z + myMove.z * theDistance
		);
	}

	
	/**
	 * Goes the given distance from vector a to vector b and returns the vector at this point.
	 * @param theDistance distance to move from vector a towards vector b
	 * @param theA the vector to go from
	 * @param theB the vector to go to
	 * @return vector at the given distance in direction to the vector b
	 */
	static public CCVector3d goFromTo(final double theDistance, final CCVector3d theA, final CCVector3d theB){
		CCVector3d myMove = CCVecMath.subtract(theB, theA).normalize();
		return new CCVector3d(
			theA.x + myMove.x * theDistance,
			theA.y + myMove.y * theDistance,
			theA.z + myMove.z * theDistance
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
	static public CCVector2f random(final float theX, final float theY){
		return new CCVector2f(CCMath.random(theX),CCMath.random(theY));
	}
	
	static public CCVector2f random(
		final float theMinX, final float theMaxX,
		final float theMinY, final float theMaxY
	){
		return new CCVector2f(
			CCMath.random(theMinX, theMaxX),
			CCMath.random(theMinY, theMaxY)
		);
	}
	
	static public CCVector3f random(final float theX, final float theY, final float theZ){
		return new CCVector3f(CCMath.random(theX),CCMath.random(theY),CCMath.random(theZ));
	}
	
	static public CCVector3f random(
		final float theMinX, final float theMaxX,
		final float theMinY, final float theMaxY,
		final float theMinZ, final float theMaxZ
	){
		return new CCVector3f(
			CCMath.random(theMinX, theMaxX),
			CCMath.random(theMinY, theMaxY),
			CCMath.random(theMinZ, theMaxZ)
		);
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
	
	public static CCVector3f random3f() {
		CCVector3f myResult = new CCVector3f(
			CCMath.random() * 2 - 1, 
			CCMath.random() * 2 - 1, 
			CCMath.random() * 2 - 1
		);
		return myResult.normalize();
	}
	
	public static CCVector3f random3f(final float theScale) {
		return random3f().scale(theScale);
	}
	
	public static CCVector2f random2f() {
		CCVector2f myResult = new CCVector2f(
			CCMath.random() * 2 - 1, 
			CCMath.random() * 2 - 1
		);
		return myResult.normalize();
	}
	
	public static CCVector2f random2f(final float theScale) {
		return random2f().scale(theScale);
	}

	/**
	 * Returns the angle between the two given vectors
	 * @param theV1
	 * @param theV2
	 */
	public static float angle(final CCVector2f theV1, CCVector2f theV2) {
		return CCMath.atan2(theV2.y, theV2.x) - CCMath.atan2(theV1.y, theV1.x);
	}

	/**
	 * Returns the angle between the two given vectors
	 * @param theV1
	 * @param theV2
	 */
	public static double angle(final CCVector2d theV1, CCVector2d theV2) {
		return CCMath.atan2(theV2.y, theV2.x) - CCMath.atan2(theV1.y, theV1.x);
	}
	
	/**
	 * Returns the angle between the two given vectors
	 * @param theV1
	 * @param theV2
	 * @return
	 */
	public static double angle(final CCVector3d theV1, final CCVector3d theV2) {
		double myDotProduct = theV1.dot(theV2);
		return CCMath.acos(myDotProduct);
	}
	
	/**
	 * Returns the angle between the two given vectors
	 * @param theV1
	 * @param theV2
	 * @return
	 */
	public static float angle(final CCVector3f theV1, final CCVector3f theV2) {
		float myDotProduct = theV1.dot(theV2);
		return CCMath.acos(myDotProduct);
	}

	/**
	 * @param theVector
	 * @param theMin
	 * @param theMax
	 */
	public static void constrain(CCVector3f theVector, float theMin, float theMax) {
		theVector.x = CCMath.constrain(theVector.x, theMin, theMax);
		theVector.y = CCMath.constrain(theVector.y, theMin, theMax);
		theVector.z = CCMath.constrain(theVector.z, theMin, theMax);
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
