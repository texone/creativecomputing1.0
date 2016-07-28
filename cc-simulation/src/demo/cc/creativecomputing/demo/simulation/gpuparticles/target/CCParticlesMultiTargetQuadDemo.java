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
package cc.creativecomputing.demo.simulation.gpuparticles.target;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.export.CCTileSaver;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.math.util.CCQuad3f;
import cc.creativecomputing.simulation.particles.CCGPUIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.CCGPUParticles;
import cc.creativecomputing.simulation.particles.constraints.CCGPUConstraint;
import cc.creativecomputing.simulation.particles.forces.CCGPUForce;
import cc.creativecomputing.simulation.particles.forces.CCGPUForceField;
import cc.creativecomputing.simulation.particles.forces.CCGPUGravity;
import cc.creativecomputing.simulation.particles.forces.CCGPUViscousDrag;
import cc.creativecomputing.simulation.particles.forces.target.CCGPUMultiTargetForce;
import cc.creativecomputing.simulation.particles.forces.target.CCGPUTargetQuadSetup;
import cc.creativecomputing.simulation.particles.forces.target.CCGPUTargetQuadSetup.CCGPUTargetQuadCreationMode;

public class CCParticlesMultiTargetQuadDemo extends CCApp {
	
	private CCGPUParticles _myParticles;
	private CCGPUIndexParticleEmitter _myEmitter;
	private CCArcball _myArcball;
	
	private CCGPUForceField _myForceField = new CCGPUForceField(0.005f,1,new CCVector3f(100,20,30));
	private CCGPUMultiTargetForce _myTargetForce;
	
	private CCGPUTargetQuadSetup _myTargetSetup;
	
	private CCGPUGravity _myGravity;
	
	private CCGPUViscousDrag _myViscousDrag;
	
	@CCControl(name = "gravity x", min = -1, max = 1)
	private float _cGravityX = 0;
	@CCControl(name = "gravity y", min = -1, max = 1)
	private float _cGravityY = 0;
	@CCControl(name = "gravity z", min = -1, max = 1)
	private float _cGravityZ = 0;
	@CCControl(name = "gravity strength", min = 0, max = 1)
	private float _cGravityStrength = 0;
	
	@CCControl(name = "target strength", min = 0, max = 10)
	private float _cTargetStrength = 0;
	@CCControl(name = "target lookahead", min = 0, max = 10)
	private float _cLookAhead = 0;
	@CCControl(name = "target max force", min = 0, max = 10)
	private float _cMaxForce = 0;
	
	@CCControl(name = "drag", min = 0, max = 1)
	private float _cDrag = 0;
	
	@CCControl(name = "noise speed", min = 0, max = 1)
	private float _cNoiseSpeed = 0;
	@CCControl(name = "noise strength", min = 0, max = 10)
	private float _cNoiseStrength = 0;
	@CCControl(name = "noise scale", min = 0, max = 1)
	private float _cNoiseScale = 0;
	
	private CCQuad3f _myQuad;

	private CCTileSaver _myTileSaver;

	public void setup() {
		_myArcball = new CCArcball(this);
		
		final List<CCGPUForce> myForces = new ArrayList<CCGPUForce>();
		myForces.add(_myViscousDrag = new CCGPUViscousDrag(0.3f));
		myForces.add(_myForceField);
		myForces.add(_myTargetForce = new CCGPUMultiTargetForce(700,700));
		myForces.add(_myGravity = new CCGPUGravity(new CCVector3f()));
		
		_myParticles = new CCGPUParticles(g, myForces, new ArrayList<CCGPUConstraint>(), 700,700);
		_myEmitter = new CCGPUIndexParticleEmitter(_myParticles);
		_myQuad = new CCQuad3f(new CCVector3f(-250,-250,0),new CCVector3f( 250,-250,0),new CCVector3f( 250, 250,0),new CCVector3f(-250, 250,0));
		_myTargetSetup = new CCGPUTargetQuadSetup(_myQuad, CCGPUTargetQuadCreationMode.RANDOM);
		_myTargetForce.targetSetup(_myTargetSetup);
		
		for(int i = 0; i < 700 * 700; i++){
			_myEmitter.emit(
				new CCVector3f(0,0,0),
				CCVecMath.random3f(10),
				10, true
			);
		}
		
		addControls("app", "app", this);
		fixUpdateTime(1/30f);
		_myTileSaver = new CCTileSaver(g);
		addUpdateListener(_myTileSaver);
		addPostListener(_myTileSaver);
	}
	
	private float _myTime = 0;
	
	public void update(final float theDeltaTime){
		_myTargetForce.strength(_cTargetStrength);
		_myTargetForce.lookAhead(_cLookAhead);
		_myTargetForce.maxForce(_cMaxForce);
		_myTime += 1/30f * _cNoiseSpeed;
		
		_myParticles.update(theDeltaTime);
		
		_myForceField.noiseOffset(new CCVector3f(_myTime*0.5f,0,0));
		_myForceField.noiseScale(_cNoiseScale);
		_myForceField.strength(_cNoiseStrength);
		_myViscousDrag.drag(_cDrag);
		_myGravity.direction().set(_cGravityX, _cGravityY, _cGravityZ);
		_myGravity.strength(_cGravityStrength);
	}

	public void draw() {
		g.clear();
		g.noDepthTest();
		_myArcball.draw(g);
		g.blend();
		g.color(255,50);
		_myParticles.renderer().mesh().draw(g);
	}
	
	@Override
	public void keyPressed(final CCKeyEvent theEvent) {
		switch (theEvent.keyCode()) {
		case VK_S:
			_myTileSaver.init("export_tile3/"+frameCount+".png",5);
			g.pointSize(10);
//			CCScreenCapture.capture("export/"+frameCount+".png", width, height);
			break;
		case VK_R:
			g.pointSize(1);
//			_myBasicShader.reload();
//			_myColorScaleShader.reload();
			break;
		case VK_D:
			g.pointSize(1);
			_myParticles.reset();
			for(int i = 0; i < 700 * 700; i++){
				_myEmitter.emit(
					new CCVector3f(0,0,0),
					CCVecMath.random3f(10),
					10, true
				);
			}
			break;
		default:
		}
	}
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCParticlesMultiTargetQuadDemo.class);
		myManager.settings().size(1024, 900);
		myManager.settings().location(0, 0);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
