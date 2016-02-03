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
package cc.creativecomputing.graphics.util;

import cc.creativecomputing.graphics.CCCamera;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCPlane3f;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;

public class CCFrustum {
	private static enum CCFrustumPlane {
		TOP, BOTTOM, LEFT, RIGHT, NEARP, FARP
	};

	public static enum CCFrustumRelation {
		OUTSIDE, INTERSECT, INSIDE
	};

	private CCPlane3f[] _myFrustumPlanes = new CCPlane3f[6];

	private CCVector3f _myNearTopLeft;
	private CCVector3f _myNearTopRight;
	private CCVector3f _myNearBottomLeft;
	private CCVector3f _myNearBottomRight;

	private CCVector3f _myFarTopLeft;
	private CCVector3f _myFarTopRight;
	private CCVector3f _myFarBottomLeft;
	private CCVector3f _myFarBottomRight;

	private float _myNear, _myFar, _myAspect, _myFov, _myTang;
	
	private float _myNearWidth;
	private float _myNearHeight;
	private float _myFarWidth;
	private float _myFarHeight;
	
	private CCCamera _myCamera;

	public CCFrustum(final CCCamera theCamera) {
		_myCamera = theCamera;
		
		updateFromCamera();
		setCamDef(_myCamera.position(), _myCamera.target(), _myCamera.up());
	}

	public void updateFromCamera() {

		_myFov = _myCamera.fov();
		_myAspect = _myCamera.aspect();
		_myNear = _myCamera.near();
		_myFar = _myCamera.far();

		_myTang = (float) CCMath.tan(_myFov * 0.5f);
		_myNearHeight = _myNear * _myTang;
		_myNearWidth = _myNearHeight * _myAspect;
		_myFarHeight = _myFar * _myTang;
		_myFarWidth = _myFarHeight * _myAspect;
		
		CCVector3f myZ = CCVecMath.subtract(_myCamera.position(), _myCamera.target());
		myZ.normalize();

		CCVector3f myX = CCVecMath.cross(_myCamera.up(), myZ);
		myX.normalize();

		CCVector3f myY = CCVecMath.cross(myZ, myX);

		CCVector3f myNearCenter = CCVecMath.subtract(_myCamera.position(), myZ.clone().scale(_myNear));
		CCVector3f myFarCenter = CCVecMath.subtract(_myCamera.position(), myZ.clone().scale(_myFar));
		
		float myNearFrustumOffsetX = _myCamera.frustumOffset().x;
		float myNearFrustumOffsetY = _myCamera.frustumOffset().y;
		
		float myFarFrustumOffsetX = myNearFrustumOffsetX * _myFar / _myNear;
		float myFarFrustumOffsetY = myNearFrustumOffsetY * _myFar / _myNear;

		_myNearTopLeft = CCVecMath.subtract(CCVecMath.add(myNearCenter, myY.clone().scale(_myNearHeight + myNearFrustumOffsetY)), myX.clone().scale(_myNearWidth - myNearFrustumOffsetX));
		_myNearTopRight = CCVecMath.add(CCVecMath.add(myNearCenter, myY.clone().scale(_myNearHeight + myNearFrustumOffsetY)), myX.clone().scale(_myNearWidth + myNearFrustumOffsetX));
		_myNearBottomLeft = CCVecMath.subtract(CCVecMath.subtract(myNearCenter, myY.clone().scale(_myNearHeight - myNearFrustumOffsetY)), myX.clone().scale(_myNearWidth - myNearFrustumOffsetX));
		_myNearBottomRight = CCVecMath.add(CCVecMath.subtract(myNearCenter, myY.clone().scale(_myNearHeight - myNearFrustumOffsetY)), myX.clone().scale(_myNearWidth + myNearFrustumOffsetX));

		_myFarTopLeft = CCVecMath.subtract(CCVecMath.add(myFarCenter, myY.clone().scale(_myFarHeight + myFarFrustumOffsetY)), myX.clone().scale(_myFarWidth - myFarFrustumOffsetX));
		_myFarTopRight = CCVecMath.add(CCVecMath.add(myFarCenter, myY.clone().scale(_myFarHeight + myFarFrustumOffsetY)), myX.clone().scale(_myFarWidth + myFarFrustumOffsetX));
		_myFarBottomLeft = CCVecMath.subtract(CCVecMath.subtract(myFarCenter, myY.clone().scale(_myFarHeight - myFarFrustumOffsetY)), myX.clone().scale(_myFarWidth - myFarFrustumOffsetX));
		_myFarBottomRight = CCVecMath.add(CCVecMath.subtract(myFarCenter, myY.clone().scale(_myFarHeight - myFarFrustumOffsetY)), myX.clone().scale(_myFarWidth + myFarFrustumOffsetX));

		_myFrustumPlanes[CCFrustumPlane.TOP.ordinal()] = new CCPlane3f(_myNearTopRight, _myNearTopLeft, _myFarTopLeft);
		_myFrustumPlanes[CCFrustumPlane.BOTTOM.ordinal()] = new CCPlane3f(_myNearBottomLeft, _myNearBottomRight, _myFarBottomRight);
		_myFrustumPlanes[CCFrustumPlane.LEFT.ordinal()] = new CCPlane3f(_myNearTopLeft, _myNearBottomLeft, _myFarBottomLeft);
		_myFrustumPlanes[CCFrustumPlane.RIGHT.ordinal()] = new CCPlane3f(_myNearBottomRight, _myNearTopRight, _myFarBottomRight);
		_myFrustumPlanes[CCFrustumPlane.NEARP.ordinal()] = new CCPlane3f(_myNearTopLeft, _myNearTopRight, _myNearBottomRight);
		_myFrustumPlanes[CCFrustumPlane.FARP.ordinal()] = new CCPlane3f(_myFarTopRight, _myFarTopLeft, _myFarBottomLeft);
	}

