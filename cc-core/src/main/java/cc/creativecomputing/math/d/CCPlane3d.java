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

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCPolygonMode;
import cc.creativecomputing.math.CCMath;
/**
 * A plane in 3D space.
 * <p>
 * The plane is defined by a vector, <i>N</i> which is normal to the plane; and a constant, <i>C</i>, representing the
 * distance of the plane from the origin. The plane can be represented by the equation <i>C = N*p</i> where <i>p</i>
 * is a point on the plane.
 * 
 * @author Christian Riekoff
 */
public class CCPlane3d {

	/**
	 * Vector normal to the plane.
	 **/
	private CCVector3d _myNormal;

	/**
	 * Constant of the plane. See formula in class definition.
	 **/
	private double _myConstant;
	
	/**
	 * Number of lines in the drawing representation
	 */
	private int _myGridSize;

	/**
	 * Constructor instantiates a new <code>CCPlane</code> object. This is the default object and contains a normal of
	 * (0,0,0) and a constant of 0.
	 */
	public CCPlane3d() {
		_myNormal = new CCVector3d();
		_myGridSize = 20;
	}

	/**
	 * Constructor instantiates a new <code>CCPlane</code> object. The normal and constant values are set at creation.
	 * 
	 * @param theNormal the normal of the plane.
	 * @param theConstant the constant of the plane.
	 */
	public CCPlane3d(CCVector3d theNormal, final double theConstant) {
		if (theNormal == null) {
			theNormal = new CCVector3d();
		}
		_myNormal = theNormal;
		_myConstant = theConstant;
		_myGridSize = 20;
	}
	
	public CCPlane3d(final CCVector3d theV1, final CCVector3d theV2, final CCVector3d theV3){
		this();
		setPlanePoints(theV1, theV2, theV3);
	}

	/**
	 * Sets the normal of the plane.
	 * 
	 * @param theNormal the new normal of the plane.
	 */
	public void normal(CCVector3d theNormal) {
		if (theNormal == null) {
			theNormal = new CCVector3d();
		}
		_myNormal = theNormal;
	}

	/**
	 * Retrieves the normal of the plane.
	 * 
	 * @return the normal of the plane.
	 */
	public CCVector3d normal() {
		return _myNormal;
	}

	/**
	 * Sets the constant value that helps define the plane.
	 * 
	 * @param theConstant the new constant value.
	 */
	public void constant(final double theConstant) {
		_myConstant = theConstant;
	}

