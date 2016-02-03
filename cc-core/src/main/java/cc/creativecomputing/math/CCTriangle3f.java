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
 * <code>Triangle</code> defines a object for containing triangle information. The triangle is defined by a collection
 * of three <code>CCVector3f</code> objects.
 * 
 * @author Mark Powell
 * @author Joshua Slack
 * @author Christian Riekoff
 */
public class CCTriangle3f {

	private CCVector3f _myPointA;
	private CCVector3f _myPointB;
	private CCVector3f _myPointC;
	
	private float _myLengthAB;
	private float _myLengthBC;
	private float _myLengthCA;
	
	private float _myPerimeter;
	private float _myArea;

	private transient CCVector3f _myCenter;
	private transient CCVector3f _myNormal;

	private float _myProjection;

	private int _myIndex;

	public CCTriangle3f() {
		reset();
	}
	
	/**
	 * Returns a random point on the triangle.
	 * @return a random point on the triangle
	 */
	public CCVector3f randomPoint() {
//		float random = CCMath.random(3);
//		if(random > 2) {
//			return random(_myPointA, _myPointB, _myPointC);
//		}else if(random > 2) {
//			return random(_myPointB, _myPointC, _myPointA);
//		}else  {
//			return random(_myPointC, _myPointA, _myPointB);
//		}
		
//		float b1 = (CCMath.gaussianRandom() + 0.5f) % 1;
//		float b2 = (CCMath.gaussianRandom() + 0.5f) % 1;
//		float b3 = (CCMath.gaussianRandom() + 0.5f) % 1;
//		float sum = b1 + b2 + b3;
//		b1 /= sum;
//		b2 /= sum;
//		b3 /= sum;
//		return new CCVector3f(
//			_myPointA.x * b1 + _myPointB.x * b2 + _myPointC.x * b3,
//			_myPointA.y * b1 + _myPointB.y * b2 + _myPointC.y * b3,
//			_myPointA.z * b1 + _myPointB.z * b2 + _myPointC.z * b3
//		);
		
		float sqrtr1 = CCMath.sqrt(CCMath.random());
		float r2 = CCMath.random();
		return new CCVector3f(
			(1 - sqrtr1) * _myPointA.x + (sqrtr1 * (1 - r2)) * _myPointB.x + (sqrtr1 * r2) * _myPointC.x,
			(1 - sqrtr1) * _myPointA.y + (sqrtr1 * (1 - r2)) * _myPointB.y + (sqrtr1 * r2) * _myPointC.y,
			(1 - sqrtr1) * _myPointA.z + (sqrtr1 * (1 - r2)) * _myPointB.z + (sqrtr1 * r2) * _myPointC.z
		);
	}
	
	private void reset() {
		_myLengthAB = -1;
		_myLengthBC = -1;
		_myLengthCA = -1;
		_myPerimeter = -1;
		_myArea = -1;
		_myCenter = null;
	}

	/**
	 * Constructor instantiates a new <Code>Triangle</code> object with the supplied vectors as the points. It is
	 * recommended that the vertices be supplied in a counter clockwise winding to support normals for a right handed
	 * coordinate system.
	 * 
	 * @param thePoint1 the first point of the triangle.
	 * @param thePoint2 the second point of the triangle.
	 * @param thePoint3 the third point of the triangle.
	 */
	public CCTriangle3f(final CCVector3f thePoint1, final CCVector3f thePoint2, final CCVector3f thePoint3) {
		this();
		_myPointA = new CCVector3f(thePoint1);
		_myPointB = new CCVector3f(thePoint2);
		_myPointC = new CCVector3f(thePoint3);
	}
	
	public CCVector3f a() {
		return _myPointA;
	}
	
	public CCVector3f b() {
		return _myPointB;
	}
	
	public CCVector3f c() {
		return _myPointC;
	}
	
	/**
	 * Returns the length of edge ab
	 * @return the length of edge ab
	 */
	public float lengthAB() {
		if(_myLengthAB < 0) {
			_myLengthAB = _myPointA.distance(_myPointB);
		}
		return _myLengthAB;
	}
	
	/**
	 * Returns the length of edge bc
	 * @return the length of edge bc
	 */
	public float lengthBC() {
		if(_myLengthBC < 0) {
			_myLengthBC = _myPointB.distance(_myPointC);
		}
		return _myLengthBC;
	}
	
	/**
	 * Returns the length of edge ca
	 * @return the length of edge ca
	 */
	public float lengthCA() {
		if(_myLengthCA < 0) {
			_myLengthCA = _myPointC.distance(_myPointA);
		}
		return _myLengthCA;
	}
	
	/**
	 * Returns the perimeter of the triangle
	 * @return the perimeter of the triangle
	 */
	public float perimeter() {
		if(_myPerimeter < 0) {
			_myPerimeter = lengthAB() + lengthBC() + lengthCA();
		}
		return _myPerimeter;
	}
	
	/**
	 * Returns the area of the triangle
	 * @return the area of the triangle
	 */
	public float area() {
		if(_myArea < 0) {
			float s = perimeter() / 2;
			_myArea = CCMath.sqrt(s * (s - lengthAB()) * (s - lengthBC()) * (s - lengthCA()));
		}
		return _myArea;
	}

