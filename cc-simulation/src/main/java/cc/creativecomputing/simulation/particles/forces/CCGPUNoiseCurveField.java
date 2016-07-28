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

import com.jogamp.opengl.cg.CGparameter;

public class CCGPUNoiseCurveField extends CCGPUForce{

	private float _myPrediction = 0;
	private float _myOffset = 0;
	private float _myScale = 1;
	private float _myOutputScale = 1;
	private float _myRadius = 1;
	
	private float _mySpeed = 1;
	
	private CGparameter _myPredictionParameter;
	private CGparameter _myOffsetParameter;
	private CGparameter _myScaleParameter;
	private CGparameter _myOutputScaleParameter;
	private CGparameter _myRadiusParameter;
	
	public CCGPUNoiseCurveField(){
		super("NoiseCurveForceFieldFollow");
	}
	
	@Override
	public void setupParameter(int theWidth, int theHeight){
		super.setupParameter(theWidth, theHeight);
		
		_myPredictionParameter = parameter("prediction");
		_myOffsetParameter = parameter("offset");
		_myScaleParameter = parameter("scale");
		_myOutputScaleParameter = parameter("outputScale");
		_myRadiusParameter = parameter("radius");
	}
	
	public void prediction(final float thePrediction) {
		_myPrediction = thePrediction;
	}

	public void scale(float theScale) {
		_myScale = theScale;
	}

	public void outputScale(float theOutputScale) {
		_myOutputScale = theOutputScale;
	}
	
	public void radius(float theRadius) {
		_myRadius = theRadius;
	}

	public void speed(float theSpeed) {
		_mySpeed = theSpeed;
	}

	@Override
	public void update(final float theDeltaTime) {
		super.update(theDeltaTime);
		
		_myOffset += theDeltaTime * _mySpeed;
		
		_myVelocityShader.parameter(_myOffsetParameter, _myOffset);
		_myVelocityShader.parameter(_myOutputScaleParameter, _myOutputScale);
		_myVelocityShader.parameter(_myScaleParameter, _myScale);
		_myVelocityShader.parameter(_myRadiusParameter, _myRadius);
		_myVelocityShader.parameter(_myPredictionParameter, _myPrediction);
	}
	
}
