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

import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.simulation.CCParticle;


public class CCAttractor extends CCForce {

	private CCVector3f _myPosition;

	private float _myStrength;

	private float _myRadius;

    private boolean _myActive;

    public CCAttractor() {
        _myPosition = new CCVector3f();
        _myRadius = 100;
        _myStrength = 1;
        _myActive = true;
    }


    public CCVector3f position() {
        return _myPosition;
    }


    public void setPositionRef(CCVector3f thePosition) {
        _myPosition = thePosition;
    }


    public float strength() {
        return _myStrength;
    }


    public void strength(float theStrength) {
        _myStrength = theStrength;
    }


    public float radius() {
        return _myRadius;
    }


    public void radius(float theRadius) {
        _myRadius = theRadius;
    }

    public boolean apply(CCParticle theParticle, CCVector3f theForce, float theDeltaTime) {
    	if (_myStrength != 0) {
    		theForce = CCVecMath.subtract(_myPosition, theParticle.position);
            final float myDistance = fastInverseSqrt(1 / theForce.lengthSquared());
            if (myDistance < _myRadius) {
                float myFallOff = 1f - myDistance / _myRadius;
                final float myForce = myFallOff * myFallOff * _myStrength;
                theForce.scale(myForce / myDistance);
            }
    	}
		return true;
	}


    private static float fastInverseSqrt(float x) {
        /** this is shamelessly stolen from traer ( http://www.cs.princeton.edu/~traer/physics/ ) */
        final float half = 0.5F * x;
        int i = Float.floatToIntBits(x);
        i = 0x5f375a86 - (i >> 1);
        x = Float.intBitsToFloat(i);
        return x * (1.5F - half * x * x);
    }


    public boolean dead() {
        return false;
    }


    public boolean active() {
        return _myActive;
    }


    public void active(boolean theActiveState) {
        _myActive = theActiveState;
    }
}
