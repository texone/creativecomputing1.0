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
package cc.creativecomputing.demo.graphics.util;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCTesselator;
import cc.creativecomputing.math.CCMath;

public class CCTesselatorTest extends CCApp {
	
	private void drawStar(float theSteps, float theMinRadius, float theMaxRadius) {
		// Feed in the list of vertices
		for (int i = 0; i < theSteps * 2; i+=2) {
			float angle = i/(theSteps * 2) * CCMath.TWO_PI;
			float x = CCMath.sin(angle);
			float y = CCMath.cos(angle);
			_myTesselator.color(x/2 + 0.5f, y/2 +0.5f, 0, 1f);
			_myTesselator.vertex(x * theMaxRadius, y * theMaxRadius, 0);

			angle = (i+1)/(theSteps * 2) * CCMath.TWO_PI;
			x = CCMath.sin(angle);
			y = CCMath.cos(angle);
			_myTesselator.color(x/2 + 0.5f, y/2 +0.5f,0, 1f);
			_myTesselator.vertex(x * theMinRadius, y * theMinRadius, 0);
		}
	}

	private CCTesselator _myTesselator;

	public void setup() {
		_myTesselator = new CCTesselator();
		g.clearColor(0);
	}

	public void draw() {
		g.clear();
//		g.polygonMode(CCPolygonMode.LINE);
		
		// Green polygon
		g.color(0.0f, 1.0f, 0.0f);

		// How to count filled and open areas

		// Begin the polygon
		_myTesselator.beginPolygon();

		// First contour
		_myTesselator.beginContour();

		// Feed in the list of vertices
		drawStar(30,150,200);
//		_myTesselator.color(1f, 0, 0, 1f);
//		_myTesselator.vertex(-100, -100);
//		
//		_myTesselator.color(0, 1, 0, 1f);
//		_myTesselator.vertex( 100, -100);
//		
//		_myTesselator.color(0, 0, 1, 1f);
//		_myTesselator.vertex( 100,  100);
//		
//		_myTesselator.color(0, 1, 1, 1f);
//		_myTesselator.vertex( 0,  100);
//		
//		_myTesselator.color(0, 0, 0, 1f);
//		_myTesselator.vertex(-100,  100);
		_myTesselator.endContour();

		// Second contour
		_myTesselator.beginContour();
		drawStar(31,50,100);

		// All done with polygon
		_myTesselator.endPolygon();
		
		g.beginShape(CCDrawMode.TRIANGLES);
		g.endShape();
	}

	public static void main(String[] args) {
		final CCApplicationManager myManager = new CCApplicationManager(CCTesselatorTest.class);
		myManager.settings().size(400, 400);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
