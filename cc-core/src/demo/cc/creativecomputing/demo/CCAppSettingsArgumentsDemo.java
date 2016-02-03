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
package cc.creativecomputing.demo;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.util.logging.CCLog;

public class CCAppSettingsArgumentsDemo extends CCApp {

	@Override
	public void setup() {
		CCLog.info(settings.getProperty("custom"));
	}

	@Override
	public void update(final float theDeltaTime) {
	}

	@Override
	public void draw() {
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCAppSettingsArgumentsDemo.class);
		String[] myArgs = new String[]{
			"-width", "300",	
			"-height", "700",
			"-undecorated",
			"-custom", "yoyo"
		};
		myManager.settings().addParameter("custom", true, "bla");
		myManager.settings().size(700,300);
		myManager.settings(myArgs);
		myManager.start();
	}
}

