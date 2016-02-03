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
package cc.creativecomputing.demo.app.watchdog;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;

public class CCWatchdogTestApp extends CCApp {

	@Override
	public void setup() {

	}

	@Override
	public void update(final float theDeltaTime) {
	}

	@Override
	public void draw() {
		System.out.println("-frameRate " + frameRate);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCWatchdogTestApp.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}

