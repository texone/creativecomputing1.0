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
package cc.creativecomputing.simulation.particles.forces;

import cc.creativecomputing.math.CCVector3f;

import com.jogamp.opengl.cg.CGparameter;

public class CCGPUAttractor extends CCGPUForce{
	private CCVector3f _myPosition;
	private float _myRadius;
	
	private CGparameter _myPositionParameter;
	private CGparameter _myRadiusParameter;
	
	public CCGPUAttractor(final CCVector3f thePosition, final float theStrength, final float theRadius){
		super("Attractor");
		_myPosition = thePosition;
		_cStrength = theStrength;
		_myRadius = theRadius;
	}

	@Override
	public void setupParameter(int theWidth, int theHeight){
		super.setupParameter(theWidth, theHeight);
		_myPositionParameter = parameter("position");
		_myRadiusParameter = parameter("radius");
	}
	
	@Override
	public void update(final float theDeltaTime) {
		super.update(theDeltaTime);
		_myVelocityShader.parameter(_myPositionParameter, _myPosition);
		_myVelocityShader.parameter(_myStrengthParameter, _cStrength);
		_myVelocityShader.parameter(_myRadiusParameter, _myRadius);
	}
	
	public void position(final CCVector3f thePosition){
		_myPosition = thePosition;
	}
	
	public CCVector3f position(){
		return _myPosition;
	}
	
	public float strength(){
		return _cStrength;
	}
	
	public void radius(final float theRadius){
		_myRadius = theRadius;
	}
	
	public float radius(){
		return _myRadius;
	}

}
