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

/**
 * The arrive behavior is similar to the seek. It decelerates the agent, as it
 * comes nearer to the target position.
 * @author christianr
 *
 */
public class CCArrive extends CCTargetBehavior{
	private float _mySlowingDistance;
	
	public CCArrive(final CCVector3f theTarget, final float theSlowingDistance){
		super(theTarget);
		_mySlowingDistance = theSlowingDistance;
	}
	
	public CCArrive(final CCVector3f theTarget){
		this(theTarget,300);
	}
	
	public void slowingDistance(final float theSlowingDistance){
		_mySlowingDistance = theSlowingDistance;
	}

	public boolean apply(final CCParticle theAgent, final CCVector3f theForce, float theDeltaTime) {
		theForce.set(_myTarget);
		theForce.subtract(theAgent.position);

		final float distance = theForce.length();
		
		if(distance == 0.01f)return false;
		
		final float rampedSpeed = theAgent.maxSpeed * (distance / _mySlowingDistance);
		final float clippedSpeed = Math.min(rampedSpeed, theAgent.maxSpeed);
		
		theForce.scale(clippedSpeed / distance);
		theForce.subtract(theAgent.velocity());
		theForce.truncate(theAgent.maxForce);
		return true;
	}

}
