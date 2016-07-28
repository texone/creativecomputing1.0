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

public class CCGPUViscousDrag extends CCGPUForce{
	private CGparameter _myCoefficientParameter;
	private float _myCoefficient;
	
	public CCGPUViscousDrag(final float theCoefficient) {
		super("ViscousDrag");
		_myCoefficient = theCoefficient;
	}
	
	public void drag(float theDrag) {
		_myCoefficient = theDrag;
	}

	@Override
	public void setupParameter(int theWidth, int theHeight){
		super.setupParameter(theWidth, theHeight);
		_myCoefficientParameter  = parameter("coefficient");
	}


	@Override
	public void update(final float theDeltaTime) {
		super.update(theDeltaTime);
		_myVelocityShader.parameter(_myCoefficientParameter, _myCoefficient);
	}
	
	
}
