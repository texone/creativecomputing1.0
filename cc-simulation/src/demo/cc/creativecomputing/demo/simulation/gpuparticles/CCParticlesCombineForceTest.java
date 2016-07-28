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
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.particles.CCGPUIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.CCGPUParticles;
import cc.creativecomputing.simulation.particles.constraints.CCGPUConstraint;
import cc.creativecomputing.simulation.particles.forces.CCGPUCombinedForce;
import cc.creativecomputing.simulation.particles.forces.CCGPUForce;
import cc.creativecomputing.simulation.particles.forces.CCGPUForceField;
import cc.creativecomputing.simulation.particles.forces.CCGPUGravity;
import cc.creativecomputing.simulation.particles.forces.CCGPUViscousDrag;

public class CCParticlesCombineForceTest extends CCApp {
	
	private CCGPUParticles _myParticles;
	private CCGPUIndexParticleEmitter _myEmitter;
	private CCArcball _myArcball;
	
	private CCGPUForceField _myForceField = new CCGPUForceField(0.005f,1,new CCVector3f(100,20,30));

	public void setup() {
		_myArcball = new CCArcball(this);
		
		final List<CCGPUForce> myCombinedForces = new ArrayList<CCGPUForce>();
		myCombinedForces.add(new CCGPUGravity(new CCVector3f(1,0,0)));
		myCombinedForces.add(new CCGPUViscousDrag(0.3f));
		myCombinedForces.add(_myForceField);
		
		CCGPUCombinedForce myCombinedForce = new CCGPUCombinedForce(myCombinedForces);
		
		final List<CCGPUForce> myForces = new ArrayList<CCGPUForce>();
		myForces.add(myCombinedForce);
		
		_myParticles = new CCGPUParticles(g, myForces, new ArrayList<CCGPUConstraint>(), 500,500);
		_myParticles.addEmitter(_myEmitter = new CCGPUIndexParticleEmitter(_myParticles));
		g.pointSize(5);
	}
	
	private float _myTime = 0;
	
	public void update(final float theDeltaTime){
		_myTime += 1/30f * 0.5f;
		for(int i = 0; i < 3000; i++){
			_myEmitter.emit(
				new CCVector3f(-400,0,0),
				CCVecMath.random3f(10),
				10, false
			);
		}
		_myParticles.update(theDeltaTime);
		
		_myForceField.noiseOffset(new CCVector3f(_myTime*0.5f,0,0));
		_myForceField.noiseScale((CCMath.sin(_myTime * 0.5f)+1) * 0.0025f + 0.005f);
	}

	public void draw() {
		g.clear();
		g.noDepthTest();
		g.pushMatrix();
		_myArcball.draw(g);
		g.blend(CCBlendMode.ADD);
		g.color(255);
//		g.pointSprite(_mySpriteTexture);
//		g.smooth();
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
		CCApplicationManager myManager = new CCApplicationManager(CCParticlesCombineForceTest.class);
		myManager.settings().size(1200, 600);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
