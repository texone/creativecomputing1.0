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
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.math.signal.CCPerlinNoise;

public class CCNoiseDeravative2DTest extends CCApp {
	
	private CCPerlinNoise _myNoise;
	
	@CCControl(name = "scale", min = 0, max = 0.01f)
	private float _cNoiseScale = 0;
	
	@CCControl(name = "gain", min = 0, max = 1f)
	private float _cNoiseGain = 0;
	
	@CCControl(name = "bands", min = 1, max = 10f)
	private float _cNoiseBands = 0;
	
	@CCControl(name = "lacunarity", min = 0, max = 10f)
	private float _cLacunarity = 0;
	
	@CCControl(name = "space", min = 1, max = 10f)
	private float _cSpace = 1;
	
	@CCControl(name = "length", min = 1, max = 200f)
	private float _cLength = 1;
	
	@CCControl(name = "alpha", min = 0, max = 1f)
	private float _cAlpha = 1;
	
	public void setup() {
		_myNoise = new CCPerlinNoise();
		
		addControls("app", "app", this);
	}
	
	float _myTime = 0;
	
	@Override
	public void update(float theDeltaTime) {
		_myNoise.scale(_cNoiseScale);
		_myNoise.bands(_cNoiseBands);
		_myNoise.gain(_cNoiseGain);
		_myNoise.lacunarity(_cLacunarity);
		_myTime += theDeltaTime;
	}

	public void draw() {
		g.clearColor(255);
		g.clear();
		g.smooth();
		g.blend(CCBlendMode.BLEND);
		g.color(0f,_cAlpha);
		g.beginShape(CCDrawMode.LINES);
		for(float x = -width/2; x <= width/2;x+=_cSpace){
			for(float y = -height/2; y <= height/2;y+=_cSpace) {
				g.vertex(x,y);
				float[] myDNoise = _myNoise.values(x + width/2,y + height/2,(int)_myTime / 3 * 100);
				g.vertex(x + (myDNoise[1] - 0.5f) * _cLength,y + (myDNoise[2] - 0.5f) * _cLength);
			}
		}
		g.endShape();
		
	}

	public static void main(String[] args) {
		final CCApplicationManager myManager = new CCApplicationManager(CCNoiseDeravative2DTest.class);
		myManager.settings().size(1680, 500);
		myManager.settings().antialiasing(8);
		myManager.settings().background(new CCColor(255));
//		myManager.settings().displayMode(CCDisplayMode.FULLSCREEN);
		myManager.start();
	}
}
