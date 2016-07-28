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
import cc.creativecomputing.simulation.steering.CCAgent;



/**
*   class CCSeparation
*
*   Implements the CCFlocking behavior
*/
public class CCFlocking extends CCNeighborHoodBehavior{
	
	private final CCFlee _myFlee = new CCFlee(new CCVector3f());
	private final CCSeekFlee _mySeek = new CCSeekFlee(new CCVector3f());
	
	/** 
	* Constructor 
	*
	* @param theDistance Radius of the area to be searched for relevant vehicles
	* @param theAngle Influence of the behavior
	*/
	public CCFlocking(final float theDistance, final float theAngle){
		super(theDistance, theAngle);
		_myFlee.weight(1f);
		_mySeek.weight(1f);
	}
	
	@Override
	/**
	 * Sets the radius of the area to be searched for relevant vehicles
	 * @param nearAreaRadius New area radius
	 */
	public void nearAreaRadius(final float theNearAreaRadius) {
		super.nearAreaRadius(theNearAreaRadius);
	}
	
	/** 
	 * Calculates the resulting force vector for this frame
	 * @param veh The vehicle
	 * @return Returns the resulting force
	 */
	public boolean apply(final CCParticle theAgent, final CCVector3f theForce, float theDeltaTime){
		int myCount = 0;
		theForce.set(0,0,0);
		
		neighbors.clear();
		
		CCVector3f myTarget = new CCVector3f();
		
		for (CCAgent myOtherAgent:_myNeighborhood.getNearAgents(theAgent, _myNearAreaRadius/2)){
			//if (isInAngle(theAgent,myOtherAgent)){
				myCount++;
				myTarget.add(myOtherAgent.position);
				neighbors.add(myOtherAgent);
			//}
		}
		
		if(myCount  <= 0)return false;
		
		myTarget.scale(1.0F / myCount);
		
		final CCVector3f myTempForce = new CCVector3f();
		
		/* apply separation */
		_myFlee.target(myTarget);
		_myFlee.apply(theAgent, myTempForce, 0);
		myTempForce.normalize();
		myTempForce.scale(_myFlee.weight());
		theForce.add(myTempForce);
		
		myTarget = new CCVector3f();
		
		for (CCAgent myOtherAgent:_myNeighborhood.getNearAgents(theAgent, _myNearAreaRadius)){
			//if (isInAngle(theAgent,myOtherAgent)){
				myCount++;
				myTarget.add(myOtherAgent.position);
				neighbors.add(myOtherAgent);
			//}
		}
		
		if(myCount  <= 0)return false;
		
		myTarget.scale(1.0F / myCount);
		
		/* apply cohesion */
		_mySeek.target(myTarget);
		_mySeek.apply(theAgent, myTempForce, 0);
		myTempForce.normalize();
		myTempForce.scale(_mySeek.weight());
		theForce.add(myTempForce);
		
		/* apply alignment */
		myTempForce.set(0,0,0);
		for(CCAgent myOtherAgent:neighbors){
			myTempForce.add(myOtherAgent.forward);
		}
		
		myTempForce.scale(3.0F / myCount);
		myTempForce.subtract(theAgent.forward);
		
		theForce.add(myTempForce);
		
		return true;
	}


}
