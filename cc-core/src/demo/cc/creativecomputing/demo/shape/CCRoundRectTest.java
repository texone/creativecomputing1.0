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
package cc.creativecomputing.demo.shape;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCShapeMode;
import cc.creativecomputing.graphics.shape.CCRoundedRectangle;

public class CCRoundRectTest extends CCApp {
	
	private CCRoundedRectangle _myRoundedRectangle;

	@Override
	public void setup() {
		_myRoundedRectangle = new CCRoundedRectangle(0,0,200,200,50, new CCColor(255), new CCColor(255,0));
		_myRoundedRectangle.shapeMode(CCShapeMode.CENTER);
	}

	@Override
	public void update(final float theDeltaTime) {
		_myRoundedRectangle.scale(mouseX / (float)width);
	}
	
	

	@Override
	public void draw() {
		g.clear();
		_myRoundedRectangle.draw(g);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCRoundRectTest.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}