	/**
	 * Returns the constant of the plane.
	 * 
	 * @return the constant of the plane.
	 */
	public double constant() {
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
	public double pseudoDistance(final CCVector3d thePoint) {
		return _myNormal.dot(thePoint) - _myConstant;
	}

	/**
	 * Determine on which side of this plane the point {@code p} lies.
	 * 
	 * @param thePoint the point to check.
	 * @return the side at which the point lies.
	 */
	public CCPlaneSide whichSide(final CCVector3d thePoint) {
		double dis = pseudoDistance(thePoint);
		if (dis < 0) {
			return CCPlaneSide.NEGATIVE;
		}
		if (dis > 0) {
			return CCPlaneSide.POSITIVE;
		}
		return CCPlaneSide.NONE;
	}

	/**
	 * Initialize the Plane using the given 3 points as coplanar.
	 * 
	 * @param v1 the first point
	 * @param v2 the second point
	 * @param v3 the third point
	 */
	public void setPlanePoints(CCVector3d v1, CCVector3d v2, CCVector3d v3) {
		_myNormal.set(v2).subtract(v1);
		_myNormal = _myNormal.cross(v3.x - v1.x, v3.y - v1.y, v3.z - v1.z).normalize();
		_myConstant = _myNormal.dot(v1);
	}

	/**
	 * Returns a string that represents the string representation of this plane. It represents the
	 * normal as a <code>CCVector3d</code> object, so the format is the following: CCPlane [Normal:
	 * CCVector3d [X=XX.XXXX, Y=YY.YYYY, Z=ZZ.ZZZZ] - Constant: CC.CCCCC]
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
	public CCVector3d intersection(final CCRay3d theRay) {
		double denominator = _myNormal.dot(theRay.direction());

		if (denominator > -CCMath.FLT_EPSILON && denominator < CCMath.FLT_EPSILON)
			return null; // coplanar

		double numerator = -(_myNormal.dot(theRay.origin()) - _myConstant);
		double ratio = numerator / denominator;

//		if (ratio < CCMath.FLT_EPSILON)
//			return null; // intersects behind origin

		return new CCVector3d(theRay.direction()).scale(ratio).add(theRay.origin());
	}
	
	public double distance(final CCRay3d theRay) {
		double denominator = _myNormal.dot(theRay.direction());

		if (denominator > -CCMath.FLT_EPSILON && denominator < CCMath.FLT_EPSILON)
			return 0; // coplanar

		double numerator = -(_myNormal.dot(theRay.origin()) - _myConstant);
		double ratio = numerator / denominator;
		
		return ratio;
	}

	@Override
	public CCPlane3d clone() {
		try {
			CCPlane3d p = (CCPlane3d) super.clone();
			p._myNormal = _myNormal.clone();
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
	
	private double _myDrawScale = 1000f;
	
	public void drawScale(final double theDrawScale) {
		_myDrawScale = theDrawScale;
	}

	public void draw(CCGraphics g){
		
		CCVector3d u,v;
		
		   u = new CCVector3d(
			_myNormal.y - _myNormal.z, 
			_myNormal.z - _myNormal.x, 
			_myNormal.x - _myNormal.y
		   ); // cross product -> note that u lies on the plane
		   v = CCVecMathD.cross(_myNormal, u); // v is orthogonal to both N and u (again is in the plane)  
		

		// now simply draw a quad centered in a arbitrary point of the plane
		// and large enough to seems a plane
		CCVector3d P0 = _myNormal.clone().scale(_myConstant);        // "arbitrary" point
		double  f  = _myDrawScale;  // large enough
		CCVector3d fu =  u.scale(f);
		CCVector3d fv =  v.scale(f);
		
		CCVector3d P1 = new CCVector3d(P0.x - fu.x - fv.x, P0.y - fu.y - fv.y, P0.z - fu.z - fv.z);
		CCVector3d P2 = new CCVector3d(P0.x + fu.x - fv.x, P0.y + fu.y - fv.y, P0.z + fu.z - fv.z);
		CCVector3d P3 = new CCVector3d(P0.x + fu.x + fv.x, P0.y + fu.y + fv.y, P0.z + fu.z + fv.z);
		CCVector3d P4 = new CCVector3d(P0.x - fu.x + fv.x, P0.y - fu.y + fv.y, P0.z - fu.z + fv.z);

		// draw your vertices
		g.polygonMode(CCPolygonMode.LINE);
		g.beginShape(CCDrawMode.QUADS);
		g.vertex((float)P1.x, (float)P1.y, (float)P1.z);
		g.vertex((float)P2.x, (float)P2.y, (float)P2.z);
		g.vertex((float)P3.x, (float)P3.y, (float)P3.z);
		g.vertex((float)P4.x, (float)P4.y, (float)P4.z);
		g.endShape();
		g.polygonMode(CCPolygonMode.FILL);

		double myLineX;
		double myLineY;
		double myLineZ;
		
		double myBlend;
		
		g.beginShape(CCDrawMode.LINES);
		for(int i = 0; i < _myGridSize;i++){
			myBlend = i / ((float)_myGridSize - 1);
			myLineX = CCMath.blend(-fv.x, fv.x, myBlend);
			myLineY = CCMath.blend(-fv.y, fv.y, myBlend);
			myLineZ = CCMath.blend(-fv.z, fv.z, myBlend);
			
			g.vertex((float)(P0.x - fu.x + myLineX), (float)(P0.y - fu.y + myLineY), (float)(P0.z - fu.z + myLineZ));
			g.vertex((float)(P0.x + fu.x + myLineX), (float)(P0.y + fu.y + myLineY), (float)(P0.z + fu.z + myLineZ));
		}
		
		for(int i = 0; i < _myGridSize;i++){
			myBlend = i / ((float)_myGridSize - 1);
			myLineX = CCMath.blend(-fu.x, fu.x, myBlend);
			myLineY = CCMath.blend(-fu.y, fu.y, myBlend);
			myLineZ = CCMath.blend(-fu.z, fu.z, myBlend);
			
			g.vertex((float)(P0.x - fv.x + myLineX), (float)(P0.y - fv.y + myLineY), (float)(P0.z - fv.z + myLineZ));
			g.vertex((float)(P0.x + fv.x + myLineX), (float)(P0.y + fv.y + myLineY), (float)(P0.z + fv.z + myLineZ));
		}
		g.endShape();
	}
}
