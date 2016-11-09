/*  
 * Copyright (c) 2009  Christian Riekoff <info@texone.org>  
 *  
 *  This file is free software: you may copy, redistribute and/or modify it  
 *  under the terms of the GNU General Public License as published by the  
 *  Free Software Foundation, either version 2 of the License, or (at your  
 *  option) any later version.  
 *  
 *  This file is distributed in the hope that it will be useful, but  
 *  WITHOUT ANY WARRANTY; without even the implied warranty of  
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  
 *  General Public License for more details.  
 *  
 *  You should have received a copy of the GNU General Public License  
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.  
 *  
 * This file incorporates work covered by the following copyright and  
 * permission notice:  
 */
package cc.creativecomputing.demo.simulation.gpuparticles;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.events.CCMouseEvent;
import cc.creativecomputing.simulation.particles.fluidfield.CCGPUFluid;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.math.CCVector2f;

public class CCGPUFluidTest extends CCApp {
	
	@CCControl(name = "advect speed", min = 0, max = 100)
	private float _cAdvectSpeed = 0;
		
	@CCControl(name = "viscousity", min = 0, max = 1)
	private float _cViscousity = 0;
		
	@CCControl(name = "darking", min = 0, max = 0.001f)
	private float _cDarking = 0;
	
	@CCControl(name = "color radius", min = 0, max = 1)
	private float _cColorRadius = 0;
	
	@CCControl(name = "impulse radius", min = 0, max = 1)
	private float _cImpulseRadius = 0;
	
	private CCGPUFluid _myFluid;

	@Override
	public void setup() {
		
//		frameRate(20);
		
		addControls("fluid", "fluid", this);
		
		_myFluid = new CCGPUFluid(g, 500, 500);
	}
	
	private boolean _myShowBoundary = false;
	
	float _myTime = 0;
	
	public void update(final float theDeltaTime) {
		_myFluid.colorDarking(_cDarking);
		_myFluid.advectSpeed(_cAdvectSpeed);
		_myFluid.viscousity(_cViscousity);
		_myFluid.colorRadius(_cColorRadius);
		_myFluid.impulseRadius(_cImpulseRadius);
		
		CCVector2f myPosition = new CCVector2f(mouseX / (float) width, 1 - mouseY / (float) height);
		
		if (_myMouseMovement != null) {
			CCVector2f myMovement = _myMouseMovement.clone();
			myMovement.normalize();
			myMovement.add(1, 1);
			myMovement.scale(0.5f);
			
			

			_myFluid.adImpulse(myPosition, myMovement);

			_myMouseMovement = null;
		}

		_myTime+= theDeltaTime;
		if(_myTime > 10) {
			_myTime = 0;
		}
		
		CCColor myColor = new CCColor(1f);
		myColor.setHSB(_myTime/10, 1f, 1f);
		_myFluid.addColor(myPosition, myColor);
		
		_myFluid.update(theDeltaTime);
	}

	@Override
	public void draw() {
		g.clear();
//		g.scale(1,-1);
//		g.translate(-width/2, -height/2);
		if(_myShowBoundary)g.image(_myFluid.velocityBuffer().attachment(0), -width/2,-height/2,width,height);
		else g.image(_myFluid.colorBuffer().attachment(0), -width/2,-height/2,width,height);
	}
	
	CCVector2f _myMouseMovement;
	CCColor _myColor;
	
	public void mouseDragged(final CCMouseEvent theEvent) {
		_myMouseMovement = new CCVector2f(theEvent.x() - theEvent.px(), theEvent.y() - theEvent.py());
		_myColor = new CCColor(1f);
	}
	
	@Override
	public void keyPressed(CCKeyEvent theEvent) {
		_myShowBoundary = !_myShowBoundary;
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCGPUFluidTest.class);
		myManager.settings().size(1000, 800);
		myManager.start();
	}
}

