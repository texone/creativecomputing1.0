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
 * The point x, y, z is the center of a disc in the plane with normal nx, ny, nz. 
 * The disc has outer radius radius1 and inner radius radius2. The normal will 
 * get normalized, so it need not already be normalized.
 * Generate returns a point inside the disc. 
 * Within returns false.
 * @author christianr
 *
 */
public class Disc extends CCDomain{
	
	public CCVector3f center;
	public CCVector3f normal;
	public CCVector3f u;
	public CCVector3f v;
	
	public float radIn;
	public float radOut;
	
	public float radInSqr; 
	public float radOutSqr;
	public float dif;
	public float d;

	public Disc(
		final CCVector3f i_center, final CCVector3f i_normal, 
		final float i_outerRadius, final float i_innerRadius
	){
		center = i_center;
		normal = i_normal;
		normal.normalize();

		if(i_outerRadius > i_innerRadius) {
			radOut = i_outerRadius; radIn = i_innerRadius;
		} else {
			radOut = i_innerRadius; radIn = i_outerRadius;
		}
		dif = radOut - radIn;
		radInSqr = CCMath.sq(radIn);
		radOutSqr = CCMath.sq(radOut);

		//Find a vector orthogonal to n.
		CCVector3f basis = new CCVector3f(1.0f, 0.0f, 0.0f);
		if (Math.abs(basis.dot(normal)) > 0.999f){
			basis = new CCVector3f(0.0f, 1.0f, 0.0f);
		}
			
		// Project away N component, normalize and cross to get
		// second orthonormal vector.
		u = basis.clone();
		CCVector3f temp = normal.clone();
		temp.scale(basis.dot(normal));
		u.subtract(temp);
		u.normalize();
		
		v = normal.cross(u);
			
		d = -(center.dot(normal));
	}
	
	public Disc(
		final CCVector3f i_center, final CCVector3f i_normal, 
		final float i_radius
	){
		this(i_center, i_normal,i_radius,0);
	}

	public CCVector3f generate(){
		// Might be faster to generate a point in a square and reject if outside the circle
		float theta = CCMath.random() * 2.0f * (float) Math.PI; // Angle around normal
		// Distance from center
		float r = radIn + CCMath.random() * dif;

		float x = r * (float) Math.cos(theta); // Weighting of each frame vector
		float y = r * (float) Math.sin(theta);

		CCVector3f u = this.u.clone();
		u.scale(x);
		CCVector3f v = this.v.clone();
		v.scale(y);

		CCVector3f result = center.clone();
		result.add(u);
		result.add(v);
		return result;
	}

}
