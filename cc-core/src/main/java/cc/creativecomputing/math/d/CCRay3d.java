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

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMath;



/**
 * <code>CCRay</code> defines a line segment which has an origin and a direction. That is, a point and an infinite ray is
 * cast from this point. The ray is defined by the following equation: R(t) = origin + t*direction for t >= 0.
 * 
 * @author Mark Powell
 * @author Joshua Slack
 */
public class CCRay3d {

	/** 
	 * The ray's start point. 
	 **/
	private CCVector3d _myOrigin;
	
	/** 
	 * The direction of the ray. 
	 **/
	private CCVector3d _myDirection;

	protected static final CCVector3d tempVa = new CCVector3d();
	protected static final CCVector3d tempVb = new CCVector3d();
	protected static final CCVector3d tempVc = new CCVector3d();
	protected static final CCVector3d tempVd = new CCVector3d();

	/**
	 * Constructor instantiates a new <code>CCRay</code> object. As default, the origin is (0,0,0) and the direction is
	 * (0,0,0).
	 * 
	 */
	public CCRay3d() {
		_myOrigin = new CCVector3d();
		_myDirection = new CCVector3d();
	}

	/**
	 * Constructor instantiates a new <code>Ray</code> object. The origin and direction are given.
	 * 
	 * @param theOrigin the origin of the ray.
	 * @param theDirection the direction the ray travels in.
	 */
	public CCRay3d(final CCVector3d theOrigin, final CCVector3d theDirection) {
		_myOrigin = theOrigin;
		_myDirection = theDirection;
	}

	/**
	 * Determines if the Ray intersects a triangle.
	 * 
	 * @param theTriangle the Triangle to test against.
	 * @return true if the ray collides.
	 */
	public boolean intersect(final CCTriangle3d theTriangle) {
		return intersect(theTriangle.get(0), theTriangle.get(1), theTriangle.get(2));
	}

	/**
	 * Determines if the Ray intersects a triangle defined by the specified points.
	 * 
	 * @param theV0 first point of the triangle.
	 * @param theV1 second point of the triangle.
	 * @param theV2 third point of the triangle.
	 * @return true if the ray collides.
	 */
	public boolean intersect(final CCVector3d theV0, final CCVector3d theV1, final CCVector3d theV2) {
		return intersectWhere(theV0, theV1, theV2, null);
	}

	/**
	 * Determines if the Ray intersects a triangle. It then stores the point of intersection
	 * in the given loc vector
	 * 
	 * @param theTriangle the Triangle to test against.
	 * @param theLocation storage vector to save the collision point in (if the ray collides)
	 * @return true if the ray collides.
	 */
	public boolean intersectWhere(final CCTriangle3d theTriangle, final CCVector3d theLocation) {
		return intersectWhere(theTriangle.get(0), theTriangle.get(1), theTriangle.get(2), theLocation);
	}

	/**
	 * Determines if the Ray intersects a triangle defined by the specified points and if so
	 * it stores the point of intersection in the given loc vector.
	 * 
	 * @param v0 first point of the triangle.
	 * @param v1 second point of the triangle.
	 * @param v2 third point of the triangle.
	 * @param loc storage vector to save the collision point in (if the ray collides) if null, only boolean is
	 *        calculated.
	 * @return true if the ray collides.
	 */
	public boolean intersectWhere(CCVector3d v0, CCVector3d v1, CCVector3d v2, CCVector3d loc) {
		return intersects(v0, v1, v2, loc, false, false);
	}

	/**
	 * Determines if the Ray intersects a quad defined by the specified points and if
	 * so it stores the point of intersection in the given loc vector. One edge of the quad
	 * is [v0,v1], another one [v0,v2]. The behavior thus is like
	 * {@link #intersectWhere(CCVector3d, CCVector3d, CCVector3d, CCVector3d)} except for the extended area, which
	 * is equivalent to the union of the triangles [v0,v1,v2] and [-v0+v1+v2,v1,v2].
	 * 
	 * @param v0 top left point of the quad.
	 * @param v1 top right point of the quad.
	 * @param v2 bottom left point of the quad.
	 * @param loc storage vector to save the collision point in (if the ray collides) if null, only boolean is
	 *        calculated.
	 * @return true if the ray collides.
	 */
	public boolean intersectWhereQuad(CCVector3d v0, CCVector3d v1, CCVector3d v2, CCVector3d loc) {
		return intersects(v0, v1, v2, loc, false, true);
	}