	public void setCamDef(final CCVector3f thePo, CCVector3f theTa, CCVector3f theU) {

		
	}

	public CCFrustumRelation isInFrustum(final CCVector3f thePoint) {
		CCFrustumRelation result = CCFrustumRelation.INSIDE;
		for (int i = 0; i < 6; i++) {
			if (_myFrustumPlanes[i].distance(thePoint) < 0)
				return CCFrustumRelation.OUTSIDE;
		}
		return (result);
	}

	public CCFrustumRelation isInFrustum(CCVector3f p, float raio) {
		CCFrustumRelation result = CCFrustumRelation.INSIDE;
		float distance;

		for (int i = 0; i < 6; i++) {
			distance = _myFrustumPlanes[i].distance(p);
			if (distance < -raio)
				return CCFrustumRelation.OUTSIDE;
			else if (distance < raio)
				result = CCFrustumRelation.INTERSECT;
		}
		return (result);
	}

	// public CCFrustumRelation boxInFrustum(final CCAABB theBoundingBox) {
	// CCFrustumRelation result = CCFrustumRelation.INSIDE;
	// for(int i=0; i < 6; i++) {
	// if (pl[i].distance(b.getVertexP(pl[i].normal())) < 0)
	// return CCFrustumRelation.OUTSIDE;
	// else if (pl[i].distance(b.getVertexN(pl[i].normal())) < 0)
	// result = CCFrustumRelation.INTERSECT;
	// }
	// return(result);
	// }

	public void drawPoints(CCGraphics g) {
		g.beginShape(CCDrawMode.POINTS);

		g.vertex(_myNearTopLeft);
		g.vertex(_myNearTopRight);
		g.vertex(_myNearBottomLeft);
		g.vertex(_myNearBottomRight);

		g.vertex(_myFarTopLeft);
		g.vertex(_myFarTopRight);
		g.vertex(_myFarBottomLeft);
		g.vertex(_myFarBottomRight);

		g.endShape();
	}

	public void drawLines(CCGraphics g) {
		g.beginShape(CCDrawMode.QUADS);

		// near plane
		g.vertex(_myNearTopLeft);
		g.vertex(_myNearTopRight);
		g.vertex(_myNearBottomRight);
		g.vertex(_myNearBottomLeft);

		// far plane
		g.vertex(_myFarTopRight);
		g.vertex(_myFarTopLeft);
		g.vertex(_myFarBottomLeft);
		g.vertex(_myFarBottomRight);

		// bottom plane
		g.vertex(_myNearBottomLeft);
		g.vertex(_myNearBottomRight);
		g.vertex(_myFarBottomRight);
		g.vertex(_myFarBottomLeft);

		// top plane
		g.vertex(_myNearTopRight);
		g.vertex(_myNearTopLeft);
		g.vertex(_myFarTopLeft);
		g.vertex(_myFarTopRight);

		// left plane
		g.vertex(_myNearTopLeft);
		g.vertex(_myNearBottomLeft);
		g.vertex(_myFarBottomLeft);
		g.vertex(_myFarTopLeft);

		// right plane
		g.vertex(_myNearBottomRight);
		g.vertex(_myNearTopRight);
		g.vertex(_myFarTopRight);
		g.vertex(_myFarBottomRight);

		g.endShape();
	}

