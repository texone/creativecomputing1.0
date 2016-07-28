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

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.simulation.particles.CCGPUParticles;
import cc.creativecomputing.simulation.particles.CCGPUUpdateShader;

import com.jogamp.opengl.cg.CGparameter;
import com.jogamp.opengl.cg.CgGL;


public abstract class CCGPUForce{
	
	protected String _myParameterIndex;
	protected String _myShaderTypeName;
	protected CCGPUUpdateShader _myVelocityShader;
	protected CCGPUParticles _myParticles;
	
	protected float _cStrength = 1;
	
	protected CGparameter _myStrengthParameter;
	
	protected CCGPUForce(final String theShaderTypeName){
		_myShaderTypeName = theShaderTypeName;
	}
	
	public void setShader(CCGPUParticles theParticles, CCGPUUpdateShader theShader, final int theIndex, final int theWidth, final int theHeight){
		setShader(theParticles,theShader,"forces["+theIndex+"]", theWidth, theHeight);
	}
	
	public void setShader(CCGPUParticles theParticles, CCGPUUpdateShader theShader, final String theIndex, final int theWidth, final int theHeight) {
		_myParticles = theParticles;
		_myVelocityShader = theShader;
		_myParameterIndex = theIndex;
		_myVelocityShader.checkError("Problem creating force.");
		CgGL.cgConnectParameter(
			_myVelocityShader.createFragmentParameter(_myShaderTypeName), 
			_myVelocityShader.fragmentParameter(_myParameterIndex)
		);
		setupParameter(theWidth, theHeight);
//		update(0);
		_myVelocityShader.checkError("Problem creating force.");
	}
	
	/**
	 * This is used internally and should not be called
	 * @param theWidth
	 * @param theHeight
	 */
	protected void setupParameter(int theWidth, int theHeight) {
		_myStrengthParameter = parameter("strength");
	}

	/**
	 * @param theG
	 * @param theWidth
	 * @param theHeight
	 */
	public void setSize(CCGraphics theG, int theWidth, int theHeight) {
	}
	
	public void update(final float theDeltaTime) {
		_myVelocityShader.parameter(_myStrengthParameter, _cStrength);
	}
	
	public void reset() {
		
	}
	
	/**
	 * Set the strength of the force. The default value is one.
	 * @param theStrength strength value to scale the force
	 */
	public void strength(final float theStrength) {
		_cStrength = theStrength;
	}
	
	protected CGparameter parameter(final String theName){
		return _myVelocityShader.fragmentParameter(_myParameterIndex+"."+theName);
	}
}
