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

public class CCLeaderFollow extends CCSeparation{
	
	private CCAgent _myLeader;
	private CCVector3f _myTarget = new CCVector3f();
	private float leaderAvoidWidth = 4F;
    private float leaderAvoidLength = 30F;
    private float _myFollowDistance = 5;
    
    private CCSeekFlee _mySeek;
    private CCArrive _myArrive;
	
	public CCLeaderFollow(final CCAgent theLeader){
		super(50,360);
		_myLeader = theLeader;
		leaderAvoidWidth = 4F * _myLeader.radius;
	    leaderAvoidLength = 30F * _myLeader.radius;
	    
	    _myTarget = new CCVector3f();
	    _mySeek = new CCSeekFlee(_myTarget);
	    _myArrive = new CCArrive(_myTarget);
	}
	
	public CCLeaderFollow(
		final CCAgent theLeader, 
		final float theFollowDistance, 
		final float theLeaderAvoidWidth, 
		final float theLeaderAvoidLength
	){
		super(50,360);
		_myLeader = theLeader;
		_myFollowDistance = theFollowDistance;
		leaderAvoidWidth = theLeaderAvoidWidth * _myLeader.radius;
	    leaderAvoidLength = theLeaderAvoidLength * _myLeader.radius;
	    
	    _myTarget = new CCVector3f();
	    _mySeek = new CCSeekFlee(_myTarget);
	    _myArrive = new CCArrive(_myTarget);
	}

	public float followDistance(){
		return _myFollowDistance;
	}
	
	public void followDistance(final float theFollowDistance){
		_myFollowDistance = theFollowDistance;
	}
	
	@Override
	public boolean apply(CCParticle theAgent, CCVector3f theForce, float theDeltaTime) {
		CCVector3f local = _myLeader.localizePosition(theAgent.position);
	    float adjust = _myLeader.relativeSpeed();
	    float margin = 3F * _myLeader.radius;
	    
	    CCVector3f myForce1 = new CCVector3f();
	    CCVector3f myForce2 = new CCVector3f();
	    
	    // check if the agent is in the avoidance range of the leader
	    if(
	    	local.z > 0.0F && local.z < leaderAvoidLength * adjust + margin * 2.0F && 
	    	local.x < leaderAvoidWidth * adjust + margin && local.x > -leaderAvoidWidth * adjust - margin && 
	    	local.y < leaderAvoidWidth * adjust + margin && local.y > -leaderAvoidWidth * adjust - margin
	    ){
	    	if(local.x <= 0)local.x -= 1;
	    	else local.x += 1;
	    	local.x *= 3;
	    	
	    	if(local.y <= 0)local.y -= 1;
	    	else local.y += 1;
	    	local.y *= 3;
	    	
	    	local.z *= 0.5f;
	    	
	    	_myTarget = _myLeader.globalizePosition(local);
	    	_mySeek.target(_myTarget);
	    	_mySeek.apply(theAgent, myForce1, 0);
	     } else {
			float followDistance = _myLeader.radius * -_myFollowDistance;
			_myTarget.set(_myLeader.forward);
			_myTarget.scale(followDistance);
			_myTarget.add(_myLeader.position);
			_myArrive.target(_myTarget);
			_myArrive.apply(theAgent, myForce1, 0);
		}
	    super.apply(theAgent, myForce2, theDeltaTime);
	    //myForce2.scale(0.2f);
	    
	    theForce.add(myForce1);
	    theForce.add(myForce2);
	    return true;
	}

}
