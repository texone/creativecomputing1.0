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
package cc.creativecomputing.geometry;

import cc.creativecomputing.math.CCRay3f;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;


public class CCTriangleIntersector {
	
	public static class CCTriangleIntersectionData implements Comparable<CCTriangleIntersectionData>{
		
		private CCRay3f _myRay;
		private CCVector3f _myIntersection;
		public float t;
		public float u;
		public float v;
		
		protected CCTriangleIntersectionData(CCRay3f theRay, float theT, float theU, float theV) {
			_myRay = theRay;
			t = theT;
			u = theU;
			v = theV;
		}
		
		protected CCTriangleIntersectionData(CCTriangleIntersectionData theData) {
			this(theData._myRay, theData.t, theData.u, theData.v);
		}
	    
	    public CCRay3f ray() {
	    	return _myRay;
	    }
		
		public CCVector3f position() {
			if(_myIntersection == null) {
				_myIntersection = _myRay.pointAtDistance(t);
			}
			return _myIntersection;
		}

		
		@Override
		public int compareTo(CCTriangleIntersectionData theArg0) {
			if(theArg0.t < t)return 1;
			return -1;
		}
	}

    public CCTriangleIntersector() {
    }

    public CCTriangleIntersectionData intersectsRay(
    	CCRay3f ray, 
    	float theAX, float theAY, float theAZ,
    	float theBX, float theBY, float theBZ,
    	float theCX, float theCY, float theCZ
    ) {
    	return intersectsRay(
    		ray,
    		new CCVector3f(theAX, theAY, theAZ),
    		new CCVector3f(theBX, theBY, theBZ),
    		new CCVector3f(theCX, theCY, theCZ)
    	);
    }

    public CCTriangleIntersectionData intersectsRay(CCRay3f ray, CCVector3f theA, CCVector3f theB, CCVector3f theC) {
    	CCVector3f e1 = CCVecMath.subtract(theB,theA);
    	CCVector3f e2 = CCVecMath.subtract(theC,theA);

    	CCVector3f h = CCVecMath.cross(ray.direction(),e2);
    	float a = e1.dot(h);

    	if (a > -0.00001 && a < 0.00001)
    		return null;

    	float f = 1/a;
    	CCVector3f s = CCVecMath.subtract(ray.origin(),theA);
    	float u = f * s.dot(h);

    	if (u < 0.0 || u > 1.0)
    		return null;

    	CCVector3f q = CCVecMath.cross(s,e1);
    	float v = f * ray.direction().dot(q);

    	if (v < 0.0 || u + v > 1.0)
    		return null;

    	// at this stage we can compute t to find out where
    	// the intersection point is on the line
    	float t = f * e2.dot(q);

    	if (t > 0.00001) // ray intersection
    		return new CCTriangleIntersectionData(ray, t,u,v);

    	else // this means that there is a line intersection
    		 // but not a ray intersection
    		 return null;
    }
}
