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
package cc.creativecomputing.demo.simulation.gpuparticles;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.export.CCScreenCapture;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.particles.CCGPUIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.CCGPUParticles;
import cc.creativecomputing.simulation.particles.constraints.CCGPUConstraint;
import cc.creativecomputing.simulation.particles.forces.CCGPUForce;
import cc.creativecomputing.simulation.particles.forces.CCGPUGravity;
import cc.creativecomputing.simulation.particles.forces.CCGPUNoiseHeightMapForce;
import cc.creativecomputing.simulation.particles.forces.CCGPUViscousDrag;
import cc.creativecomputing.util.CCFormatUtil;

public class CCNoiseHeightMapForceDemo extends CCApp {
	
	@CCControl(name = "gravity", min = 0, max = 3f)
	private float _cGravity = 0.1f;
	
	@CCControl(name = "noise scale", min = 0, max = 0.01f)
	private float _cNoiseScale = 0.005f;
	
	private CCGPUParticles _myParticles;
	private CCGPUIndexParticleEmitter _myEmitter;
	private CCArcball _myArcball;
	
	private CCGPUGravity _myGravity = new CCGPUGravity(new CCVector3f(0.1,0,0));
	private CCGPUNoiseHeightMapForce _myForceField = new CCGPUNoiseHeightMapForce(0.005f,1,100,new CCVector3f(100,20,30));

	public void setup() {
		addControls("app", "app", this);
		_myArcball = new CCArcball(this);
		
		final List<CCGPUForce> myForces = new ArrayList<CCGPUForce>();
		myForces.add(_myGravity);
		myForces.add(new CCGPUViscousDrag(0.3f));
		
		final List<CCGPUConstraint> myConstraints = new ArrayList<CCGPUConstraint>();
		myForces.add(_myForceField);
	
		_myParticles = new CCGPUParticles(g,myForces,myConstraints,800,800);
		_myParticles.addEmitter(_myEmitter = new CCGPUIndexParticleEmitter(_myParticles));
		g.smooth();
		
		addControls("app", "app", this);
		_myUI.hide();
	}
	
	float angle = 0;
	private float _myTime = 0;
	
	public void update(final float theDeltaTime){

		_myTime += 1/30f * 0.5f;
		angle += theDeltaTime * 30;
		for(int i = 0; i < 100; i++){
			_myEmitter.emit(
				new CCVector3f(CCMath.random(-400,400), 0,CCMath.random(-400,400)),
				new CCVector3f(CCVecMath.random3f(20)),
				10, false
			);
		}
		
		_myGravity.direction().x = _cGravity;
		
		_myForceField.noiseScale(_cNoiseScale);
		_myForceField.noiseOffset(new CCVector3f(_myTime*0.5f,0,0));
		_myForceField.noiseScale((CCMath.sin(_myTime * 0.5f)+1) * 0.0025f + 0.005f);
		_myParticles.update(theDeltaTime);
	}

	public void draw() {
		
		g.noDepthTest();
		g.clear();
		g.color(255,50);
		
		g.pushMatrix();
		_myArcball.draw(g);
		g.blend(CCBlendMode.ADD);
		g.color(255,50);
		_myParticles.draw();
		g.popMatrix();
		
		g.blend();
	}
	
	public void keyPressed(final CCKeyEvent theEvent){
		switch(theEvent.keyChar()) {
		case 's':
			CCScreenCapture.capture("export/heightmap/heightmap"+CCFormatUtil.nf(frameCount, 4) + ".png", width, height);
			break;
		}
	}
	

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCNoiseHeightMapForceDemo.class);
		myManager.settings().size(1200, 800);
//		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
