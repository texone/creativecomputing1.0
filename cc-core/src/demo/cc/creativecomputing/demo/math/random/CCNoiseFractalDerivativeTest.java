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
package cc.creativecomputing.demo.math.random;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.math.signal.CCPerlinNoise;

public class CCNoiseFractalDerivativeTest extends CCApp {
	
	private CCPerlinNoise _myNoise;
	
	@CCControl(name = "scale", min = 0, max = 1f)
	private float _cNoiseScale = 0;
	
	@CCControl(name = "gain", min = 0, max = 1f)
	private float _cNoiseGain = 0;
	
	@CCControl(name = "bands", min = 1, max = 10f)
	private float _cNoiseBands = 0;
	
	@CCControl(name = "lacunarity", min = 0, max = 10f)
	private float _cLacunarity = 0;
	
	public void setup() {
		_myNoise = new CCPerlinNoise();
		
		addControls("app", "app", this);
	}
	
	private float fractalSum(float theX, float theY) {
		float f = 0.0f;
		float w = 0.5f;
		float dx = 0.0f;
		float dz = 0.0f;
		
		for (int i = 0; i < 4; i++) {
			float[] myNoiseV = _myNoise.signalImpl(theX, theY);
			dx += myNoiseV[1];
			dz += myNoiseV[2];
			f += w * myNoiseV[0] / (1.0f + dx * dx + dz * dz); // replace with "w * n[0]" for a classic fbm()
			w *= 0.5f;
			theX *= 2.0f;
			theY *= 2.0f;
		}
		return f;
	}
	
	@Override
	public void update(float theDeltaTime) {
		_myNoise.scale(_cNoiseScale);
		_myNoise.bands(_cNoiseBands);
		_myNoise.gain(_cNoiseGain);
		_myNoise.lacunarity(_cLacunarity);
	}

	public void draw() {
		g.clear();
		
		g.color(255,0,0);
		g.beginShape(CCDrawMode.LINE_STRIP);
		for(float x = -width/2; x <= width/2;x++){
			float y = fractalSum((x + width/2) * _cNoiseScale,0) * height - height/2;
			g.vertex(x,y);
		}
		g.endShape();
		
	}

	public static void main(String[] args) {
		final CCApplicationManager myManager = new CCApplicationManager(CCNoiseFractalDerivativeTest.class);
		myManager.settings().size(1400, 400);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
