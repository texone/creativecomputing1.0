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

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCPolygonMode;

/**
 * A plane in 3D space.
 * <p>
 * The plane is defined by a vector, <i>N</i> which is normal to the plane; and a constant, <i>C</i>, representing the
 * distance of the plane from the origin. The plane can be represented by the equation <i>C = N*p</i> where <i>p</i>
 * is a point on the plane.
 * 
 * @author Christian Riekoff
 */
public class CCPlane3f {

	/**
	 * Vector normal to the plane.
	 **/
	private CCVector3f _myNormal;
	
	private CCVector3f _myOrigin;

	/**
	 * Constant of the plane. See formula in class definition.
	 **/
	private float _myConstant;
	
	/**
	 * Number of lines in the drawing representation
	 */
	private int _myGridSize = 20;

	/**
	 * Constructor instantiates a new <code>CCPlane</code> object. This is the default object and contains a normal of
	 * (0,0,0) and a constant of 0.
	 */
	public CCPlane3f() {
		_myNormal = new CCVector3f();
		_myOrigin = new CCVector3f();
	}

	/**
	 * Constructor instantiates a new <code>CCPlane</code> object. The normal and constant values are set at creation.
	 * 
	 * @param theNormal the normal of the plane.
	 * @param theConstant the constant of the plane.
	 */
	public CCPlane3f(CCVector3f theNormal, final float theConstant) {
		if (theNormal == null) {
			theNormal = new CCVector3f();
		}
		_myNormal = theNormal;
		_myOrigin = _myNormal.clone().scale(theConstant);
		_myConstant = theConstant;
	}
	
	/**
	 * Creates a plane based on a normal and an origin point lying on the plane
	 * @param theOrigin point on the plane
	 * @param theNormal normal of the plane
	 */
	public CCPlane3f(CCVector3f theOrigin, CCVector3f theNormal) {
		_myNormal = theNormal;
		_myOrigin = theOrigin;
		setOriginNormal(theOrigin, theNormal);
	}
	
	public CCPlane3f(final CCVector3f theV1, final CCVector3f theV2, final CCVector3f theV3){
		this();
		setPlanePoints(theV1, theV2, theV3);
	}

	/**
	 * Sets the normal of the plane.
	 * 
	 * @param theNormal the new normal of the plane.
	 */
	public void normal(CCVector3f theNormal) {
		if (theNormal == null) {
			theNormal = new CCVector3f();
		}
		_myNormal = theNormal;
	}

	/**
	 * Retrieves the normal of the plane.
	 * 
	 * @return the normal of the plane.
	 */
	public CCVector3f normal() {
		return _myNormal;
	}

	/**
	 * Sets the constant value that helps define the plane.
	 * 
	 * @param theConstant the new constant value.
	 */
	public void constant(final float theConstant) {
		_myConstant = theConstant;
		_myOrigin = _myNormal.clone().scale(_myConstant);
	}

	/**
	 * Returns the constant of the plane.
	 * 
	 * @return the constant of the plane.
	 */
	public float constant() {
		return _myConstant;
	}

	/**
	 * Calculates the distance from this plane to a provided point. If the point is on the
	 * negative side of the plane the distance returned is negative, otherwise it is positive. If the point is on the
	 * plane, it is zero.
	 * 
	 * @param thePoint the point to check.
	 * @return the signed distance from the plane to a point.
	 */
	public float pseudoDistance(final CCVector3f thePoint) {
		return _myNormal.dot(thePoint) - _myConstant;
	}

	/**
	 * Determine on which side of this plane the point {@code p} lies.
	 * 
	 * @param thePoint the point to check.
	 * @return the side at which the point lies.
	 */
	public CCPlaneSide whichSide(final CCVector3f thePoint) {
		float dis = pseudoDistance(thePoint);
		if (dis < 0) {
			return CCPlaneSide.NEGATIVE;
		}
		if (dis > 0) {
			return CCPlaneSide.POSITIVE;
		}
		return CCPlaneSide.NONE;
	}

	/**
	 * Initialize this plane using the three points of the given triangle.
	 * 
	 * @param theTriangle the triangle
	 */
	public void setPlanePoints(final CCTriangle3f theTriangle) {
		setPlanePoints(theTriangle.get(0), theTriangle.get(1), theTriangle.get(2));
	}

	/**
	 * Initialize the Plane using the given 3 points as coplanar.
	 * 
	 * @param v1 the first point
	 * @param v2 the second point
	 * @param v3 the third point
	 */
	public void setPlanePoints(CCVector3f v1, CCVector3f v2, CCVector3f v3) {
		_myOrigin.set(v1);
		_myNormal.set(v2).subtract(v1);
		_myNormal = _myNormal.cross(v3.x - v1.x, v3.y - v1.y, v3.z - v1.z).normalize();
		_myConstant = _myNormal.dot(v1);
	}
	
	/**
     * Initialize this plane using a point of origin and a normal.
     *
     * @param theOrigin
     * @param theNormal
     */
    public void setOriginNormal(CCVector3f theOrigin, CCVector3f theNormal){
		_myOrigin.set(theOrigin);
        _myNormal.set(theNormal);
        _myConstant = theNormal.x * theOrigin.x + theNormal.y * theOrigin.y + theNormal.z * theOrigin.z;
    }

	/**
	 * Returns a string that represents the string representation of this plane. It represents the
	 * normal as a <code>CCVector3f</code> object, so the format is the following: CCPlane [Normal:
	 * CCVector3f [X=XX.XXXX, Y=YY.YYYY, Z=ZZ.ZZZZ] - Constant: CC.CCCCC]
	 * 
	 * @return the string representation of this plane.
	 */
	public String toString() {
		return "CCPlane [Normal: " + _myNormal + " - Constant: " + _myConstant + "]";
	}
	
