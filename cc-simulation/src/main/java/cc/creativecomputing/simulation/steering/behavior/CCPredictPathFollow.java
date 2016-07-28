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

import java.util.List;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.simulation.CCParticle;
import cc.creativecomputing.simulation.force.CCForce;

public class CCPredictPathFollow extends CCForce{
	private List<CCVector3f> _myPathway;
	private float _myPredictRange;
	private CCVector3f _myTarget;
	private int _myIndex = 0;
	private final CCArrive _mySeek;
	
	public CCPredictPathFollow(final List<CCVector3f> thePathway, final float thePredictRange){
		_myPathway = thePathway;
		
		_myPredictRange = thePredictRange;
		_myIndex = (int)CCMath.random(_myPathway.size());
		_myTarget = _myPathway.get(_myIndex).clone();
		_mySeek = new CCArrive(_myTarget);
	}
	
	public void pathWay(final List<CCVector3f> thePathway){
		_myPathway = thePathway;
		_myIndex = (int)CCMath.random(_myPathway.size());
		_myTarget = _myPathway.get(_myIndex).clone();
	    _mySeek.target(_myTarget);
	}

	public boolean apply(final CCParticle theAgent, final CCVector3f theForce, float theDeltaTime) {
		float dist = CCMath.abs(theAgent.position.x - _myTarget.x);
		if(dist < _myPredictRange){
			_myTarget = _myPathway.get(_myIndex++).clone();
			_myIndex %= _myPathway.size();
		}
	    _mySeek.target(_myTarget);
	    _mySeek.apply(theAgent, theForce, 0);
	    return true;
	    
	}

}
