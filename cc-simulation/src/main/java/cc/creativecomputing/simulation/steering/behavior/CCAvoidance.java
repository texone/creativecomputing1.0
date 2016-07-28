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
import cc.creativecomputing.simulation.domain.CCDomain;
import cc.creativecomputing.simulation.force.CCDomainForce;

/**
 * Steer agents away from a domain of space.
 * <p>
 * Agents are tested to see whether they will pass from being 
 * outside the specified domain to being inside it within look_ahead 
 * time units from now if the next Move() action were to occur now. 
 * The specific direction and amount of turn is dependent on the kind 
 * of domain being avoided.
 * </p>
 * <p>
 * At present the only domains for which Avoid() is implemented are 
 * CCSphere, CCRectangle, CCTriangle, CCDisc and CCPlane.
 * </p>
       
 * @author info
 *
 */
public class CCAvoidance extends CCDomainForce{
	
	private float _myLookAhead;	// how many time units ahead to look
    private float _myEpsilon;		// add to r^2 for softening
	
    /**
     * Creates a new avoidance,
     * @param theLookAhead 
     * 		how far forward along the velocity vector to look for the obstacle
     * @param theEpsilon 
     * 		The amount of acceleration falls off inversely with the squared 
     * 		distance to the edge of the domain. But when that distance is small, 
     * 		the acceleration would be infinite, so epsilon is always added to the 
     * 		distance.
     */
	public CCAvoidance(final float theLookAhead, final float theEpsilon){
		_myLookAhead = theLookAhead;
		_myEpsilon = theEpsilon;
	}
	
	public float lookAhead(){
		return _myLookAhead;
	}
	
	public void lookAhead(final float theLookAhead){
		_myLookAhead = theLookAhead;
	}
	
	public float epsilon(){
		return _myEpsilon;
	}
	
	public void epsilon(final float theEpsilon){
		_myEpsilon = theEpsilon;
	}

	public boolean apply(CCParticle theParticle, CCVector3f theForce, float theDeltaTime) {
		for(CCDomain myDomain:_myDomains){
			myDomain.avoidance(theParticle, theForce, _myLookAhead, _myEpsilon);
		}
		return false;
	}

}