	/**
	 * 
	 * <code>get</code> retrieves a point on the triangle denoted by the index supplied.
	 * 
	 * @param theIndex the index of the point.
	 * @return the point.
	 */
	public CCVector3f get(final int theIndex) {
		switch (theIndex) {
		case 0:
			return _myPointA;
		case 1:
			return _myPointB;
		case 2:
			return _myPointC;
		default:
			return null;
		}
	}

	/**
	 * 
	 * <code>set</code> sets one of the triangles points to that specified as a parameter.
	 * 
	 * @param theIndex the index to place the point.
	 * @param thePoint the point to set.
	 */
	public void set(final int theIndex, final CCVector3f thePoint) {
		reset();
		switch (theIndex) {
		case 0:
			_myPointA.set(thePoint);
			break;
		case 1:
			_myPointB.set(thePoint);
			break;
		case 2:
			_myPointC.set(thePoint);
			break;
		}
	}
	
	public void set(CCVector3f theA, CCVector3f theB, CCVector3f theC) {
		reset();
		_myPointA = theA;
		_myPointB = theB;
		_myPointC = theC;
	}

	/**
	 * calculateCenter finds the average point of the triangle.
	 * 
	 */
	public void calculateCenter() {
		if (_myCenter == null)
			_myCenter = new CCVector3f(_myPointA);
		else
			_myCenter.set(_myPointA);
		_myCenter.add(_myPointB).add(_myPointC).scale(1f / 3f);
	}

	/**
	 * calculateCenter finds the average point of the triangle.
	 * 
	 */
	public void calculateNormal() {
		if (_myNormal == null)
			_myNormal = new CCVector3f(_myPointB);
		else
			_myNormal.set(_myPointB);
		_myNormal.subtract(_myPointA);
		_myNormal = _myNormal.cross(_myPointC.x - _myPointA.x, _myPointC.y - _myPointA.y, _myPointC.z - _myPointA.z);
		_myNormal.normalize();
	}

	/**
	 * obtains the center point of this triangle (average of the three triangles)
	 * 
	 * @return the center point.
	 */
	public CCVector3f center() {
		if (_myCenter == null) {
			calculateCenter();
		}
		return _myCenter;
	}

	/**
	 * sets the center point of this triangle (average of the three triangles)
	 * 
	 * @param theCenter the center point.
	 */
	public void center(final CCVector3f theCenter) {
		_myCenter = theCenter;
	}

	/**
	 * obtains the unit length normal vector of this triangle, if set or calculated
	 * 
	 * @return the normal vector
	 */
	public CCVector3f normal() {
		if (_myNormal == null) {
			calculateNormal();
		}
		return _myNormal;
	}

	/**
	 * sets the normal vector of this triangle (to conform, must be unit length)
	 * 
	 * @param theNormal the normal vector.
	 */
	public void normal(final CCVector3f theNormal) {
		_myNormal = theNormal;
	}

	/**
	 * obtains the projection of the vertices relative to the line origin.
	 * 
	 * @return the projection of the triangle.
	 */
	public float projection() {
		return _myProjection;
	}

	/**
	 * sets the projection of the vertices relative to the line origin.
	 * 
	 * @param theProjection the projection of the triangle.
	 */
	public void projection(final float theProjection) {
		_myProjection = theProjection;
	}

	/**
	 * obtains an index that this triangle represents if it is contained in a OBBTree.
	 * 
	 * @return the index in an OBBtree
	 */
	public int index() {
		return _myIndex;
	}

	/**
	 * sets an index that this triangle represents if it is contained in a OBBTree.
	 * 
	 * @param theIndex the index in an OBBtree
	 */
	public void index(final int theIndex) {
		_myIndex = theIndex;
	}
	
	/**
     * Checks if point vector is inside the triangle created by the points a, b
     * and c. These points will create a plane and the point checked will have
     * to be on this plane in the region between a,b,c (triangle vertices
     * inclusive).
     * 
     * @return true, if point is in triangle.
     */
    public boolean containsPoint(CCVector3f p) {
    	CCVector3f v0 = CCVecMath.subtract(_myPointC, _myPointA);
    	CCVector3f v1 = CCVecMath.subtract(_myPointB,_myPointA);
    	CCVector3f v2 = CCVecMath.subtract(p,_myPointA);

        // Compute dot products
        float dot00 = v0.dot(v0);
        float dot01 = v0.dot(v1);
        float dot02 = v0.dot(v2);
        float dot11 = v1.dot(v1);
        float dot12 = v1.dot(v2);

        // Compute barycentric coordinates
        float invDenom = 1.0f / (dot00 * dot11 - dot01 * dot01);
        float u = (dot11 * dot02 - dot01 * dot12) * invDenom;
        float v = (dot00 * dot12 - dot01 * dot02) * invDenom;

        // Check if point is in triangle
        return (u >= 0.0) && (v >= 0.0) && (u + v <= 1.0);
    }

	@Override
	public CCTriangle3f clone() {
		try {
			CCTriangle3f t = (CCTriangle3f) super.clone();
			t._myPointA = _myPointA.clone();
			t._myPointB = _myPointB.clone();
			t._myPointC = _myPointC.clone();
			return t;
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}
}
