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
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;

public class CCGPURandomDemo extends CCApp {
	
	private CCGLSLShader _myRandomShader;
	
	public void setup() {
		g.noBlend();
		_myRandomShader = new CCGLSLShader(
			CCIOUtil.classPath(this, "random_vertex.glsl"),
			CCIOUtil.classPath(this, "random_fragment.glsl")
		);
		_myRandomShader.load();
		
		addControls("noise", "noise", this);
	}
	
	float time = 0;
	CCVector3f _myOffset = new CCVector3f();
	CCVector3f _myOldOffset = new CCVector3f();
	CCVector3f _myNewOffset = new CCVector3f(10,10,2);
	
	float blend = 0;
	float max = 2;
	
	public void update(final float theDeltaTime){
		time += theDeltaTime;
		if(time > max){
			time -= max;
			_myOldOffset = _myNewOffset;
			_myNewOffset = CCVecMath.random(-10, 10, -10, 10, -2, 2);
		}
		blend = (CCMath.cos((time / max)*CCMath.PI + CCMath.PI)+1)/2;
		_myOffset = CCVecMath.blend(blend, _myOldOffset, _myNewOffset);
	}

	public void draw() {
		g.clear();
		_myRandomShader.start();
		_myRandomShader.uniform3f("randAdd", CCMath.random(100f), CCMath.random(100f),  CCMath.random(3000,10000));
//		_myNoiseShader.parameter(_myNoiseOffsetParameter, _myOffset);
		g.rect(-width/2, -height/2, width, height);
		_myRandomShader.end();
	}
	
	
	
	/**
	 * main, just calls things in the appropriate order
	 */

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCGPURandomDemo.class);
		myManager.settings().size(1000, 1000);
		myManager.settings().frameRate(30);
		myManager.start();
	}
}
