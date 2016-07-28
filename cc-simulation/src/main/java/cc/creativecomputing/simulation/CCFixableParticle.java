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
package cc.creativecomputing.simulation;


import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;

public class CCFixableParticle extends CCParticle{

	public boolean fixed = false;

	public CCFixableParticle(){
		super();
	}

	public void update(float dt){

		if (!fixed)
			super.update(dt);
	}
	
	public void addDampingForce(float factor){
		allForces.subtract( CCVecMath.multiply(_myVelocity,	 factor) );
	}

	public void addRepulsionForce(CCVector3f where, float radius, float scaleX, float scaleY){
	    				
		CCVector3f diff	= CCVecMath.subtract(position , where);
		float length	= diff.length();
		
		boolean closeEnough = true;
	    if (radius > 0){
	        if (length > radius){
	            closeEnough = false;
	        }
	    }
		
		if (closeEnough == true){
			float pct = 1 - (length / radius);  // stronger on the inside
	        diff.normalize();
			allForces.x += diff.x * scaleX * pct;
			allForces.y += diff.y * scaleY * pct;
	    }
	}
}
