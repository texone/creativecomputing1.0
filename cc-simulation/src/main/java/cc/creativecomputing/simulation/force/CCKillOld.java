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

/**
 * Removes all particles older than the set maximum life time. 
 * @author info
 */
public class CCKillOld extends CCForce{
	
	private float _myMaxLifeTime;
	
	public CCKillOld(final float theMaxLifeTime){
		_myMaxLifeTime = theMaxLifeTime;
	}
	
	public float maximumLifeTime(){
		return _myMaxLifeTime;
	}
	
	public void maximumLifeTime(final float theMaxLifeTime){
		_myMaxLifeTime = theMaxLifeTime;
	}

	public boolean apply(CCParticle theParticle, CCVector3f theForce, float theDeltaTime) {
		theParticle.isKilled(theParticle.age() > _myMaxLifeTime);
		return false;
	}

}
