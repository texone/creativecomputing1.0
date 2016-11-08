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
package cc.creativecomputing.demo.topic.fractalfrh;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;

public class CCFractalCloudsDemo extends CCApp {
	@CCControl(name="clouds", tabName = "clouds", column = 0)
	private CCFractalClouds _myClouds;

	@Override
	public void setup() {
		_myClouds = new CCFractalClouds(g, width, height);
		addControls("app", "app", this);
	}

	@Override
	public void update(final float theDeltaTime) {
		_myClouds.time(theDeltaTime);
	}

	@Override
	public void draw() {
		g.clearColor(255,0,0);
		g.clear();
		g.blend();
		_myClouds.draw(g);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCFractalCloudsDemo.class);
		myManager.settings().antialiasing(8);
		myManager.settings().size(1200, 850);
//		myManager.settings().undecorated(true);
//		myManager.settings().location(0,0);
		myManager.start();
	}
}

