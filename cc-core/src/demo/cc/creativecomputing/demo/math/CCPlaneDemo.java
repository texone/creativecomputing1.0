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
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.math.CCPlane3f;
import cc.creativecomputing.math.util.CCArcball;

public class CCPlaneDemo extends CCApp {

	@CCControl(name = "norm x", min = -1, max = 1)
	private static float _cNormX = 0;

	@CCControl(name = "norm y", min = -1, max = 1)
	private static float _cNormY = 0;

	@CCControl(name = "norm z", min = -1, max = 1)
	private static float _cNormZ = 0;

	@CCControl(name = "planeConstant", min = -400, max = 400)
	private static float _cPlaneConstant = 0;

	private CCPlane3f _myPlane;
	private CCArcball _myArcball;

	@Override
	public void setup() {
		addControls("app", "app", this);
		_myPlane = new CCPlane3f();
		_myArcball = new CCArcball(this);
	}

	@Override
	public void update(final float theDeltaTime) {
		_myPlane.normal().set(_cNormX, _cNormY, _cNormZ).normalize();
		_myPlane.constant(_cPlaneConstant);
	}

	@Override
	public void draw() {
		g.clear();
		_myArcball.draw(g);
		_myPlane.draw(g);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCPlaneDemo.class);
		myManager.settings().size(1200, 500);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
