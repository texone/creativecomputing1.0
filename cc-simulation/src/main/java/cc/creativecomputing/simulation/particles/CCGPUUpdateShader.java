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
package cc.creativecomputing.simulation.particles;

import java.util.List;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.shader.util.CCGPUNoise;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.simulation.particles.constraints.CCGPUConstraint;
import cc.creativecomputing.simulation.particles.forces.CCGPUForce;
import cc.creativecomputing.simulation.particles.impulses.CCGPUImpulse;

import com.jogamp.opengl.cg.CGparameter;
import com.jogamp.opengl.cg.CgGL;


/**
 * @invisible
 * @author info
 *
 */
public class CCGPUUpdateShader extends CCCGShader{

	protected CGparameter _myVelocityTextureParameter;
	protected CGparameter _myPositionTextureParameter;
	protected CGparameter _myInfoTextureParameter;
	protected CGparameter _myColorTextureParameter;
	protected CGparameter _myStaticPositionTextureParameter;
	protected CGparameter _myStaticPositionBlendParameter;
	protected CGparameter _myDeltaTimeParameter;
	
	protected CGparameter _myForcesParameter;
	protected CGparameter _myConstraintsParameter;
	protected CGparameter _myImpulsesParameter;
	
	protected CCGPUUpdateShader(
		final CCGPUParticles theParticles,
		final CCGraphics theGraphics, 
		final List<CCGPUForce> theForces , 
		final List<CCGPUConstraint> theConstrains,
		final List<CCGPUImpulse> theImpulses,
		final String[] theShaderFile,
		final int theWidth,
		final int theHeight
	){
		super(null,theShaderFile);

		checkError("created velocity shader");
		
		_myForcesParameter = fragmentParameter("forces");
		CgGL.cgSetArraySize(_myForcesParameter, theForces.size());
		
		int myIndex = 0;
		for(CCGPUForce myForce:theForces){
			myForce.setShader(theParticles, this, myIndex++, theWidth, theHeight);
		}
		
		_myConstraintsParameter = fragmentParameter("constraints");
		CgGL.cgSetArraySize(_myConstraintsParameter, theConstrains.size());
		
		int myConstraintIndex = 0;
		for(CCGPUConstraint myConstraint:theConstrains){
			myConstraint.setShader(this, myConstraintIndex++, theWidth, theHeight);
		}
		
		_myImpulsesParameter = fragmentParameter("impulses");
		CgGL.cgSetArraySize(_myImpulsesParameter, theImpulses.size());
		
		int myImpulseIndex = 0;
		for(CCGPUImpulse myImpulse:theImpulses){
			myImpulse.setShader(this, myImpulseIndex++, theWidth, theHeight);
		}
		
		_myPositionTextureParameter = fragmentParameter("positionTexture");
		_myInfoTextureParameter = fragmentParameter("infoTexture");
		_myVelocityTextureParameter = fragmentParameter("velocityTexture");
		_myColorTextureParameter = fragmentParameter("colorTexture");
		_myStaticPositionTextureParameter = fragmentParameter("staticPositions");
		_myStaticPositionBlendParameter = fragmentParameter("staticPositionBlend");
		_myDeltaTimeParameter = fragmentParameter("deltaTime");
		
		load();
		
//		for(CCGPUForce myForce:theForces){
//			myForce.setupParameter(theWidth, theHeight);
//		}
		CCGPUNoise.attachFragmentNoise(this);
	}
	
	public CCGPUUpdateShader(
		final CCGPUParticles theParticles,
		final CCGraphics theGraphics, 
		final List<CCGPUForce> theForces, 
		final List<CCGPUConstraint> theConstrains,
		final List<CCGPUImpulse> theImpulses,
		final int theWidth,
		final int theHeight
	){
		this(
			theParticles,
			theGraphics, 
			theForces, 
			theConstrains, 
			theImpulses,
			new String[] {
				CCIOUtil.classPath(CCGPUUpdateShader.class,"shader/simplex.fp"),
				CCIOUtil.classPath(CCGPUUpdateShader.class,"shader/forces.fp"),
				CCIOUtil.classPath(CCGPUUpdateShader.class,"shader/constraints.fp"),
				CCIOUtil.classPath(CCGPUUpdateShader.class,"shader/impulses.fp"),
				CCIOUtil.classPath(CCGPUUpdateShader.class,"shader/velocity.fp")
			},
			theWidth, theHeight
		);
	}
	
	public void staticPositions(CCTexture2D thePositions){
		texture(_myStaticPositionTextureParameter, thePositions.id());
	}
	
	public void staticPositionBlend(float thePositionBlend){
		parameter(_myStaticPositionBlendParameter, thePositionBlend);
	}
	
	public void data(final CCShaderBuffer theDataBuffer){
		texture(_myPositionTextureParameter, theDataBuffer.attachment(0).id());
		texture(_myInfoTextureParameter, theDataBuffer.attachment(1).id());
		texture(_myVelocityTextureParameter, theDataBuffer.attachment(2).id());
		texture(_myColorTextureParameter, theDataBuffer.attachment(3).id());
	}
	
	public void deltaTime(final float theDeltaTime){
		parameter(_myDeltaTimeParameter, theDeltaTime);
	}
	
}
