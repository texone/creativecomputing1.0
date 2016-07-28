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
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.particles.CCGPUIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.CCGPUParticles;
import cc.creativecomputing.simulation.particles.constraints.CCGPUConstraint;
import cc.creativecomputing.simulation.particles.forces.CCGPUForce;
import cc.creativecomputing.simulation.particles.forces.CCGPUViscousDrag;
import cc.creativecomputing.simulation.particles.render.CCGPUSortedParticleRenderer;

public class CCParticlesSortRendererTest extends CCApp {
	
	private CCGPUParticles _myParticles;
	private CCGPUSortedParticleRenderer _myRenderer;
	private CCGPUIndexParticleEmitter _myEmitter; 
	private CCArcball _myArcball;

	@CCControl(name = "pointsize", min = 1, max = 500)
	private float _cPointSize = 1;

	@CCControl(name = "alpha", min = 0, max = 1)
	private float _cAlpha = 1;
	
	public void setup() {
		
		addControls("app", "app", this);
		_myArcball = new CCArcball(this);
		
		final List<CCGPUForce> myForces = new ArrayList<CCGPUForce>();
		myForces.add(new CCGPUViscousDrag(0.3f));
//		myForces.add(_myForceField);
		
		_myRenderer = new CCGPUSortedParticleRenderer(g);
		_myParticles = new CCGPUParticles(g, _myRenderer, myForces, new ArrayList<CCGPUConstraint>(), 2,2);
		_myParticles.addEmitter(_myEmitter = new CCGPUIndexParticleEmitter(_myParticles));
		
		for(int i = 0; i < _myParticles.size(); i++){
			_myEmitter.emit(
				new CCVector3f(
					CCMath.random(-width/2, width/2),
					CCMath.random(-height/2, height/2),
					CCMath.map(i, 0, _myParticles.size(),-height/2, height/2)
				),
				CCVecMath.random3f(10),
				1000, true
			);
		}
		_myParticles.update(1f/30);
	}
	
	public void update(final float theDeltaTime){
		
		
//		_myForceField.noiseOffset(new CCVector3f(0,0,_myTime));
//		_myForceField.noiseScale(0.0025f);
		
		_myRenderer.pointSize(_cPointSize);
		_myParticles.update(theDeltaTime);
	}

	public void draw() {
		g.clear();

		g.pushMatrix();
		_myArcball.draw(g);
		g.color(1f, _cAlpha);
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
//		_myParticles.reset();
	}
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCParticlesSortRendererTest.class);
		myManager.settings().size(1200, 600);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
