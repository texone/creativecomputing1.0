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
import cc.creativecomputing.simulation.force.CCForce;

public class CCSteerToRandom extends CCForce {
	
	private float _myRange;
	private float _mySwitchTimer = 0;
	private float _mySwitchTime = 0;
	
	private CCVector3f leaderTarget = new CCVector3f();
	private CCVector3f targetOffset;
	private float _myMinDistance = 1;
	private CCSeekFlee _mySeek;
	
	public CCSteerToRandom(){
		this(50,3);
	}
	
	public CCSteerToRandom(final float theRange, final float theSwitchTime){
		_myRange = theRange;
		_mySeek = new CCSeekFlee(leaderTarget);
		_mySwitchTime = theSwitchTime;
		_mySwitchTimer = theSwitchTime;
	}
	
	public float range(){
		return _myRange;
	}
	
	public void range(final float theRange){
		_myRange = theRange;
	}
	
	public float switchTime(){
		return _mySwitchTime;
	}
	
	public void switchTime(final float theSwitchTime){
		_mySwitchTime = theSwitchTime;
	}

	public boolean apply(CCParticle theAgent, CCVector3f theForce, float theDeltaTime) {
		if (_mySwitchTimer >= _mySwitchTime || leaderTarget.distance(theAgent.position) < _myMinDistance * 3F) {
			do {
				leaderTarget.randomize();
				leaderTarget.scale(_myRange);
				leaderTarget.add(theAgent.position);
				
				targetOffset = leaderTarget.clone();
				targetOffset.subtract(theAgent.position);
				targetOffset.normalize();
			} while ( targetOffset.dot(theAgent.forward) < -0.80000000000000004f);
			
			_mySwitchTimer = 0;
			_mySeek.target(leaderTarget);
		}
		_mySwitchTimer += theDeltaTime;
		
		_mySeek.apply(theAgent, theForce, theDeltaTime);
		return true;
	}

}
