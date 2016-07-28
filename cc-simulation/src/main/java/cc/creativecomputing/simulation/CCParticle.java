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
package cc.creativecomputing.simulation;


import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCVector3f;

/**
 * A vehicle with a local space and needed movement fields
 * @author christianr
 *
 */
public class CCParticle extends CCLocalSpace{

	protected float _myMass;

	@CCControl(name = "max speed")
	public float maxSpeed;

	public float maxForce;
	
	protected float _myAge;
	
	protected boolean _myIsKilled = false;

	protected CCVector3f _myVelocity;

	protected CCVector3f allForces;

	protected CCVector3f _myAcceleration;
	
	public float radius = 0.5f;

	static protected CCVector3f accelUp = new CCVector3f();

	static protected final CCVector3f globalUp = new CCVector3f(0.0F, 0.1F, 0.0F);

	static protected CCVector3f bankUp = new CCVector3f();

	static protected CCVector3f newAccel = new CCVector3f();

	static protected float accelDamping = 0.7F;
	
	static protected CCVector3f steering = new CCVector3f();

	public CCParticle(){
		_myAcceleration = new CCVector3f();
		_myMass = 1.0F;
		_myAge = 0;
		maxSpeed = 1.0F;
		maxForce = 0.04F;
		velocity(new CCVector3f());
		allForces = new CCVector3f();
	}
	
	/**
	 * Returns the future position of the particle based on its velocity
	 * and the given look ahead time.
	 * @param theLookAhead time to look in the future
	 * @return
	 */
	public CCVector3f futurePosition(final float theLookAhead){
		return new CCVector3f(
	        position.x + _myVelocity.x * theLookAhead,
	        position.y + _myVelocity.y * theLookAhead,
	        position.z + _myVelocity.z * theLookAhead
	    );
	}

	
	public void applyForce(CCVector3f force){
		allForces.add(force);
	}

	public void update(float theDeltaTime){
		_myAge += theDeltaTime;
		theDeltaTime *= CCSimulation.TARGET_FRAMERATE;
		
		allForces.truncate(maxForce * theDeltaTime);
		
		newAccel.set(allForces);
		
		if (_myMass != 1.0F){
			newAccel.scale(1.0F / _myMass);
		}
		_myAcceleration.interpolate(accelDamping, newAccel);

		allForces.set(0,0,0);
		
		_myVelocity.add(_myAcceleration);
		_myVelocity.truncate(maxSpeed * theDeltaTime);
		
		position.add(_myVelocity);
		
		accelUp.set(_myAcceleration);
		accelUp.scale(0.5F);
		
		bankUp.set(up);
		bankUp.add(accelUp);
		bankUp.add(globalUp);
		bankUp.normalize();
		
		float speed = _myVelocity.length();
		
		if (speed > 0.0F){
			forward.set(_myVelocity);
			forward.scale(1.0F / speed);
			side.set(forward.cross(bankUp));
			up.set(side.cross(forward));
		}
	}
	
	public void draw(CCGraphics g){	//this is very inefficient, use wisely
		g.beginShape(CCDrawMode.POINTS);
			g.vertex( position );
		g.endShape();
	}
	
	/**
	 * Returns the speed relative to maximum speed of the vehicle
	 * @return
	 */
	public float relativeSpeed() {
		return _myVelocity.length() / maxSpeed;
	}

	public void velocity(CCVector3f velocity) {
		_myVelocity = velocity;
	}

	public CCVector3f velocity() {
		return _myVelocity;
	}

	public void acceleration(CCVector3f acceleration) {
		_myAcceleration = acceleration;
	}

	public CCVector3f acceleration() {
		return _myAcceleration;
	}
	
	public float age(){
		return _myAge;
	}
	
	public void age(final float theAge){
		_myAge = theAge;
	}
	
	public boolean isKilled(){
		return _myIsKilled;
	}
	
	public void isKilled(final boolean theIsKilled){
		_myIsKilled = theIsKilled;
	}
}
