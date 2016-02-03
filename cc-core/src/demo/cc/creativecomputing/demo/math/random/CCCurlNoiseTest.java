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
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.random.CCCurlNoise;
import cc.creativecomputing.math.util.CCArcball;

public class CCCurlNoiseTest extends CCApp {

	private CCCurlNoise _myCurlNoise;
	private CCArcball _myArcball;
	
	@Override
	public void setup() {
		_myCurlNoise = new CCCurlNoise();
		_myArcball = new CCArcball(this);
	}

	float _myTime = 0;
	
	@Override
	public void update(final float theDeltaTime) {
		_myTime+= theDeltaTime;
	}

	@Override
	public void draw() {
		g.clear();
		_myArcball.draw(g);
		for(int x = -width/2; x < width/2; x+=15) {
			for(int y = -height/2; y < height/2; y+=15) {
				CCVector3f myCurlNoise = _myCurlNoise.noise(x * 0.002f, y * 0.002f, _myTime * 0.05f).scale(25);
				g.line(x,y,0, x + myCurlNoise.x, y + myCurlNoise.y, myCurlNoise.z);
			}
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCCurlNoiseTest.class);
		myManager.settings().size(1400, 800);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