	/**
	 * Determines if the Ray intersects a triangle and if so it stores the point of
	 * intersection in the given loc vector as t, u, v where t is the distance from the origin to the point of
	 * intersection and u,v is the intersection point in terms of the triangle plane.
	 * 
	 * @param theTriangle the Triangle to test against.
	 * @param theLocation storage vector to save the collision point in (if the ray collides) as t, u, v
	 * @return true if the ray collides.
	 */
	public boolean intersectWherePlanar(final CCTriangle3d theTriangle, final CCVector3d theLocation) {
		return intersectWherePlanar(theTriangle.get(0), theTriangle.get(1), theTriangle.get(2), theLocation);
	}

	/**
	 * Determines if the Ray intersects a triangle defined by the specified points and
	 * if so it stores the point of intersection in the given loc vector as t, u, v where t is the distance from the
	 * origin to the point of intersection and u,v is the intersection point in terms of the triangle plane.
	 * 
	 * @param v0 first point of the triangle.
	 * @param v1 second point of the triangle.
	 * @param v2 third point of the triangle.
	 * @param loc storage vector to save the collision point in (if the ray collides) as t, u, v
	 * @return true if the ray collides.
	 */
	public boolean intersectWherePlanar(CCVector3d v0, CCVector3d v1, CCVector3d v2, CCVector3d loc) {
		return intersects(v0, v1, v2, loc, true, false);
	}

	/**
	 * Does the actual intersection work.
	 * 
	 * @param v0 first point of the triangle.
	 * @param v1 second point of the triangle.
	 * @param v2 third point of the triangle.
	 * @param store storage vector - if null, no intersection is calc'd
	 * @param doPlanar true if we are calculating planar results.
	 * @param quad
	 * @return true if ray intersects triangle
	 */
//	private boolean intersects(
//		final CCVector3d v0, final CCVector3d v1, final CCVector3d v2, CCVector3d store, 
//		final boolean doPlanar, final boolean quad
//	) {
//		CCVector3d diff = CCVecMath.subtract(_myOrigin, v0);
//		CCVector3d edge1 = CCVecMath.subtract(v1, v0);
//		CCVector3d edge2 = CCVecMath.subtract(v2, v0);
//		CCVector3d norm = CCVecMath.cross(edge1, edge2);
//
//		double dirDotNorm = _myDirection.dot(norm);
//		double sign;
//		if (dirDotNorm > CCMath.FLT_EPSILON) {
//			sign = 1;
//		} else if (dirDotNorm < -CCMath.FLT_EPSILON) {
//			sign = -1f;
//			dirDotNorm = -dirDotNorm;
//		} else {
//			// ray and triangle/quad are parallel
//			return false;
//		}
//
//		double dirDotDiffxEdge2 = sign * _myDirection.dot(diff.cross(edge2));
//		if (dirDotDiffxEdge2 >= 0.0f) {
//			double dirDotEdge1xDiff = sign * _myDirection.dot(edge1.cross(diff));
//			if (dirDotEdge1xDiff >= 0.0f) {
//				if (!quad ? dirDotDiffxEdge2 + dirDotEdge1xDiff <= dirDotNorm : dirDotEdge1xDiff <= dirDotNorm) {
//					
//					double diffDotNorm = -sign * diff.dot(norm);
//					if (diffDotNorm >= 0.0f) {
//						// ray intersects triangle
//						// if storage vector is null, just return true,
//						if (store == null)
//							return true;
//						// else fill in.
//						double inv = 1f / dirDotNorm;
//						double t = diffDotNorm * inv;
//						if (!doPlanar) {
//							store.set(_myOrigin).add(_myDirection.x() * t, _myDirection.y() * t, _myDirection.z() * t);
//						} else {
//							// these weights can be used to determine
//							// interpolated values, such as texture coord.
//							// eg. texcoord s,t at intersection point:
//							// s = w0*s0 + w1*s1 + w2*s2;
//							// t = w0*t0 + w1*t1 + w2*t2;
//							double w1 = dirDotDiffxEdge2 * inv;
//							double w2 = dirDotEdge1xDiff * inv;
//							// double w0 = 1.0f - w1 - w2;
//							store.set(t, w1, w2);
//						}
//						return true;
//					}
//				}
//			}
//		}
//		return false;
//	}
	
