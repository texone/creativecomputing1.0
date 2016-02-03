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

import cc.creativecomputing.math.CCVecMath;


public class CCSphere3d{

	private CCVector3d _myCenter;
    private double _myRadius;

    public CCSphere3d() {
        this(new CCVector3d(), 1);
    }

    public CCSphere3d(final CCSphere3d theSphere) {
        this(theSphere._myCenter, theSphere._myRadius);
    }

    public CCSphere3d(final CCVector3d theCenter, final double theRadius) {
        _myCenter = new CCVector3d(theCenter);
        _myRadius = theRadius;
    }
    
    public CCVector3d center() {
    	return _myCenter;
    }

    public boolean containsPoint(final CCVector3d theVector) {
        double d = _myCenter.distanceSquared(theVector);
        return (d <= _myRadius * _myRadius);
    }

    /**
     * Computes primary and secondary intersection points of this sphere with the 
     * given ray. If no intersection is found the method returns null. In all other 
     * cases, the returned array will contain the distance to the primary intersection
     * point (i.e. the closest in the direction of the ray) as its first index
     * and the other one as its second. If any of distance values is negative,
     * the intersection point lies in the opposite ray direction (might be
     * useful to know). To get the actual intersection point coordinates, simply
     * pass the returned values to {@link CCRay3d#pointAtDistance(double)}.
     * 
     * @param theRay
     * @return 2-element double array of intersection points or null if ray
     *         doesn't intersect sphere at all.
     */
    public double[] intersectRay(final CCRay3d theRay) {
        double[] result = null;
        CCVector3d q = CCVecMath.subtract(theRay.origin(), _myCenter);
        double distSquared = q.lengthSquared();
        double v = -q.dot(theRay.direction());
        double d = _myRadius * _myRadius - (distSquared - v * v);
        if (d >= 0.0) {
            d = (double) Math.sqrt(d);
            double a = v + d;
            double b = v - d;
            if (!(a < 0 && b < 0)) {
                if (a > 0 && b > 0) {
                    if (a > b) {
                        double t = a;
                        a = b;
                        b = t;
                    }
                } else {
                    if (b > 0) {
                        double t = a;
                        a = b;
                        b = t;
                    }
                }
            }
            result = new double[] { a, b };
        }
        return result;
    }

	/**
	 * @param theRadius the radius to set
	 */
	public void radius(final double theRadius) {
		_myRadius = theRadius;
	}

	/**
	 * @return the radius
	 */
	public double radius() {
		return _myRadius;
	}
}