	/**
	 * Calculates the intersection of this plane with the given ray. If there is no intersection
	 * because the ray and the plane are coplanar or the intersection is behind the rays origin
	 * the method return null.
	 * @param theRay used to calculate intersection
	 * @return the intersection point
	 */
	public CCVector3f intersection(final CCRay3f theRay) {
		float denominator = _myNormal.dot(theRay.direction());

		if (denominator > -CCMath.FLT_EPSILON && denominator < CCMath.FLT_EPSILON)
			return null; // coplanar

		float numerator = -(_myNormal.dot(theRay.origin()) - _myConstant);
		float ratio = numerator / denominator;

		if (ratio < CCMath.FLT_EPSILON)
			return null; // intersects behind origin

		return new CCVector3f(theRay.direction()).scale(ratio).add(theRay.origin());
	}
	
	public float distance(final CCRay3f theRay) {
		float denominator = _myNormal.dot(theRay.direction());

		if (denominator > -CCMath.FLT_EPSILON && denominator < CCMath.FLT_EPSILON)
			return 0; // coplanar

		float numerator = -(_myNormal.dot(theRay.origin()) - _myConstant);
		float ratio = numerator / denominator;
		
		return ratio;
	}
	
	public float distance(CCVector3f thePoint) {
		return _myNormal.dot(thePoint) - _myConstant;
	}

	@Override
	public CCPlane3f clone() {
		try {
			CCPlane3f p = (CCPlane3f) super.clone();
			p._myNormal = _myNormal.clone();
			p._myOrigin = _myOrigin.clone();
			return p;
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}

	public static enum CCPlaneSide {
		/** A point on the side opposite the normal to the plane. */
		NEGATIVE,
		/** A point on the plane itself. */
		NONE,
		/** A point on the side of the normal to the plane. */
		POSITIVE
	}
	
	private float _myDrawScale = 1000f;
	
	public void drawScale(final float theDrawScale) {
		_myDrawScale = theDrawScale;
	}

	public void draw(CCGraphics g){
		
		CCVector3f u,v;
		
		   u = new CCVector3f(
			_myNormal.y - _myNormal.z, 
			_myNormal.z - _myNormal.x, 
			_myNormal.x - _myNormal.y
		   ); // cross product -> note that u lies on the plane
		   v = CCVecMath.cross(_myNormal, u); // v is orthogonal to both N and u (again is in the plane)  
		

		// now simply draw a quad centered in a arbitrary point of the plane
		// and large enough to seems a plane
		CCVector3f P0 = _myOrigin;        // "arbitrary" point
		float  f  = _myDrawScale;  // large enough
		CCVector3f fu =  u.scale(f);
		CCVector3f fv =  v.scale(f);
		
		CCVector3f P1 = new CCVector3f(P0.x - fu.x - fv.x, P0.y - fu.y - fv.y, P0.z - fu.z - fv.z);
		CCVector3f P2 = new CCVector3f(P0.x + fu.x - fv.x, P0.y + fu.y - fv.y, P0.z + fu.z - fv.z);
		CCVector3f P3 = new CCVector3f(P0.x + fu.x + fv.x, P0.y + fu.y + fv.y, P0.z + fu.z + fv.z);
		CCVector3f P4 = new CCVector3f(P0.x - fu.x + fv.x, P0.y - fu.y + fv.y, P0.z - fu.z + fv.z);

		// draw your vertices
		g.polygonMode(CCPolygonMode.LINE);
		g.beginShape(CCDrawMode.QUADS);
		g.vertex(P1);
		g.vertex(P2);
		g.vertex(P3);
		g.vertex(P4);
		g.endShape();
		g.polygonMode(CCPolygonMode.FILL);

		float myLineX;
		float myLineY;
		float myLineZ;
		
		float myBlend;
		
		g.beginShape(CCDrawMode.LINES);
		for(int i = 0; i < _myGridSize;i++){
			myBlend = i / ((float)_myGridSize - 1);
			myLineX = CCMath.blend(-fv.x, fv.x, myBlend);
			myLineY = CCMath.blend(-fv.y, fv.y, myBlend);
			myLineZ = CCMath.blend(-fv.z, fv.z, myBlend);
			
			g.vertex(P0.x - fu.x + myLineX, P0.y - fu.y + myLineY, P0.z - fu.z + myLineZ);
			g.vertex(P0.x + fu.x + myLineX, P0.y + fu.y + myLineY, P0.z + fu.z + myLineZ);
		}
		
		for(int i = 0; i < _myGridSize;i++){
			myBlend = i / ((float)_myGridSize - 1);
			myLineX = CCMath.blend(-fu.x, fu.x, myBlend);
			myLineY = CCMath.blend(-fu.y, fu.y, myBlend);
			myLineZ = CCMath.blend(-fu.z, fu.z, myBlend);
			
			g.vertex(P0.x - fv.x + myLineX, P0.y - fv.y + myLineY, P0.z - fv.z + myLineZ);
			g.vertex(P0.x + fv.x + myLineX, P0.y + fv.y + myLineY, P0.z + fv.z + myLineZ);
		}
		
		g.vertex(0,0,0);
		g.vertex(_myNormal.x * _myDrawScale, _myNormal.y * _myDrawScale, _myNormal.z * _myDrawScale);
		g.endShape();
	}
}