	/**
	 * TODO there is a bug in the quad mode
     * <code>intersects</code> does the actual intersection work.
     *
     * @param v0
     *            first point of the triangle.
     * @param v1
     *            second point of the triangle.
     * @param v2
     *            third point of the triangle.
     * @param store
     *            storage vector - if null, no intersection is calc'd
     * @param doPlanar
     *            true if we are calcing planar results.
     * @param quad
     * @return true if ray intersects triangle
     */
    private boolean intersects(
    	CCVector3d v0, CCVector3d v1, CCVector3d v2,
        CCVector3d store, boolean doPlanar, boolean quad
    ) {
        CCVector3d diff = CCVecMathD.subtract(_myOrigin,v0);
        CCVector3d edge1 = CCVecMathD.subtract(v1, v0);
        CCVector3d edge2 = CCVecMathD.subtract(v2, v0);
        CCVector3d norm = edge1.cross(edge2);

        double dirDotNorm = _myDirection.dot(norm);
        double sign;
        if (dirDotNorm > CCMath.FLT_EPSILON) {
            sign = 1;
        } else if (dirDotNorm < -CCMath.FLT_EPSILON) {
            sign = -1f;
            dirDotNorm = -dirDotNorm;
        } else {
            // ray and triangle/quad are parallel
            return false;
        }

        double dirDotDiffxEdge2 = sign * _myDirection.dot(diff.cross(edge2));
        
        if (dirDotDiffxEdge2 >= 0.0f) {
            double dirDotEdge1xDiff = sign * _myDirection.dot(edge1.cross(diff));

            if (dirDotEdge1xDiff >= 0.0f) {
            	double diffDotNorm = -sign * diff.dot(norm);
                if ( !quad ? dirDotDiffxEdge2 + dirDotEdge1xDiff <= dirDotNorm : dirDotEdge1xDiff <= dirDotNorm ) {
                    
                	
                    if (diffDotNorm >= 0.0f) {
                        // ray intersects triangle
                        // if storage vector is null, just return true,
                        if (store == null)
                            return true;
                        // else fill in.
                        double inv = 1f / dirDotNorm;
                        double t = diffDotNorm * inv;
                        if (!doPlanar) {
                            store.set(_myOrigin);
                            store.add(_myDirection.x * t,_myDirection.y * t, _myDirection.z * t);
                        } else {
                            // these weights can be used to determine
                            // interpolated values, such as texture coord.
                            // eg. texcoord s,t at intersection point:
                            // s = w0*s0 + w1*s1 + w2*s2;
                            // t = w0*t0 + w1*t1 + w2*t2;
                            double w1 = dirDotDiffxEdge2 * inv;
                            double w2 = dirDotEdge1xDiff * inv;
                            //double w0 = 1.0f - w1 - w2;
                            store.set(t, w1, w2);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

	/**
	 * Determines if the Ray intersects a quad defined by the specified points and if
	 * so it stores the point of intersection in the given loc vector as t, u, v where t is the distance from the origin
	 * to the point of intersection and u,v is the intersection point in terms of the quad plane. One edge of the quad
	 * is [v0,v1], another one [v0,v2]. The behavior thus is like
	 * {@link #intersectWherePlanar(CCVector3d, CCVector3d, CCVector3d, CCVector3d)} except for the extended area, which
	 * is equivalent to the union of the triangles [v0,v1,v2] and [-v0+v1+v2,v1,v2].
	 * 
	 * @param v0 top left point of the quad.
	 * @param v1 top right point of the quad.
	 * @param v2 bottom left point of the quad.
	 * @param loc storage vector to save the collision point in (if the ray collides) as t, u, v
	 * @return true if the ray collides with the quad.
	 */
	public boolean intersectWherePlanarQuad(CCVector3d v0, CCVector3d v1, CCVector3d v2, CCVector3d loc) {
		return intersects(v0, v1, v2, loc, true, true);
	}

	/**
	 * 
	 * @param thePlane
	 * @param loc
	 * @return true if the ray collides with the given Plane
	 */
	public boolean intersectsWherePlane(final CCPlane3d thePlane, final CCVector3d theLocation) {
		double denominator = thePlane.normal().dot(_myDirection);

		if (denominator > -CCMath.FLT_EPSILON && denominator < CCMath.FLT_EPSILON)
			return false; // coplanar

		double numerator = -(thePlane.normal().dot(_myOrigin) - thePlane.constant());
		double ratio = numerator / denominator;

		if (ratio < CCMath.FLT_EPSILON)
			return false; // intersects behind origin

		theLocation.set(_myDirection).scale(ratio).add(_myOrigin);

		return true;
	}
	
	public CCVector3d closestPoint(final CCVector3d thePoint) {
		CCVector3d myResult = new CCVector3d();
		tempVa.set(CCVecMathD.subtract(thePoint,_myOrigin));
		double rayParam = _myDirection.dot(tempVa);
		
		if (rayParam > 0) {
			myResult.set(CCVecMathD.add(_myOrigin, _myDirection.clone().scale(rayParam)));
		} else {
			myResult.set(_myOrigin);
		}
		return myResult;
	}
	


    /**
     * Returns the point at the given distance on the ray. The distance can be
     * any real number.
     *
     * @param theDistance
     * @return vector
     */
    public CCVector3d pointAtDistance(double theDistance) {
        return _myDirection.clone().scale(theDistance).add(_myOrigin);
    }

	/**
	 * Returns the squared distance from the given point to the ray
	 * @param thePoint
	 * @return 
	 */
	public double distanceSquared(final CCVector3d thePoint) {
		tempVa.set(CCVecMathD.subtract(thePoint,_myOrigin));
		double rayParam = _myDirection.dot(tempVa);
		
		if (rayParam > 0) {
			tempVb.set(CCVecMathD.add(_myOrigin, _myDirection.clone().scale(rayParam)));
		} else {
			tempVb.set(_myOrigin);
			rayParam = 0.0f;
		}

		tempVa.set(CCVecMathD.subtract(thePoint, tempVb));
		return tempVa.lengthSquared();
	}
	
	public double distanceSquared(final double theX, final double theY, final double theZ) {
		return distanceSquared(new CCVector3d(theX, theY, theZ));
	}
	
	public double distance(final double theX, final double theY, final double theZ) {
		return CCMath.sqrt(distanceSquared(new CCVector3d(theX, theY, theZ)));
	}

	/**
	 * Retrieves the origin point of the ray.
	 * 
	 * @return the origin of the ray.
	 */
	public CCVector3d origin() {
		return _myOrigin;
	}

	/**
	 * Sets the origin of the ray.
	 * 
	 * @param theOrigin the origin of the ray.
	 */
	public void origin(final CCVector3d theOrigin) {
		_myOrigin = theOrigin;
	}

	/**
	 * Retrieves the direction vector of the ray.
	 * 
	 * @return the direction of the ray.
	 */
	public CCVector3d direction() {
		return _myDirection;
	}

	/**
	 * Sets the direction vector of the ray.
	 * 
	 * @param theDirection the direction of the ray.
	 */
	public void direction(final CCVector3d theDirection) {
		_myDirection = theDirection;
	}

	/**
	 * Copies information from a source ray into this ray.
	 * 
	 * @param theSource the ray to copy information from
	 */
	public void set(final CCRay3d theSource) {
		_myOrigin.set(theSource.origin());
		_myDirection.set(theSource.direction());
	}

	@Override
	public CCRay3d clone() {
		try {
			CCRay3d r = (CCRay3d) super.clone();
			r._myDirection = _myDirection.clone();
			r._myOrigin = _myDirection.clone();
			return r;
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}
	
	public void draw(CCGraphics g){
		g.line(
			(float)_myOrigin.x, 
			(float)_myOrigin.y, 
			(float)_myOrigin.z, 
			(float)_myOrigin.x + (float)_myDirection.x * 1000, 
			(float)_myOrigin.y + (float)_myDirection.y * 1000,
			(float)_myOrigin.z + (float)_myDirection.z * 1000
		);
	}
	
	@Override
	public String toString(){
		return "CCRAY:\norigin:" + _myOrigin + "\ndirection:" + _myDirection;
	}
}
