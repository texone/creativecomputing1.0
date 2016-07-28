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
import cc.creativecomputing.simulation.steering.CCAgent;

public class CCSpring extends CCForce{
	private float _mySpringConstant;

	private float _myDamping;

	private float _myRestLength;

	private CCAgent _myTarget;

	boolean on;

	public CCSpring(
		final CCAgent theTarget, 
		final float theSpringConstant, 
		final float theDamping, 
		final float theRestLength
	){
		_mySpringConstant = theSpringConstant;
		_myDamping = theDamping;
		_myRestLength = theRestLength;
		_myTarget = theTarget;
		on = true;
	}

	public final CCAgent target(){
		return _myTarget;
	}

	public final float currentLength(final CCAgent theAgent){
		return theAgent.position.distance(_myTarget.position);
	}

	public final float restLength(){
		return _myRestLength;
	}

	public final float strength(){
		return _mySpringConstant;
	}

	public final void setStrength(float ks){
		_mySpringConstant = ks;
	}

	public final float damping(){
		return _myDamping;
	}

	public final void setDamping(float d){
		_myDamping = d;
	}

	public final void restLength(
		final float i_restLength
	){
		_myRestLength = i_restLength;
	}

	public boolean apply(final CCParticle theAgent, final CCVector3f theForce, float theDeltaTime) {
		if (on){
			theForce.set(theAgent.position);
			theForce.subtract(_myTarget.position);

			float a2bDistance = theForce.length();
			float springForce = -(a2bDistance - _myRestLength) * _mySpringConstant;
			
			theForce.normalize();
			
			CCVector3f velocityA2B = theAgent.velocity().clone();
			velocityA2B.subtract(_myTarget.velocity());
			
			float dampingForce = -_myDamping * (theForce.dot(velocityA2B));
			float r = springForce + dampingForce;
			
			theForce.scale(r);
		}
		return true;
	}
}
