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


public class CCSphere3f{

	private CCVector3f _myCenter;
    private float _myRadius;

    public CCSphere3f() {
        this(new CCVector3f(), 1);
    }

    public CCSphere3f(final CCSphere3f theSphere) {
        this(theSphere._myCenter, theSphere._myRadius);
    }

    public CCSphere3f(final CCVector3f theCenter, final float theRadius) {
        _myCenter = new CCVector3f(theCenter);
        _myRadius = theRadius;
    }
    
    public CCVector3f center() {
    	return _myCenter;
    }

    public boolean containsPoint(final CCVector3f theVector) {
        float d = _myCenter.distanceSquared(theVector);
        return (d <= _myRadius * _myRadius);
    }

    /**
     * Alternative to {@link SphereIntersectorReflector}. Computes primary &
     * secondary intersection points of this sphere with the given ray. If no
     * intersection is found the method returns null. In all other cases, the
     * returned array will contain the distance to the primary intersection
     * point (i.e. the closest in the direction of the ray) as its first index
     * and the other one as its second. If any of distance values is negative,
     * the intersection point lies in the opposite ray direction (might be
     * useful to know). To get the actual intersection point coordinates, simply
     * pass the returned values to {@link Ray3D#getPointAtDistance(float)}.
     * 
     * @param theRay
     * @return 2-element float array of intersection points or null if ray
     *         doesn't intersect sphere at all.
     */
    public float[] intersectRay(final CCRay3f theRay) {
        float[] result = null;
        CCVector3f q = CCVecMath.subtract(theRay.origin(), _myCenter);
        float distSquared = q.lengthSquared();
        float v = -q.dot(theRay.direction());
        float d = _myRadius * _myRadius - (distSquared - v * v);
        if (d >= 0.0) {
            d = (float) Math.sqrt(d);
            float a = v + d;
            float b = v - d;
            if (!(a < 0 && b < 0)) {
                if (a > 0 && b > 0) {
                    if (a > b) {
                        float t = a;
                        a = b;
                        b = t;
                    }
                } else {
                    if (b > 0) {
                        float t = a;
                        a = b;
                        b = t;
                    }
                }
            }
            result = new float[] { a, b };
        }
        return result;
    }

	/**
	 * @param theRadius the radius to set
	 */
	public void radius(final float theRadius) {
		_myRadius = theRadius;
	}

	/**
	 * @return the radius
	 */
	public float radius() {
		return _myRadius;
	}
}

