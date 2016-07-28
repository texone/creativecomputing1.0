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
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.particles.CCGPUIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.CCGPUParticles;
import cc.creativecomputing.simulation.particles.constraints.CCGPUConstraint;
import cc.creativecomputing.simulation.particles.forces.CCGPUForce;
import cc.creativecomputing.simulation.particles.forces.CCGPUForceField;
import cc.creativecomputing.simulation.particles.forces.CCGPUViscousDrag;
import cc.creativecomputing.simulation.particles.render.CCGPUIndexedParticleRenderer;

public class CCParticlesNoiseFlowFieldTest extends CCApp {
	
	private CCGPUParticles _myParticles;
	private CCGPUIndexParticleEmitter _myEmitter;
	private CCArcball _myArcball;
	
	private CCGPUForceField _myForceField = new CCGPUForceField(0.005f,1,new CCVector3f(100,20,30));

	public void setup() {
		_myArcball = new CCArcball(this);
		
		final List<CCGPUForce> myForces = new ArrayList<CCGPUForce>();
		myForces.add(new CCGPUViscousDrag(0.3f));
		myForces.add(_myForceField);
		
		_myParticles = new CCGPUParticles(g, new CCGPUIndexedParticleRenderer(), myForces, new ArrayList<CCGPUConstraint>(), 1000,1000);
		_myParticles.addEmitter(_myEmitter = new CCGPUIndexParticleEmitter(_myParticles));
		
		for(int i = 0; i < 100000; i++){
			_myEmitter.emit(
				new CCVector3f(CCMath.random(-width/2, width/2),CCMath.random(-height/2, height/2),CCMath.random(-height/2, height/2)),
				CCVecMath.random3f(10),
				10, true
			);
		}
	}
	
	private float _myTime = 0;
	
	public void update(final float theDeltaTime){
		_myTime += 1/30f * 0.5f;
		
		_myParticles.update(theDeltaTime);
		
		_myForceField.noiseOffset(new CCVector3f(0,0,_myTime));
		_myForceField.noiseScale(0.0025f);
	}

	public void draw() {
		g.clear();
		g.noDepthTest();
		g.pushMatrix();
		_myArcball.draw(g);
		g.color(255,50);
		g.blend();
//		g.pointSprite(_mySpriteTexture);
//		g.smooth();
		g.blend();
		_myParticles.draw();
//		g.noSmooth();
//		g.noPointSprite();
		g.popMatrix();
		g.color(255);
		g.text(frameRate + ":" + _myEmitter.particlesInUse(),-width/2+20,-height/2+20);
	}
	
	public void keyPressed(CCKeyEvent theEvent) {
		_myParticles.reset();
	}
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCParticlesNoiseFlowFieldTest.class);
		myManager.settings().size(1200, 600);
//		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
