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
package cc.creativecomputing.demo.graphics.shader.tutorial;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;

public class CCCGDynamicArraysDemo extends CCApp {

	@Override
	public void setup() {

	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {

	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCCGDynamicArraysDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}

