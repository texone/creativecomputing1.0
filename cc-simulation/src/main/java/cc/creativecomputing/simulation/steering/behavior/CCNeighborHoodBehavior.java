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

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.simulation.CCParticle;
import cc.creativecomputing.simulation.force.CCForce;
import cc.creativecomputing.simulation.steering.CCAgent;
import cc.creativecomputing.simulation.steering.CCNeighborhood;



public abstract class CCNeighborHoodBehavior extends CCForce{
	
	protected CCNeighborhood<?> _myNeighborhood;
	
	/**
	 * Distance in which neighbors have to be to be included into calculations
	 */
	protected float _myNearAreaRadius;
	
	/**
	 * Cosine of the angle in which neighbors have to be to be included into calculations
	 */
	protected float _myNearAngleCos;
	
	/**
	 * Vector for keeping the position of the group of related Agents
	 */
	protected CCVector3f _myGroupPosition = new CCVector3f();
	
	/**
	 * List for keeping the current neighbors of this agent
	 */
	public List<CCAgent> neighbors = new ArrayList<CCAgent>();
	
	public CCNeighborHoodBehavior(final float theNearAreaRadius, final float theNearAngle){
		_myNeighborhood  = CCNeighborhood.EMPTY;
		_myNearAreaRadius = theNearAreaRadius;
		_myNearAngleCos = CCMath.cos(CCMath.radians(theNearAngle));
	}
	
	public CCNeighborHoodBehavior(){
		this(30,1);
		_myNearAngleCos = 1;
	}
	
	public CCVector3f groupPosition(){
		return _myGroupPosition;
	}
	
	public void neighborhood(final CCNeighborhood<?> theNeighborhood){
		_myNeighborhood = theNeighborhood;
	}
	
	public boolean isInAngle(final CCAgent theAgent, final CCAgent theOtherAgent){
		if(_myNearAngleCos == 1)return true;
		final CCVector3f range = theAgent.position.clone();
		range.subtract(theOtherAgent.position);
		
		float dot = range.dot(theAgent.forward);
		return dot >= _myNearAngleCos;
	}
	
	public void nearAreaRadius(final float theNearAreaRadius){
		_myNearAreaRadius = theNearAreaRadius;
	}
	
	public float nearAreaRadius(){
		return _myNearAreaRadius;
	}
	
	public void nearAngle(final float theAngle){
		_myNearAngleCos = CCMath.cos(CCMath.radians(theAngle));
	}
	
	protected int _myCount = 0;
	
	
	public boolean apply(final CCParticle theAgent, final CCVector3f theForce, float theDeltaTime){
		_myCount = 0;
		theForce.set(0,0,0);
		
		neighbors.clear();
		
		for (CCAgent myOtherAgent:_myNeighborhood.getNearAgents(theAgent, _myNearAreaRadius)){
			//if (isInAngle(theAgent,myOtherAgent)){
				_myCount++;
				theForce.add(myOtherAgent.position);
				neighbors.add(myOtherAgent);
			//}
		}
		
		if (_myCount > 0){
			theForce.scale(1.0F / _myCount);
			_myGroupPosition.set(theForce);
			return true;
		}
		return false; 
	}
}
