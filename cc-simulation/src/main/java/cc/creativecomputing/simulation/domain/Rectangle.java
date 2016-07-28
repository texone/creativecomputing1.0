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
 * The point o is a point on the plane. u and v are (non-parallel) 
 * basis vectors in the plane. They don't need to be normal or orthogonal.
 * <br>
 * Generate returns a random point in the diamond-shaped patch whose 
 * corners are o, o+u, o+u+v, and o+v. 
 * Within returns true if the point is in the positive half-space of 
 * the plane (in the plane or on the side that the normal (u cross v) points to).
 * @author christianr
 *
 */
public class Rectangle extends CCTriangle{

	public Rectangle(final CCVector3f i_p0, final CCVector3f i_p1, final CCVector3f i_p2){
		super(i_p0, i_p1, i_p2);
	}

	CCVector3f calculateNonBasicEdge(){
		final CCVector3f f = v.clone();
	   f.add(u);
	   return f;
	}

	public CCVector3f generate(){
		final CCVector3f p = this._myPoint.clone();
		
		final CCVector3f u = this.u.clone();
		u.scale(CCMath.random());
		p.add(u);
		
		final CCVector3f v = this.v.clone();
		v.scale(CCMath.random());
		p.add(v);
		
		return p;
	}

	public CCVector3f nearestEdge(final CCVector3f i_vector){
		final CCVector3f uofs = uNorm.clone();
      uofs.scale(uNorm.dot(i_vector));
      uofs.subtract(i_vector);
      final float udistSqr = uofs.lengthSquared();
      
      final CCVector3f vofs = vNorm.clone();
      vofs.scale(vNorm.dot(i_vector));
      vofs.subtract(i_vector);
      final float vdistSqr = vofs.lengthSquared();

      final CCVector3f foffset = u.clone();
      foffset.add(v);
      foffset.subtract(i_vector);
      
      final CCVector3f fofs = uNorm.clone();
      fofs.scale(uNorm.dot(foffset));
      fofs.subtract(foffset);
      float fdistSqr = fofs.lengthSquared();
      
      final CCVector3f gofs = vNorm.clone();
      gofs.scale(vNorm.dot(foffset));
      gofs.subtract(foffset);
      float gdistSqr = gofs.lengthSquared();

      // S is the safety vector toward the closest point on boundary.
      final CCVector3f result;
      if(udistSqr <= vdistSqr && udistSqr <= fdistSqr && udistSqr <= gdistSqr) result = uofs;
      else if(vdistSqr <= fdistSqr && vdistSqr <= gdistSqr) result = vofs;
      else if(fdistSqr <= gdistSqr) result = fofs;
      else result = gofs;
      return result;
	}
}
