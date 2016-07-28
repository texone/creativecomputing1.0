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
 * The first point is the apex of the cone and the second is the other endpoint
 *  of the axis of the cone. radius1 is the radius of the base of the cone. 
 *  radius2 is the radius of the base of a cone to subtract from the first 
 *  cone to create a conical shell. This is similar to the cylindrical shell, 
 *  which can be thought of as a large cylinder with a smaller cylinder subtracted 
 *  from the middle. 
 *  <br>
 *  Both cones share the same apex and axis, which implies that the thickness of 
 *  the conical shell tapers to 0 at the apex. radius2 = 0 for a solid cone 
 *  with no empty space in the middle.
 *  <br>
 *  Generate returns a random point in the conical shell. 
 *  Within returns true if the point is within the conical shell.
 * @author christianr
 *
 */
public class Cone extends Cylinder{

	public Cone(
		final CCVector3f i_e0, final CCVector3f i_e1, 
		final float i_radOut, final float i_radIn
	){
		super(i_e0,i_e1,i_radOut,i_radIn);
	}

	boolean isWithin(
		final float i_rSqr, final float i_dist
	){
		return (i_rSqr <= CCMath.sq(i_dist * radIn)&& i_rSqr >= CCMath.sq(i_dist * radOut));
	}
	
	float scaleCoord(final float i_coord, final float i_dist){
		return i_coord * i_dist;
	}
}
