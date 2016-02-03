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
import cc.creativecomputing.CCApplicationSettings.CCDisplayMode;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.util.logging.CCLog;

public class CCAppUpdateModeTest extends CCApp{

	@CCControl(name = "val", min = 0, max = 100)
	float _myVal = 0;

	@Override
	public void setup() {
		addControls("app", "app", this);
		CCLog.info(_myVal);
	}
	
	@Override
	public void update(float theDeltaTime) {
		CCLog.info("UPDATE:" + _myVal);
	}
	
	@Override
	public void draw() {
		g.clear();
		
		g.line(mouseX,mouseY,100,100);
	}
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCAppUpdateModeTest.class);
		myManager.settings().displayMode(CCDisplayMode.UPDATE_ONLY);
		myManager.settings().size(400, 400);
		myManager.start();
	}

}
