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

import cc.creativecomputing.graphics.CCGraphics;



/**
 * Axis-aligned bounding box with basic intersection features for Ray, AABB and Sphere classes.
 */
public class CCAABB {

	/**
	 * Creates a new instance from two vectors specifying opposite corners of the box
	 * 
	 * @param theMin first corner point
	 * @param theMax second corner point
	 * @return new AABB with center at the half point between the 2 input vectors
	 */
	public static final CCAABB fromMinMax(final CCVector3f theMin, final CCVector3f theMax) {
		CCVector3f a = CCVecMath.min(theMin, theMax);
		CCVector3f b = CCVecMath.max(theMin, theMax);
		return new CCAABB(a.clone().add(b).scale(0.5f), b.clone().subtract(a).scale(0.5f));
	}

	private CCVector3f _myCenter;
	private CCVector3f _myExtent;

	private CCVector3f _myMin;
	private CCVector3f _myMax;

	public CCAABB() {
		_myCenter = new CCVector3f();
		_myMin = new CCVector3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
		_myMax = new CCVector3f(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
		_myExtent = new CCVector3f();
	}

	/**
	 * Creates an independent copy of the passed in box
	 * 
	 * @param theAABB
	 */
	public CCAABB(final CCAABB theAABB) {
		this(theAABB._myCenter.clone(), theAABB._myExtent.clone());
	}

	/**
	 * Creates a new instance from center point and extent
	 * 
	 * @param thePosition
	 * @param theExtent box dimensions (the box will be double the size in each direction)
	 */
	public CCAABB(final CCVector3f thePosition, final CCVector3f theExtent) {
		_myCenter = new CCVector3f(thePosition);
		extent(new CCVector3f(theExtent));
	}

	public CCAABB clone() {
		return new CCAABB(this);
	}

	/**
	 * Returns the current box size
	 * 
	 * @return box size
	 */
	public final CCVector3f extent() {
		return _myExtent;
	}

	public CCVector3f center() {
		return _myCenter;
	}

	/**
	 * Updates the position of the box in space and calls {@link #updateBounds()} immediately
	 */
	public void center(final float theX, final float theY, final float theZ) {
		_myCenter.set(theX, theY, theZ);
		updateBounds();
	}
	
	public void center(final CCVector3f theCenter) {
		_myCenter.set(theCenter);
		updateBounds();
	}

	public CCVector3f max() {
		return _myMax;
	}

	public CCVector3f min() {
		return _myMin;
	}
	
	/**
	 * Checks if the point is inside the given AABB.
	 * 
	 * @param box bounding box to check
	 * 
	 * @return true, if point is inside
	 */
	public boolean isInside(final CCVector3f theVector) {
		if (theVector.x < _myMin.x || theVector.x > _myMax.x) {
			return false;
		}
		if (theVector.y < _myMin.y || theVector.y > _myMax.y) {
			return false;
		}
		if (theVector.z < _myMin.z || theVector.z > _myMax.z) {
			return false;
		}
		return true;
	}

	/**
	 * Checks if the box intersects the passed in one.
	 * 
	 * @param theAABB box to check
	 * @return true, if boxes overlap
	 */
	public boolean intersectsBox(final CCAABB theAABB) {
		CCVector3f t = CCVecMath.subtract(theAABB._myCenter,_myCenter);
		return 
			CCMath.abs(t.x) <= (_myExtent.x + theAABB._myExtent.x) && 
			CCMath.abs(t.y) <= (_myExtent.y + theAABB._myExtent.y) && 
			CCMath.abs(t.z) <= (_myExtent.z + theAABB._myExtent.z);
	}

	/**
	 * Calculates intersection with the given ray between a certain distance interval.
	 * 
	 * Ray-box intersection is using IEEE numerical properties to ensure the test is both robust and efficient, as
	 * described in:
	 * 
	 * Amy Williams, Steve Barrus, R. Keith Morley, and Peter Shirley: "An Efficient and Robust Ray-Box Intersection
	 * Algorithm" Journal of graphics tools, 10(1):49-54, 2005
	 * 
	 * @param theRay incident ray
	 * @param theMinDist
	 * @param theMaxDist
	 * @return intersection point on the bounding box (only the first is returned) or null if no intersection
	 */
	public CCVector3f intersectsRay(final CCRay3f theRay, final float theMinDist, final float theMaxDist) {
		CCVector3f invDir = theRay.direction().reciprocal();
		boolean signDirX = invDir.x < 0;
		boolean signDirY = invDir.y < 0;
		boolean signDirZ = invDir.z < 0;
		
		CCVector3f bbox = signDirX ? _myMax : _myMin;
		float tmin = (bbox.x - theRay.origin().x) * invDir.x;
		
		bbox = signDirX ? _myMin : _myMax;
		float tmax = (bbox.x - theRay.origin().x) * invDir.x;
		
		bbox = signDirY ? _myMax : _myMin;
		float tymin = (bbox.y - theRay.origin().y) * invDir.y;
		
		bbox = signDirY ? _myMin : _myMax;
		float tymax = (bbox.y - theRay.origin().y) * invDir.y;
		
		if ((tmin > tymax) || (tymin > tmax)) {
			return null;
		}
		if (tymin > tmin) {
			tmin = tymin;
		}
		if (tymax < tmax) {
			tmax = tymax;
		}
		
		bbox = signDirZ ? _myMax : _myMin;
		float tzmin = (bbox.z - theRay.origin().z) * invDir.z;
		
		bbox = signDirZ ? _myMin : _myMax;
		float tzmax = (bbox.z - theRay.origin().z) * invDir.z;
		
		if ((tmin > tzmax) || (tzmin > tmax)) {
			return null;
		}
		if (tzmin > tmin) {
			tmin = tzmin;
		}
		if (tzmax < tmax) {
			tmax = tzmax;
		}
		if ((tmin < theMaxDist) && (tmax > theMinDist)) {
			return theRay.pointAtDistance(tmin);
		}
		return null;
	}

	public boolean intersectsSphere(final CCSphere3f theSphere) {
		return intersectsSphere(theSphere.center(), theSphere.radius());
	}

	/**
	 * @param theCenter sphere center
	 * @param theRadius sphere radius
	 * @return true, if AABB intersects with sphere
	 */
	public boolean intersectsSphere(final CCVector3f theCenter, final float theRadius) {
		float s, d = 0;
		// find the square of the distance
		// from the sphere to the box
		if (theCenter.x < _myMin.x) {
			s = theCenter.x - _myMin.x;
			d = s * s;
		} else if (theCenter.x > _myMax.x) {
			s = theCenter.x - _myMax.x;
			d += s * s;
		}

		if (theCenter.y < _myMin.y) {
			s = theCenter.y - _myMin.y;
			d += s * s;
		} else if (theCenter.y > _myMax.y) {
			s = theCenter.y - _myMax.y;
			d += s * s;
		}

		if (theCenter.z < _myMin.z) {
			s = theCenter.z - _myMin.z;
			d += s * s;
		} else if (theCenter.z > _myMax.z) {
			s = theCenter.z - _myMax.z;
			d += s * s;
		}

		return d <= theRadius * theRadius;
	}

	public void set(final CCAABB theAABB) {
		_myExtent.set(theAABB._myExtent);
		center(theAABB._myCenter);
	}

	/**
	 * Updates the size of the box and calls {@link #updateBounds()} immediately
	 * 
	 * @param theExtent new box size
	 * @return itself, for method chaining
	 */
	public CCAABB extent(final CCVector3f theExtent) {
		_myExtent = theExtent;
		return updateBounds();
	}

	/**
	 * Increases the size of the box so that the given vector is inside the box
	 * 
	 * @param thePoint the point to check
	 * @return itself, for method chaining
	 */
	public void checkSize(final CCVector3f thePoint) {
		if(thePoint.x > _myMax.x)_myMax.x = thePoint.x;
		if(thePoint.y > _myMax.y)_myMax.y = thePoint.y;
		if(thePoint.z > _myMax.z)_myMax.z = thePoint.z;
		
		if(thePoint.x < _myMin.x)_myMin.x = thePoint.x;
		if(thePoint.y < _myMin.y)_myMin.y = thePoint.y;
		if(thePoint.z < _myMin.z)_myMin.z = thePoint.z;
		
		_myExtent.x = (_myMax.x - _myMin.x) / 2;
		_myExtent.y = (_myMax.y - _myMin.y) / 2;
		_myExtent.z = (_myMax.z - _myMin.z) / 2;
		
		_myCenter.x = _myMax.x - _myExtent.x;
		_myCenter.y = _myMax.y - _myExtent.y;
		_myCenter.z = _myMax.z - _myExtent.z;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("<aabb> pos: ").append(_myCenter).append(" ext: ").append(_myExtent);
		return sb.toString();
	}

	/**
	 * Updates the min/max corner points of the box. MUST be called after moving the box in space by manipulating the
	 * public x,y,z coordinates directly.
	 * 
	 * @return itself
	 */
	public final CCAABB updateBounds() {
		// this is check is necessary for the constructor
		if (_myExtent != null) {
			_myMin = CCVecMath.subtract(_myCenter,_myExtent);
			_myMax = CCVecMath.add(_myCenter,_myExtent);
		}
		return this;
	}
	
	
	public void draw(CCGraphics g) {
		g.pushMatrix();
		g.translate(_myCenter);
		g.boxGrid(_myExtent.x * 2, _myExtent.y * 2, _myExtent.z * 2);
		g.popMatrix();
	}
}
