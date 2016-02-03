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
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.util.logging.CCLog;

public class CCUpdateDemo extends CCApp {
	
	@CCControl(name = "val", min = 0, max = 100)
	float _myVal = 0;

	@Override
	public void setup() {
		addControls("app", "app", this);
		CCLog.info(_myVal);
	}
	
	long _myTimeInMillis = 0;
	long _myLastMillis = 0;

	@Override
	public void update(final float theDeltaTime) {
		if(_myLastMillis == 0) {
			_myLastMillis = System.currentTimeMillis();
		}
		long myMillis = System.currentTimeMillis();
		
		_myTimeInMillis += myMillis - _myLastMillis;
		_myLastMillis = myMillis;
		
		CCLog.info(_myTimeInMillis - _myMillisSinceStart);
	}

	@Override
	public void draw() {

	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCUpdateDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}

