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
package cc.creativecomputing.demo.topic.interaction;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.signal.CCSimplexNoise;

public class CCAccelerationConstraintTest extends CCApp {

	@CCControl(name = "target speed", min = 0, max = 1)
	private float _cTargetSpeed = 0;
	
	@CCControl(name = "target x", min = 0, max = 1)
	private float _cTargetX = 0;
	
	@CCControl(name = "lookAhead", min = 0, max = 10)
	private float _cLookAhead = 0;
	
	@CCControl(name = "max lookAhead", min = 0, max = 10)
	private float _cMaxLookAhead = 0;
	
	@CCControl(name = "max acc", min = 0, max = 100)
	private float _cMaxAcc = 0;
	
	@CCControl(name = "max vel", min = 0, max = 100)
	private float _cMaxVel = 0;
	
	private float _myX = 0;
	private float _myVelocity = 0;
	private float _myAcceleration = 0;
	
	private float _myTargetX = 0;
	private float _myLastTarget = 0;
	private float _myTargetVelocity = 0;
	private float _myLastTargetVelocity = 0;
	private float _myTargetAcceleration = 0;
	
	private float _myFutureX = 0;
	
	@Override
	public void setup() {
		addControls("app", "app", this);
	}
	
	private float _myTime = 0;
	
	private CCSimplexNoise _myNoise;
	
	private float target(float theTime){
		return (_myNoise.value(theTime) - 0.5f) * width;
	}

	@Override
	public void update(final float theDeltaTime) {
		_myNoise = new CCSimplexNoise();
		_myTime += theDeltaTime;
		
		_myLastTarget = _myTargetX;
		_myTargetX = target((int)_myTime * _cTargetSpeed);
//		_myTargetX = CCMath.blend(-width/2, width/2, _cTargetX);//
		
		_myLastTargetVelocity = _myTargetVelocity;
		_myTargetVelocity = (_myTargetX - _myLastTarget) / theDeltaTime;
		
		_myTargetAcceleration = (_myTargetVelocity - _myLastTargetVelocity) / theDeltaTime;
		
		_myFutureX = _myTargetX + _myTargetVelocity * _cLookAhead;
		
		float PositionDifference = _myFutureX - _myX;
		float myChange = CCMath.constrain(PositionDifference, -_cMaxVel, _cMaxVel);
		
		_myAcceleration = (myChange - _myVelocity) / theDeltaTime;
		_myAcceleration = CCMath.constrain(_myAcceleration, -_cMaxAcc, _cMaxAcc);
		_myVelocity += _myAcceleration * theDeltaTime;
		
		_myX+= _myVelocity * theDeltaTime;
	}

	@Override
	public void draw() {
		g.clear();
		
		g.color(1f,0,0);
		g.line(_myFutureX, -height/2, _myFutureX, height/2);
		
		g.color(0,1f,0,0.5f);
		g.line(_myTargetX, -height/2, _myTargetX, height/2);
		g.rect(0,0,10,_myTargetVelocity);
		g.rect(20,0,10,_myTargetAcceleration);
		
		g.color(1f,0.5f);
		g.line(_myX, -height/2, _myX, height/2);
		g.rect(10, 0, 10, _myVelocity);
		g.rect(30, 0, 10, _myAcceleration);
		
		g.color(1f);
		g.line(0,-_cMaxVel,20,-_cMaxVel);
		g.line(0,_cMaxVel,20,_cMaxVel);
		g.line(20,-_cMaxAcc,40,-_cMaxAcc);
		g.line(20,_cMaxAcc,40,_cMaxAcc);

//		g.color(1f,0,0);
//		g.line(_myFutureTarget, -height/2, _myFutureTarget, height/2);
		
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCAccelerationConstraintTest.class);
		myManager.settings().size(1400, 500);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

