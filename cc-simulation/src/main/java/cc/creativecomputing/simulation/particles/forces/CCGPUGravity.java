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

public class CCGPUGravity extends CCGPUForce{
	private CGparameter _myGravityParameter;
	private CCVector3f _myGravity;
	
	public CCGPUGravity(final CCVector3f theGravity) {
		super("Gravity");
		_myGravity = theGravity;
	}

	@Override
	public void setupParameter(int theWidth, int theHeight){
		super.setupParameter(theWidth, theHeight);
		_myGravityParameter  = parameter("gravity");
	}
	
	public CCVector3f direction() {
		return _myGravity;
	}

	@Override
	public void update(final float theDeltaTime) {
		super.update(theDeltaTime);
		_myVelocityShader.parameter(_myGravityParameter, _myGravity);
	}
	
	
}
