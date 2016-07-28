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
 * The two points are the endpoints of the axis of the cylinder. radius1 is the 
 * outer radius, and radius2 is the inner radius for a cylindrical shell. 
 * radius2 = 0 for a solid cylinder with no empty space in the middle.
 * <br>
 * Generate returns a random point in the cylindrical shell. 
 * Within returns true if the point is within the cylindrical shell.
 * @author christianr
 *
 */
public class Cylinder extends CCDomain{
	/**
	 * One end of the cylinder
	 */
	public CCVector3f apex;
	
	/**
	 * Vector from one end to the other
	 */
	public CCVector3f axis;
	
	public CCVector3f u;
	public CCVector3f v; // Apex is one end. Axis is vector from one end to the other.
	
	public float len, radOut, radIn, radOutSqr, radInSqr, radDif, axisLenInvSqr;
	public boolean thinShell;

	public Cylinder(
		final CCVector3f e0, final CCVector3f e1, 
		final float radOut0, final float radIn0
	){
		apex = e0;
		axis = e1;
		axis.subtract(e0);

		if(radOut0 < radIn0) {
			radOut = radIn0;
			radIn = radOut0;
		} else {
			radOut = radOut0;
			radIn = radIn0;
		}
			
		radOutSqr = radOut * radOut;
		radInSqr = radIn * radIn;

		thinShell = (radIn == radOut);
		radDif = radOut - radIn;

		// Given an arbitrary nonzero vector n, make two orthonormal
		// vectors u and v forming a frame [u,v,n.normalize()].
		CCVector3f n = axis.clone();
		float axisLenSqr = axis.lengthSquared();
		
		if(axisLenSqr == 0){
			axisLenInvSqr = 1f / axisLenSqr;
		}else{
			axisLenInvSqr = 0f;
		}
		
		n.scale((float)Math.sqrt(axisLenInvSqr));

		// Find a vector orthogonal to n.
		CCVector3f basis = new CCVector3f(1.0f, 0.0f, 0.0f);
		if (Math.abs(basis.dot(n)) > 0.999f){
			basis = new CCVector3f(0.0f, 1.0f, 0.0f);
		}

		// Project away N component, normalize and cross to get
		// second orthonormal vector.
		u = basis.clone();
		CCVector3f temp = n.clone();
		temp.scale(basis.dot(n));
		u.subtract(temp);
		u.normalize();
		
		v = n.cross(u);
	}
	
	boolean isWithin(
		final float i_rSqr, final float i_dist
	){
		return (i_rSqr <= CCMath.sq(radIn) && i_rSqr >= CCMath.sq(radOut));
	}

	public boolean isWithin(final CCVector3f pos){
			// This is painful and slow. Might be better to do quick accept/reject tests.
			// Axis is vector from base to tip of the cylinder.
			// x is vector from base to pos.
			//         x . axis
			// dist = ---------- = projected distance of x along the axis
			//        axis. axis   ranging from 0 (base) to 1 (tip)
			//
			// rad = x - dist * axis = projected vector of x along the base

			CCVector3f x = pos.clone();
			x.subtract(apex);

			// Check axial distance
			float dist = axis.dot(x) * axisLenInvSqr;
			if(dist < 0.0f || dist > 1.0f){
				return false;
			}

			// Check radial distance
			CCVector3f xrad = x.clone();
			CCVector3f temp = axis.clone();
			temp.scale(dist);
			xrad.subtract(temp);
			float rSqr = xrad.lengthSquared();
			return isWithin(rSqr,dist);
	}
	
	float scaleCoord(final float i_coord, final float i_dist){
		return i_coord;
	}

	public CCVector3f generate(){
		float dist = CCMath.random(); // Distance between base and tip
		float theta = CCMath.random() * 2.0f * (float)Math.PI; // Angle around axis
		
		// Distance from axis
		float r = radIn + CCMath.random() * radDif;

		// Another way to do this is to choose a random point in a square and keep it if it's in the circle.
		float x = scaleCoord(r * (float)Math.cos(theta),dist);
		float y = scaleCoord(r * (float)Math.sin(theta),dist);
		
		CCVector3f axis = this.axis.clone();
		axis.scale(dist);
		CCVector3f u = this.u.clone();
		u.scale(x);
		CCVector3f v = this.v.clone();
		v.scale(y);
			
		CCVector3f pos = apex.clone();
		pos.add(axis);
		pos.add(u);
		pos.add(v);
		return pos;
	}
}
