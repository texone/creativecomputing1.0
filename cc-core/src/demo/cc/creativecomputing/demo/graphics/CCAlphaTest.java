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
package cc.creativecomputing.demo.graphics;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;

public class CCAlphaTest extends CCApp {

	@Override
	public void setup() {

	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {
		g.clear();
		g.blend(CCBlendMode.REPLACE);
		g.color(255, 100);
		
		g.beginShape(CCDrawMode.QUADS);
		g.vertex(0,0);
		g.vertex(200,0);
		g.vertex(200,200);
		g.vertex(0,200);
		g.endShape();
		
		g.beginShape(CCDrawMode.QUADS);
		g.vertex(50,50);
		g.vertex(250,50);
		g.vertex(250,250);
		g.vertex(50,250);
		g.endShape();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCAlphaTest.class);
		myManager.settings().size(800, 800);
		myManager.start();
	}
}

