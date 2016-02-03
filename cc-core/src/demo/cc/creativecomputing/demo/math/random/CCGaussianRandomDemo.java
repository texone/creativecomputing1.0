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
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.math.CCMath;

public class CCGaussianRandomDemo extends CCApp {

	@Override
	public void setup() {

	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {
		g.clear();
		CCMath.randomSeed(0);
		g.color(255,50);
		g.blend(CCBlendMode.ADD);
		g.beginShape(CCDrawMode.POINTS);
		for(int i = 0; i < 100000;i++) {
			float x = CCMath.gaussianRandom(-width/2, width/2);
			float y = CCMath.gaussianRandom(-height/2, height/2);
			g.vertex(x,y);
		}
		g.endShape();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCGaussianRandomDemo.class);
		myManager.settings().size(500, 500);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

