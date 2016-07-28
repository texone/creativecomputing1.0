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

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.simulation.CCParticle;
import cc.creativecomputing.simulation.force.CCForce;

/**
 * Wander is a useful steering force to apply a random walk to an agent.
 * A simple solution would be to calculate a random steering force for
 * each time step, resulting in an uninteresting and jittery movement 
 * instead of sustained turns.
 * <br>
 * Burt Reynold's approach is to steer toward a target that is constrained
 * to move on the perimeter of a circle projected in the front of the agent.
 * This way the direction of the agent is only slightly modified between
 * each time step, creating a jitter free smooth motion.
 * <br>
 * "The steering force takes a random walk from one direction to another. 
 * This idea can be implemented several ways, but one that has produced good 
 * results is to constrain the steering force to the surface of a sphere 
 * located slightly ahead of the character. To produce the steering force for 
 * the next frame: a random displacement is added to the previous value, 
 * and the sum is constrained again to the sphere's surface."
 * <br>
 * The sphere's radius determines the maximum wandering strength and the 
 * magnitude of the random displacement determines the wander rate.
 */
public class CCWander extends CCForce{

	/**
	 * The radius of the sphere projected before the agent, determining 
	 * the maximum wander strength
	 */
	private float _myWanderStrength;

	/**
	 * The maximum random displacement added to the direction
	 */
	private float _myWanderRate;

	/**
	 * direction of the wander that is displaced on every time step
	 */
	private final CCVector3f _myWanderDirection;
	
	/**
	 * Initializes a new CCWanderer using the giving strength and rate.
	 * @param i_wanderStrength, int or float: The radius of the sphere projected before the agent, 
	 * 			determining the maximum wander strength
	 * @param i_wanderRate, int or float: The maximum random displacement added to the direction
	 */
	public CCWander(
		final float theWanderStrength,
		final float theWanderRate
	){
		_myWanderStrength = theWanderStrength;
		_myWanderRate = theWanderRate;
		_myWanderDirection = new CCVector3f();
	}
	
	/**
	 * Initializes a new CCWanderer using default values.
	 *
	 */
	public CCWander(){
		this(1f,0.6f);
	}
	
	/**
	 * Applies the behavior to its agent
	 */
	public synchronized boolean apply(final CCParticle theAgent,final CCVector3f theForce, float theDeltaTime){
		float x = (CCMath.random() * 2 - 1) * _myWanderRate;
		float y = (CCMath.random() * 2 - 1) * _myWanderRate;
		float z = (CCMath.random() * 2 - 1) * _myWanderRate;

		_myWanderDirection.add(x,y,z);
		//_myWanderDirection().x(0.0F);
		_myWanderDirection.approximateNormalize();

		CCVector3f wanderGlobal = theAgent.globalizeDirection(wanderDirection());

		theForce.set(
			theAgent.forward.x * sqrt2 + wanderGlobal.x * _myWanderStrength,
			theAgent.forward.y * sqrt2 + wanderGlobal.y * _myWanderStrength,
			theAgent.forward.z * sqrt2 + wanderGlobal.z * _myWanderStrength
		);
		return true;
	}

	/**
	 * Sets the maximum strength of the wander.
	 * @param theWanderStrength The _myWanderStrength to set.
	 */
	public void wanderStrength(final float theWanderStrength){
		_myWanderStrength = theWanderStrength;
	}

	/**
	 * Returns the maximum strength of the wanderer.
	 * @return Returns the _myWanderStrength.
	 */
	public float wanderStrength(){
		return _myWanderStrength;
	}

	/**
	 * Sets the maximum displacement added each time step to 
	 * the direction of the wanderer.
	 * @param theWanderRate The _myWanderRate to set.
	 */
	public void wanderRate(final float theWanderRate){
		this._myWanderRate = theWanderRate;
	}

	/**
	 * Returns the maximum displacement added each time step to 
	 * the direction of the wanderer.
	 * @return Returns the _myWanderRate.
	 */
	public float wanderRate(){
		return _myWanderRate;
	}

	/**
	 * Returns the current direction of the wanderer
	 * @return CCVector3f: the current direction of the wanderer
	 */
	public CCVector3f wanderDirection(){
		return _myWanderDirection;
	}

}
