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
package cc.creativecomputing.simulation.domain;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3f;

/**
 * These are the vertices of a triangle. The triangle can be used to define 
 * an arbitrary geometric model for particles to bounce off, or generate 
 * particles on its surface (and explode them), etc.
 * <br>
 * Generate returns a random point in the triangle. Within always returns 
 * false. [This must eventually change so we can sink particles that
 *  enter/exit a model. Suggestions?]
 * @author christianr
 *
 */
public class CCTriangle extends CCPlaneDomain {

	public CCVector3f u;
	public CCVector3f v;
	// f is the third (non-basis) triangle edge.
	final CCVector3f f;

	public CCVector3f uNorm;
	public CCVector3f vNorm;
	final CCVector3f fNorm;

	//needed for avoid and bounce
	public float uLen;
	public float vLen;

	public CCTriangle(
		final CCVector3f i_p0, 
		final CCVector3f i_p1,
		final CCVector3f i_p2
	){
		super(i_p0);

		u = i_p1;
		u.subtract(_myPoint);

		v = i_p2;
		v.subtract(_myPoint);

		f = calculateNonBasicEdge();

		uLen = u.length();
		uNorm = u.clone();
		uNorm.scale(1 / uLen);

		fNorm = f.clone();
		fNorm.normalize();

		vLen = v.length();
		vNorm = v.clone();
		vNorm.scale(1 / vLen);

		_myNormal = uNorm.cross(vNorm);
		_myNormal.normalize();

		d = -_myPoint.dot(_myNormal);
	}

	CCVector3f calculateNonBasicEdge() {
		final CCVector3f f = v.clone();
		f.subtract(u);
		return f;
	}

	public CCVector3f generate() {
		final float r1 = CCMath.random();
		final float r2 = CCMath.random();

		final CCVector3f p = this._myPoint.clone();
		final CCVector3f u = this.u.clone();
		final CCVector3f v = this.v.clone();

		if (r1 + r2 < 1.0f) {
			u.scale(r1);
			v.scale(r2);
		} else {
			u.scale(1f - r1);
			v.scale(1f - r2);
		}

		p.add(u);
		p.add(v);

		return p;
	}

	public CCVector3f nearestEdge(final CCVector3f i_vector) {
		final CCVector3f uofs = uNorm.clone();
		uofs.scale(uNorm.dot(i_vector));
		uofs.subtract(i_vector);
		final float udistSqr = uofs.lengthSquared();

		final CCVector3f vofs = vNorm.clone();
		vofs.scale(vNorm.dot(i_vector));
		vofs.subtract(i_vector);
		final float vdistSqr = vofs.lengthSquared();

		final CCVector3f foffset = i_vector.clone();
		foffset.subtract(u);

		final CCVector3f fofs = fNorm.clone();
		fofs.scale(fNorm.dot(foffset));
		fofs.subtract(foffset);
		float fdistSqr = fofs.lengthSquared();

		// S is the safety vector toward the closest point on boundary.
		final CCVector3f result;
		if (udistSqr <= vdistSqr & udistSqr <= fdistSqr)
			result = uofs;
		else if (vdistSqr <= fdistSqr)
			result = vofs;
		else
			result = fofs;
		return result;
	}
}
