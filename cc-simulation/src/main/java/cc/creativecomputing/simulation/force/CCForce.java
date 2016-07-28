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
package cc.creativecomputing.simulation.force;

import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.simulation.CCParticle;
import cc.creativecomputing.simulation.force.CCForce;


public abstract class CCForce{
	
	protected float weight = 1;
	
	protected static float sqrt2 = (float) Math.sqrt(2D);
	
	public CCForce(){
	}
	
	/**
	 * Implement this method to define a logic under which circumstances a behavior is active
	 * @return
	 */
	public boolean isActive(final CCParticle theVehicle){
		return true;
	}
	
	public float weight(){
		return weight;
	}
	
	/**
	 * Sets the weight this behavior is applied with
	 * can range from 0 to 1 exceeding values are truncated
	 * @param theWeight
	 */
	public void weight(final float theWeight){
		weight = Math.max(theWeight,0);
	}

	
	/**
	 * Applies the behavior to the given force 
	 * @param theForce CCVector3f, vector the behavior is applied to
	 * @param theDeltaTime TODO
	 * @param i_entity CCAgent, the agent the behavior is applied to
	 */
	public abstract boolean apply(final CCParticle theParticle, final CCVector3f theForce, float theDeltaTime);
}
