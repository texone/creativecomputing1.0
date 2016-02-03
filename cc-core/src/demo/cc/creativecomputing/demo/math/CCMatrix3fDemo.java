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
import cc.creativecomputing.math.CCMatrix32f;
import cc.creativecomputing.math.CCVector2f;

public class CCMatrix3fDemo extends CCApp {
	
	private CCMatrix32f _myMatrix;

	@Override
	public void setup() {
		_myMatrix = new CCMatrix32f();
		_myMatrix.translate(100,100);
		_myMatrix.scale(200);
	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {
		g.clear();
		
		CCVector2f myPosition = new CCVector2f();
		myPosition = _myMatrix.transform(myPosition);
		
		g.ellipse(myPosition, 50);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCMatrix3fDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}

