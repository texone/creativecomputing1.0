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
package cc.creativecomputing.simulation.steering.behavior;

import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.simulation.CCParticle;

public class CCRotateAroundTarget extends CCTargetBehavior{
	
	private float _myRadius;

	public CCRotateAroundTarget(CCVector3f theTarget, final float theRadius) {
		super(theTarget);
		_myRadius = theRadius;
	}

	public boolean apply(CCParticle theAgent, CCVector3f theForce, float theDeltaTime) {
		CCVector3f dist = theAgent.position.clone();
		dist.subtract(_myTarget);
		
		CCVector3f myForce = dist.clone();
		myForce.approximateNormalize();
		myForce = myForce.cross(new CCVector3f(0,1,0));
		theForce.set(theAgent.forward);
		theForce.scale(20);
		float distance = _myRadius - dist.approximateLength();
		
		dist.normalize();
		dist.scale(distance);
		
		theForce.add(dist);
		
		return true;
	}

}