	public void drawPlanes(CCGraphics g) {
		g.beginShape(CCDrawMode.QUADS);

		// near plane
		g.vertex(_myNearTopLeft);
		g.vertex(_myNearTopRight);
		g.vertex(_myNearBottomRight);
		g.vertex(_myNearBottomLeft);

		// far plane
		g.vertex(_myFarTopRight);
		g.vertex(_myFarTopLeft);
		g.vertex(_myFarBottomLeft);
		g.vertex(_myFarBottomRight);

		// bottom plane
		g.vertex(_myNearBottomLeft);
		g.vertex(_myNearBottomRight);
		g.vertex(_myFarBottomRight);
		g.vertex(_myFarBottomLeft);

		// top plane
		g.vertex(_myNearTopRight);
		g.vertex(_myNearTopLeft);
		g.vertex(_myFarTopLeft);
		g.vertex(_myFarTopRight);

		// left plane

		g.vertex(_myNearTopLeft);
		g.vertex(_myNearBottomLeft);
		g.vertex(_myFarBottomLeft);
		g.vertex(_myFarTopLeft);

		// right plane
		g.vertex(_myNearBottomRight);
		g.vertex(_myNearTopRight);
		g.vertex(_myFarTopRight);
		g.vertex(_myFarBottomRight);

		g.endShape();
	}

	public void drawNormals(CCGraphics g) {
		CCVector3f a, b;

		g.beginShape(CCDrawMode.LINES);

		// near
		a = CCVecMath.add(_myNearTopRight, _myNearTopLeft, _myNearBottomRight, _myNearBottomLeft).scale(0.25f);
		b = CCVecMath.add(a, _myFrustumPlanes[CCFrustumPlane.NEARP.ordinal()].normal().clone().scale(100));
		g.vertex(a);
		g.vertex(b);

		// far
		a = CCVecMath.add(_myFarTopRight, _myFarTopLeft, _myFarBottomRight, _myFarBottomLeft).scale(0.25f);
		b = CCVecMath.add(a, _myFrustumPlanes[CCFrustumPlane.FARP.ordinal()].normal().clone().scale(100));
		g.vertex(a);
		g.vertex(b);

		// left
		a = CCVecMath.add(_myFarTopLeft, _myFarBottomLeft, _myNearBottomLeft, _myNearTopLeft).scale(0.25f);
		b = CCVecMath.add(a, _myFrustumPlanes[CCFrustumPlane.LEFT.ordinal()].normal().clone().scale(100));
		g.vertex(a);
		g.vertex(b);

		// right
		a = CCVecMath.add(_myFarTopRight, _myNearBottomRight, _myFarBottomRight, _myNearTopRight).scale(0.25f);
		b = CCVecMath.add(a, _myFrustumPlanes[CCFrustumPlane.RIGHT.ordinal()].normal().clone().scale(100));
		g.vertex(a);
		g.vertex(b);

		// top
		a = CCVecMath.add(_myFarTopRight, _myFarTopLeft, _myNearTopRight, _myNearTopLeft).scale(0.25f);
		b = CCVecMath.add(a, _myFrustumPlanes[CCFrustumPlane.TOP.ordinal()].normal().clone().scale(100));
		g.vertex(a);
		g.vertex(b);

		// bottom
		a = CCVecMath.add(_myFarBottomRight, _myFarBottomLeft, _myNearBottomRight, _myNearBottomLeft).scale(0.25f);
		b = CCVecMath.add(a, _myFrustumPlanes[CCFrustumPlane.BOTTOM.ordinal()].normal().clone().scale(100));
		g.vertex(a);
		g.vertex(b);

		g.endShape();
	}
};
