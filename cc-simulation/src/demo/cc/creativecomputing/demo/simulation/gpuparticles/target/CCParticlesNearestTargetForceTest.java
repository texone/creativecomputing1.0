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

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.export.CCScreenCapture;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.particles.CCGPUIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.CCGPUParticles;
import cc.creativecomputing.simulation.particles.constraints.CCGPUConstraint;
import cc.creativecomputing.simulation.particles.forces.CCGPUForce;
import cc.creativecomputing.simulation.particles.forces.CCGPUForceField;
import cc.creativecomputing.simulation.particles.forces.CCGPUGravity;
import cc.creativecomputing.simulation.particles.forces.CCGPUViscousDrag;
import cc.creativecomputing.simulation.particles.forces.target.CCGPUNearestGridPositionTargetForce;
import cc.creativecomputing.util.CCFormatUtil;
import cc.creativecomputing.util.logging.CCLog;

public class CCParticlesNearestTargetForceTest extends CCApp {
	
	private CCGPUParticles _myParticles;
	private CCGPUIndexParticleEmitter _myEmitter;
	private CCArcball _myArcball;
	
	private CCGPUForceField _myForceField = new CCGPUForceField(0.005f,1,new CCVector3f(100,20,30));
	private CCGPUNearestGridPositionTargetForce _myTargetForce = new CCGPUNearestGridPositionTargetForce(100,100);
	
	private CCTexture2D _myMaskTexture;
	
	@CCControl(name = "texture offset x", min = -400, max = 400)
	private float _cTextureOffsetX = 0;
	
	@CCControl(name = "texture offset y", min = -400, max = 400)
	private float _cTextureOffsetY = 0;
	
	@CCControl(name = "scale x", min = 0.1f, max = 4)
	private float _cTextureScaleX = 1;
	
	@CCControl(name = "scale y", min = 0.1f, max = 4)
	private float _cTextureScaleY = 1;
	
	@CCControl(name = "target", min = 0f, max = 1f)
	private float _cTarget = 0;
	
	@CCControl(name = "forcefield", min = 0f, max = 1f)
	private float _cForceFiel = 0;
	
	@CCControl(name = "lookAhead", min = -1, max = 1)
	private float _cLookAhead = 0;
	
	@CCControl(name = "targetTime", min = 0, max = 30)
	private float _cTargetTime = 0;

	public void setup() {
		_myArcball = new CCArcball(this);
		hideControls();
		
		final List<CCGPUForce> myForces = new ArrayList<CCGPUForce>();
		myForces.add(new CCGPUGravity(new CCVector3f(0,-4,0)));
		myForces.add(new CCGPUViscousDrag(0.3f));
		myForces.add(_myForceField);
		myForces.add(_myTargetForce);
		
		_myParticles = new CCGPUParticles(g, myForces, new ArrayList<CCGPUConstraint>(), 700,700);
		_myEmitter = new CCGPUIndexParticleEmitter(_myParticles);
		
		_myMaskTexture = new CCTexture2D(CCTextureIO.newTextureData("texone2.png"));
		_myTargetForce.setMask(_myMaskTexture);
		
		addControls("app", "app", this);
	}
	
	private float _myTime = 0;
	
	private boolean _myDoDebug = false;
	private boolean _myDoNextFrame = false;
	
	public void update(final float theDeltaTime){
		if(_myDoDebug && !_myDoNextFrame)return;
		
		for(int i = 0; i < 3000; i++){
			_myEmitter.emit(
				new CCVector3f(CCMath.random(100, 300),height/2,0),
				CCVecMath.random3f(10),
				20
			);
		}
		_myTargetForce.lookAhead(_cLookAhead);
		_myTargetForce.targetTime(_cTargetTime);
		_myTargetForce.textureOffset(_cTextureOffsetX, _cTextureOffsetY);
		_myTargetForce.textureScale(_cTextureScaleX, _cTextureScaleY);
		
		_myForceField.strength(_cForceFiel);
		_myTargetForce.strength(_cTarget);
//		_myTargetForce.center(new CCVector3f(0,height/2 - mouseY,0));
		
		_myTime += 1/30f * 0.5f;
		
		_myParticles.update(theDeltaTime);
		
		_myForceField.noiseOffset(new CCVector3f(_myTime*0.5f,0,0));
		_myForceField.noiseScale((CCMath.sin(_myTime * 0.5f)+1) * 0.0025f+0.005f);
		
		
		if(_myDoDebug) {
			CCLog.info("#### PARTICLE INFOS ######");
			
			FloatBuffer myData = _myTargetForce.particleTargetInfos().getData();
			while(myData.hasRemaining()){
				for(int i = 0; i < 10;i++){
					CCLog.info("[" + myData.get()+","+myData.get()+","+myData.get()+","+myData.get()+"] , ");
				}
				CCLog.info("");
			}
			_myDoNextFrame = false;
			
			CCLog.info("#### TARGET INFOS ######");
			myData = _myTargetForce.targetInfos().getData();
			while(myData.hasRemaining()){
				for(int i = 0; i < 10;i++){
					CCLog.info("[" + myData.get()+","+myData.get()+","+myData.get()+","+myData.get()+"] , ");
				}
			}
			_myDoNextFrame = false;
		}
	}

	public void draw() {
		g.clear();
		g.noDepthTest();
		_myArcball.draw(g);
		g.blend();
		g.color(255,50);
		_myParticles.draw();
//		g.noBlend();
		g.color(255);
//		g.image(_myTargetForce.particleTargetInfos(), -400,0);
//		g.image(_myTargetForce.targetInfos(), -400,-400);
	}
	
	public void keyPressed(CCKeyEvent theEvent) {
		switch(theEvent.keyChar()) {
		case 's':
			CCScreenCapture.capture("export/target/target"+CCFormatUtil.nf(frameCount, 4) + ".png", width, height);
			break;
		case 'd':
			_myDoDebug = !_myDoDebug;
			break;
		case 'f':
			_myDoNextFrame = true;
			break;
		}
	}
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCParticlesNearestTargetForceTest.class);
		myManager.settings().size(1024, 800);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
