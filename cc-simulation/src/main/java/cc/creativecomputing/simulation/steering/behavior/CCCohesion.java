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


public class CCCohesion extends CCNeighborHoodBehavior{
	
	private final CCSeekFlee _mySeek = new CCSeekFlee(new CCVector3f());

	public CCCohesion(final float theDistance, final float theAngle){
		super(theDistance, theAngle);
	}
	
	@Override
	public boolean apply(final CCParticle theAgent, final CCVector3f theForce, float theDeltaTime){
		boolean myDoApply = super.apply(theAgent,theForce, theDeltaTime);
		
		if(myDoApply){
			_mySeek.target(theForce);
			_mySeek.apply(theAgent, theForce, 0);
		}
		
		return myDoApply;
	}
}
