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

public class CCGPUNoiseHeightMapForce extends CCGPUForce{
	private float _myNoiseScale;
	private float _myHeight;
	private CCVector3f _myNoiseOffset;
	
	private CGparameter _myNoiseScaleParameter;
	private CGparameter _myNoiseOffsetParameter;
	private CGparameter _myHeightParameter;
	
	public CCGPUNoiseHeightMapForce(final float theNoiseScale, final float theStrength, final float theHeight, final CCVector3f theNoiseOffset){
		super("NoiseHeightmapForce");
		_myNoiseScale = theNoiseScale;
		_cStrength = theStrength;
		_myNoiseOffset = theNoiseOffset;
		_myHeight = theHeight;
	}
	
	@Override
	public void setupParameter(int theWidth, int theHeight){
		super.setupParameter(theWidth, theHeight);
		_myNoiseScaleParameter = parameter("noiseScale");
		_myNoiseOffsetParameter = parameter("noiseOffset");
		_myHeightParameter = parameter("height");
	}

	@Override
	public void update(final float theDeltaTime) {
		super.update(theDeltaTime);
		_myVelocityShader.parameter(_myNoiseOffsetParameter, _myNoiseOffset);
		_myVelocityShader.parameter(_myNoiseScaleParameter, _myNoiseScale);
		_myVelocityShader.parameter(_myHeightParameter, _myHeight);
	}
	
	public void noiseOffset(final CCVector3f theNoiseOffset){
		_myNoiseOffset = theNoiseOffset;
	}
	
	public CCVector3f noiseOffset(){
		return _myNoiseOffset;
	}
	
	public void noiseScale(final float theNoiseScale){
		_myNoiseScale = theNoiseScale;
	}
	
	public float noiseScale(){
		return _myNoiseScale;
	}
	
	public void height(final float theHeight){
		_myHeight = theHeight;
	}
	
}
