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
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.util.CCGPUNoise;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;

import com.jogamp.opengl.cg.CGparameter;

public class CCGPUSimplexNoiseFragmentTest extends CCApp {
	
	private CCCGShader _myNoiseShader;
	private CGparameter _myNoiseScaleParameter;
	private CGparameter _myNoiseOffsetParameter;
	private CGparameter _myNoiseAmountParameter;
	private CGparameter _myFadeParameter;
	private CGparameter _myFadeSizeParameter;
	
	@CCControl(name = "noiseScaleX", min = 0, max = 1)
	private float _cNoiseScaleX = 1;
	
	@CCControl(name = "noiseScaleY", min = 0, max = 1)
	private float _cNoiseScaleY = 1;
	
	@CCControl(name = "noiseScaleZ", min = 0, max = 1)
	private float _cNoiseScaleZ = 1;
	
	@CCControl(name = "noiseOcataves", min = 0, max = 10)
	private float _cNoiseOctaves = 1;
	
	@CCControl(name = "noise speed x", min = 0, max = 20)
	private float _cNoiseSpeeedX = 1;
	
	@CCControl(name = "noise speed y", min = 0, max = 20)
	private float _cNoiseSpeeedY = 1;
	
	@CCControl(name = "noise speed z", min = 0, max = 20)
	private float _cNoiseSpeeedZ = 1;

	@CCControl(name = "noiseAmount", min = 0, max = 20)
	private float _cNoiseAmount = 1;
	
	@CCControl(name = "fade", min = 0, max = 1)
	private float _cFade = 1;
	
	@CCControl(name = "fade size", min = 0, max = 1)
	private float _cFadeSize = 1;
	
	public void setup() {
		g.noBlend();
		_myNoiseShader = new CCCGShader(
			null,
			new String[]{
				CCIOUtil.classPath(CCGPUNoise.class, "simplex.fp"),
				CCIOUtil.classPath(this, "simplexnoisefadefragment.fp")
			}
		);
		_myNoiseScaleParameter = _myNoiseShader.fragmentParameter("noiseScale");
		_myNoiseOffsetParameter = _myNoiseShader.fragmentParameter("noiseOffset");
		_myNoiseAmountParameter = _myNoiseShader.fragmentParameter("noiseAmount");
		_myFadeParameter = _myNoiseShader.fragmentParameter("fade");
		_myFadeSizeParameter = _myNoiseShader.fragmentParameter("fadeSize");
		_myNoiseShader.load();
		
		CCGPUNoise.attachFragmentNoise(_myNoiseShader);
		
		addControls("noise", "noise", this);
	}
	
	float time = 0;
	CCVector3f _myOffset = new CCVector3f();
	CCVector3f _myOldOffset = new CCVector3f();
	CCVector3f _myNewOffset = new CCVector3f(0,0,0);
	
	float blend = 0;
	float max = 2;
	
	public void update(final float theDeltaTime){
		time += theDeltaTime;
		if(time > max){
			time -= max;
			_myOldOffset = _myNewOffset;
			//_myNewOffset = CCVecMath.random(-10, 10, -10, 10, -2, 2);
			_myNewOffset = CCVecMath.random(0, 0, 0, 0, -1, 1);
		}
		blend = (CCMath.cos((time / max)*CCMath.PI + CCMath.PI)+1)/2;
		_myOffset.add(theDeltaTime * _cNoiseSpeeedX, theDeltaTime * _cNoiseSpeeedY, theDeltaTime * _cNoiseSpeeedZ);
	}

	public void draw() {
		g.clear();
		_myNoiseShader.start();
		_myNoiseShader.parameter(_myNoiseScaleParameter, _cNoiseScaleX, _cNoiseScaleY, _cNoiseScaleZ);
		_myNoiseShader.parameter(_myNoiseOffsetParameter, _myOffset);
		_myNoiseShader.parameter(_myNoiseAmountParameter, _cNoiseAmount);
		_myNoiseShader.parameter(_myFadeParameter, _cFade);
		_myNoiseShader.parameter(_myFadeSizeParameter, _cFadeSize);
		g.beginShape(CCDrawMode.QUADS);
		g.textureCoords(0, 0f, 0f);
		g.vertex(-width/2, -height/2);
		g.textureCoords(0, 1f, 0f);
		g.vertex( width/2, -height/2);
		g.textureCoords(0, 1f, 1f);
		g.vertex( width/2,  height/2);
		g.textureCoords(0, 0f, 1f);
		g.vertex(-width/2,  height/2);
		g.endShape();
		_myNoiseShader.end();
	}
	
	
	
	/**
	 * main, just calls things in the appropriate order
	 */

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCGPUSimplexNoiseFragmentTest.class);
		myManager.settings().size(1000, 1000);
		myManager.start();
	}
}
