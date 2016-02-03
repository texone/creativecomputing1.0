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
package cc.creativecomputing.demo.graphics.shader.noise;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.util.CCGPUNoise;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;

import com.jogamp.opengl.cg.CGparameter;

public class CCGPUNoiseFieldTest extends CCApp {
	
	CCCGShader _myShader;
	CGparameter _myNoiseScaleParameter;
	CGparameter _myNoiseOffsetParameter;

	CCArcball _myArcball;
	CCVBOMesh _myMesh;
	
	public void setup() {
		g.clearColor(0);
		_myShader = new CCCGShader(
			CCIOUtil.classPath(this, "noisefield.vp"),
			null
		);
		_myShader.load();
		
		_myNoiseScaleParameter = _myShader.vertexParameter("noiseScale");
		_myNoiseOffsetParameter = _myShader.vertexParameter("noiseOffset");
		CCGPUNoise.attachVertexNoise(_myShader);
		
		_myArcball = new CCArcball(this);
		_myMesh = new CCVBOMesh(CCDrawMode.LINES,2000000);
		
		g.pointSize(20);
		g.strokeWeight(0.1f);
		g.smooth();
		
		for(float x = -1000; x < 1000; x +=2){
			for(float y = -1000; y < 1000; y +=2){
				_myMesh.addColor(0,0,0);
				_myMesh.addVertex(x,y,0);
				_myMesh.addColor(1,1,1,0.15f);
				_myMesh.addVertex(x,y,30);
			}
		}
	}
	
	float time = 0;
	CCVector3f _myOffset = new CCVector3f();
	CCVector3f _myOldOffset = new CCVector3f();
	CCVector3f _myNewOffset = new CCVector3f(10,10);
	
	float blend = 0;
	float max = 30;
	
	public void update(final float theDeltaTime){
		time += theDeltaTime * 1;
		if(time > max){
			time -= max;
			_myOldOffset = _myNewOffset;
			_myNewOffset = CCVecMath.random(-10, 10, -10, 10,-10,10);
		}
		blend = (CCMath.cos((time / max)*CCMath.PI + CCMath.PI)+1)/2;
		_myOffset = CCVecMath.blend(blend, _myOldOffset, _myNewOffset);
	}

	public void draw() {
		g.clear();
		_myArcball.draw(g);
		g.color(1f,0.05f);
		g.noDepthTest();
		g.blend(CCBlendMode.ADD);
		_myShader.start();
		_myShader.parameter(_myNoiseScaleParameter, (1 - CCMath.abs((blend - 0.5f) * 2))*0.01f + 0.002f);
		_myShader.parameter(_myNoiseOffsetParameter, _myOffset);
		_myMesh.draw(g);
		_myShader.end();
		g.noBlend();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCGPUNoiseFieldTest.class);
		myManager.settings().size(1400, 750);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
