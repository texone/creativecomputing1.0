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
package cc.creativecomputing.simulation.steering;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.events.CCUpdateListener;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.simulation.CCParticle;
import cc.creativecomputing.simulation.CCParticleGroup;
import cc.creativecomputing.simulation.steering.behavior.CCNeighborHoodBehavior;


/** 
 * The CCNeighborhood class implements a spatial scene lookup table. 
 * Normally used as a pre-simulation simulator, it calculates the 
 * distances between the vehicles in the scene. It can be used to
 * query for all vehicles or obstacles within a defined radius of 
 * another vehicle.
 */
public class CCNeighborhood<AgentType extends CCAgent> extends CCParticleGroup<AgentType>implements CCUpdateListener{
	
	private List<CCObstacle> _myObstacles = new ArrayList<CCObstacle>();
	
	public static CCNeighborhood<CCAgent> EMPTY = new CCNeighborhood<CCAgent>();

	/** 
	 * Distance matrix 
	 **/
	protected float[][] _myDistanceMatrix;

	/**
	 * Number of elements in the distance matrix 
	 **/
	protected int _myNumberOfElements = 0;

	/** 
	 * Constructor 
	 **/
	public CCNeighborhood(){
		super();
	}

	@Override
	/** 
	 * Adds a new vehicle to the scene description. The vehicle is
	 * automatically added to all pre- and post-Simulations as well.
	 * @param theNewVehicle The vehicle object to add to the scene
	 */
	public void addParticle(final AgentType theAgent) {
		// Register this class in all behaviors that need it 
		CCMind _myMind = theAgent.mind();

		if (_myMind != null) {
			for (CCNeighborHoodBehavior myBehavior : _myMind.neighborhoodBehaviors()) {
				myBehavior.neighborhood(this);
			}
		}

		super.addParticle(theAgent);
	}
	
	public void add(CCObstacle theObstacle) {
		_myObstacles.add(theObstacle);
	}

	/** 
	 * Initializes the neighborhood object distance matrix
	 */
	public void init() {
		// get number of elements
		_myNumberOfElements = _myParticles.size();

		// create distance matrix
		_myDistanceMatrix = new float[_myNumberOfElements][_myNumberOfElements];

		// fill distance matrix with -1 	
		for (int i = 0; i < _myNumberOfElements; i++) {
			for (int j = 0; j < _myNumberOfElements; j++) {
				_myDistanceMatrix[i][j] = -1;
			}
		}
	}

	/** 
	 * Removes all objects from the scene description.
	 * The pre- and post-simulations are also cleared of all objects	 
	 */
	public void removeAll() {
		_myParticles.clear();

		// Reset the element count to force a re-initialization of the distance matrix
		_myNumberOfElements = 0;
	}

	/**
	 * Replaces the list of vehicles with a new list
	 *
	 * @param theAgents Array of vehicles
	 */
	public void agents(final List<AgentType> theAgents) {
		_myParticles = theAgents;
	}

	/** 
	 * Updates the current neighborhood state and recalculates
	 * the distance informations. If the number of vehicles has
	 * changed, the distance matrix is completely regenerated.
	 */
	public void update(final float theDeltaTime) {
		if (_myParticles.size() != _myNumberOfElements){
			init();
		}
		
		for (int i = 0; i < _myNumberOfElements; i++) {
			for (int j = i + 1; j < _myNumberOfElements; j++) {

				// quadratic distance
				float lenSqr = _myParticles.get(i).position.distanceSquared(_myParticles.get(j).position);
				_myDistanceMatrix[i][j] = lenSqr;
				_myDistanceMatrix[j][i] = lenSqr;
			}
		}
		
		super.update(theDeltaTime);
	}

	/** 
	 * Returns the number of elements in the distance matrix
	 * @return Number of elements in the distance matrix
	 */
	public int getCount() {
		return _myParticles.size();
	}
	
	/** 
	 * Returns an array of vehicles whose distance from the vehicle
	 * is less than the specified distance
	 * @param theAgent CCVehicle used as center for search
	 * @param theDistance maximum distance from vehicle
	 * @return Array of vehicles
	 */
	public List<CCObstacle> getNearObstacles(final CCParticle theAgent, final float theDistance){
		final List<CCObstacle> myResult = new ArrayList<CCObstacle>();
		
		final CCVector3f myMinCorner = theAgent.position.clone();
		myMinCorner.subtract(theDistance, theDistance, theDistance);
		
		final CCVector3f myMaxCorner = theAgent.position.clone();
		myMaxCorner.add(theDistance, theDistance, theDistance);
		for(CCObstacle myObstacle:_myObstacles){
			// Add the obstacle to the result set
			if (myObstacle.domain().intersectsBox(myMinCorner, myMaxCorner)){
				myResult.add(myObstacle);								
			}
		}				
		            
		return myResult;	
	}

	/** 
	 * Returns an array of vehicles whose distance from the vehicle
	 * is less than the specified distance
	 * @param v CCVehicle used as center for search
	 * @param theDistance maximum distance from vehicle
	 * @return Array of vehicles
	 */
	public List<AgentType> getNearAgents(final CCParticle theAgent, final float theDistance) {
		final List<AgentType> _myResult = new ArrayList<AgentType>();

		// get the index of the vehicle
		final int myVehicleIndex = _myParticles.indexOf(theAgent);
		
		if(myVehicleIndex == -1)return _myResult;

		// quadratic distance
		final float distSquared = theDistance * theDistance;

	
		
		// query distance matrix and put near vehicles in the result list
		for (int i = 0; i < _myNumberOfElements; i++) {
			if ((i != myVehicleIndex) && (_myDistanceMatrix[myVehicleIndex][i] <= distSquared)){
				_myResult.add(_myParticles.get(i));
			}
		}

		return _myResult;
	}
}
