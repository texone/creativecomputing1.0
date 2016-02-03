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
package cc.creativecomputing.demo.math;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.d.CCTriangle2d;
import cc.creativecomputing.math.d.CCVector2d;

public class CCBaryCentricDemo extends CCApp {
	
	private CCTriangle2d _myTriangle1;
	private CCTriangle2d _myTriangle2;

	@Override
	public void setup() {
		_myTriangle1 = new CCTriangle2d(
			new CCVector2d(0, CCMath.random(height)),
			new CCVector2d(width/4, CCMath.random(height)),
			new CCVector2d(width/2, CCMath.random(height))
		);
		

		_myTriangle2 = new CCTriangle2d(
			new CCVector2d(width/2, CCMath.random(height)),
			new CCVector2d(width * 0.75, CCMath.random(height)),
			new CCVector2d(width, CCMath.random(height))
		);
	}

	@Override
	public void update(final float theDeltaTime) {
	}

	@Override
	public void draw() {
		g.clear();
		g.beginOrtho();
		
		g.beginShape(CCDrawMode.TRIANGLES);
		g.color(255,0,0);
		g.vertex(_myTriangle1.a());
		g.vertex(_myTriangle1.b());
		g.vertex(_myTriangle1.c());
		g.color(0,255,0);
		g.vertex(_myTriangle2.a());
		g.vertex(_myTriangle2.b());
		g.vertex(_myTriangle2.c());
		g.endShape();
		
		CCVector2d myBaryCoords = _myTriangle1.toBarycentricCoordinates(new CCVector2d(mouseX, mouseY));
		CCVector2d myTriangleCoords = _myTriangle2.toTriangleCoordinates(myBaryCoords);
		
		g.color(255);
		g.ellipse(mouseX, mouseY, 5);
		
		g.color(255);
		g.ellipse((float)myTriangleCoords.x, (float)myTriangleCoords.y, 5);
		
		g.endOrtho();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCBaryCentricDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}

