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
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.util.CCGPUNoise;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.util.CCArcball;

import com.jogamp.opengl.cg.CGparameter;

/**
 * This test demonstrates the basic use of the noise shader extension
 * @author info
 *
 */
public class CCGPUNoiseBackDemo extends CCApp {
	
	private CCCGShader _myNoiseShader;
	
	private CGparameter _myNoiseScaleParameter;
	private CGparameter _myNoiseZParameter;
	private CGparameter _myNoisePowParameter;
	
	private float _myNoiseZ = 0;
	
	private CCArcball _myArcball;
	private CCVBOMesh _myMesh;
	
	@CCControl(name = "noise scale", min = 0, max = 0.01f)
	public float _cNoiseScale;

	@CCControl(name = "noise pow", min = 0.1f, max = 4f)
	public float _cNoisePow;
	
	public void setup() {
		g.clearColor(0);
		g.noBlend();
		
		addControls("app", "app", this);
		
		_myNoiseShader = new CCCGShader(
			CCIOUtil.classPath(this,"noiseback.vp"),
			CCIOUtil.classPath(this,"noiseback.fp")
		);
		_myNoiseShader.load();
		
		_myNoiseScaleParameter = _myNoiseShader.vertexParameter("noiseScale");
		_myNoiseZParameter = _myNoiseShader.vertexParameter("noiseZ");
		_myNoisePowParameter = _myNoiseShader.vertexParameter("noisePow");
		CCGPUNoise.attachVertexNoise(_myNoiseShader);
		
		_myArcball = new CCArcball(this);
		_myMesh = new CCVBOMesh(CCDrawMode.QUADS,400 * 400 * 4);
		
		for(float x = -400; x < 400; x +=4){
			for(float y = -400; y < 400; y +=4){
				_myMesh.addVertex(x, y, 0);
				_myMesh.addVertex(x+3,y,0);
				_myMesh.addVertex(x+3,y+3,0);
				_myMesh.addVertex(x,y+3,0);
			}
		}
	}
	
	public void update(final float theDeltaTime){
		_myNoiseZ += theDeltaTime * 0.1f;
	}

	public void draw() {
		g.clear();
		_myArcball.draw(g);
		_myNoiseShader.start();
		_myNoiseShader.parameter(_myNoiseScaleParameter, _cNoiseScale);
		_myNoiseShader.parameter(_myNoisePowParameter, _cNoisePow);
		_myNoiseShader.parameter(_myNoiseZParameter, _myNoiseZ);
		_myMesh.draw(g);
		_myNoiseShader.end();
	}
	
	
	
	/**
	 * main, just calls things in the appropriate order
	 */

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCGPUNoiseBackDemo.class);
		myManager.settings().size(800, 800);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
