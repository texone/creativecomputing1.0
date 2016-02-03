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
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.util.CCGPUTextureNoise;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;

import com.jogamp.opengl.cg.CGparameter;

public class CCGPUTextureNoiseFragmentTest extends CCApp {
	
	private CCCGShader _myNoiseShader;
	private CGparameter _myNoiseScaleParameter;
	private CGparameter _myNoiseOffsetParameter;
	
	public void setup() {
		g.noBlend();
		_myNoiseShader = new CCCGShader(
			null,
			CCIOUtil.classPath(this, "texturenoisefragment.fp")
		);
		_myNoiseScaleParameter = _myNoiseShader.fragmentParameter("noiseScale");
		_myNoiseOffsetParameter = _myNoiseShader.fragmentParameter("noiseOffset");
		_myNoiseShader.load();
		
		CCGPUTextureNoise.attachFragmentNoise(_myNoiseShader);
	}
	
	float time = 0;
	CCVector3f _myOffset = new CCVector3f();
	CCVector3f _myOldOffset = new CCVector3f();
	CCVector3f _myNewOffset = new CCVector3f(1,1,1);
	
	float blend = 0;
	float max = 2;
	
	public void update(final float theDeltaTime){
		time += theDeltaTime * 1f;
		if(time > max){
			time -= max;
			_myOldOffset = _myNewOffset;
			_myNewOffset = CCVecMath.random(-1, 1, -1, 1, -1, 1);
		}
		blend = (CCMath.cos((time / max)*CCMath.PI + CCMath.PI)+1)/2;
		_myOffset = CCVecMath.blend(blend, _myOldOffset, _myNewOffset);
	}

	public void draw() {
		g.clear();
		_myNoiseShader.start();
		_myNoiseShader.parameter(_myNoiseScaleParameter, 1 - CCMath.abs((blend - 0.5f) * 2));
		_myNoiseShader.parameter(_myNoiseOffsetParameter, _myOffset);
		g.rect(-width/2, -height/2, width, height);
		_myNoiseShader.end();
	}
	
	
	
	/**
	 * main, just calls things in the appropriate order
	 */

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCGPUTextureNoiseFragmentTest.class);
		myManager.settings().size(1000, 1000);
		myManager.start();
	}
}
