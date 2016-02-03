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
package cc.creativecomputing.demo.control;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.util.logging.CCLog;

public class CCUIFloatMethodTest extends CCApp {

	@Override
	public void setup() {
		addControls("test","test", this);
	}
	
	@CCControl(name = "print", min=0, max = 10)
	public void printValue(final float theValue){
		CCLog.info(theValue);
	}

	public void draw() {

	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCUIFloatMethodTest.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}
