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
import cc.creativecomputing.math.signal.CCSimplexNoise;

public class CCNoiseCurveDemo extends CCApp {
	
	private CCSimplexNoise _myNoise;
	
	@CCControl(name = "amount", min = 0, max = 300)
	private float _cAmpunt = 0;
	
	@CCControl(name = "speed x", min = 0, max = 100)
	private float _cSppedX = 0;
	
	private float _myOffset = 0;
	
	@CCControl(name = "speed y", min = 0, max = 100)
	private float _cSppedY = 0;
	
	private float _myOffsetY = 0;

	@Override
	public void setup() {
		_myNoise = new CCSimplexNoise();
		
		addControls("app", "app", _myNoise);
		addControls("app", "app2", this);
	}

	@Override
	public void update(final float theDeltaTime) {
		_myOffset += _cSppedX * theDeltaTime;
		_myOffsetY += _cSppedY * theDeltaTime;
	}

	@Override
	public void draw() {
		g.clear();
		System.out.println(_myOffset);
		g.beginShape(CCDrawMode.POINTS);
		for(float x = -width;x < width/2;x++){
			g.vertex(x,_myNoise.value(x + _myOffset, _myOffsetY) * _cAmpunt);
		}
		g.endShape();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCNoiseCurveDemo.class);
		myManager.settings().size(1500, 500);
		myManager.start();
	}
}

