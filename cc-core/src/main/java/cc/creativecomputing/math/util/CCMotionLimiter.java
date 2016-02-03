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
package cc.creativecomputing.math.util;

import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.math.CCMath;

/**
 * @author christianriekoff
 *
 */
public class CCMotionLimiter {
	@CCControl(name = "look ahead", min = 0, max = 1)
	public static float _cLookAhead = 0;
	
	@CCControl(name = "max velocity", min = 0, max = 25000)
	public static float _cMaxVelocity = 0;
	
	@CCControl(name = "max acceleration", min = 0, max = 25000)
	public static float _cMaxAcceleration = 0;

	protected double _myPosition = 0;
	protected double _myFuturePosition = 0;
	protected double _myVelocity = 0;
	protected double _myAcceleration = 0;
	
	protected double _myMinRange = 0;
	protected double _myMaxRange = 0;
	protected boolean _myLimitRange = false;
	
	public double limit(double theTarget, double theDeltaTime) {
		if(_myLimitRange) {
//			theTarget = CCMath.constrain(theTarget, _myMinRange, _myMaxRange);
		}
		
		_myFuturePosition = _myPosition + _myVelocity * _cLookAhead;
		
		if(_myLimitRange) {
//			_myFuturePosition = CCMath.constrain(_myFuturePosition, _myMinRange, _myMaxRange);
		}
		
		double myFutureTargetDiff = theTarget - _myFuturePosition;
		
		_myAcceleration = myFutureTargetDiff / theDeltaTime;
		_myAcceleration = CCMath.constrain(_myAcceleration, -_cMaxAcceleration, _cMaxAcceleration);
//		
		_myVelocity += _myAcceleration * theDeltaTime;
		_myVelocity = CCMath.constrain(_myVelocity, -_cMaxVelocity, _cMaxVelocity);
		
		_myPosition += _myVelocity * theDeltaTime;
		
		return _myPosition;
	}
	
	public float stay(){
		return (float)(CCMath.sq(_myVelocity) / (2 * _cMaxAcceleration));
	}
	
	public float limit(float theTarget, float theDeltaTime) {
		return (float)limit((double)theTarget, (double)theDeltaTime);
	}
	
	public void range(float theMin, float theMax){
		_myMinRange = theMin;
		_myMaxRange = theMax;
		_myLimitRange = true;
	}
	
	public double futurePosition() {
		return _myFuturePosition;
	}
	
	public double acceleration() {
		return _myAcceleration;
	}
	
	public double velocity() {
		return _myVelocity;
	}
}
