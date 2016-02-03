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

public class CCUIBooleanMethodTest extends CCApp {

	@Override
	public void setup() {
		addControls("test", "test", this);
	}
	
	@CCControl(name = "print toggle", toggle = true)
	public void printValue(final boolean theValue){
		CCLog.info(theValue);
	}
	
	@CCControl(name = "print bang")
	public void printValue(){
		CCLog.info("bang");
	}

	public void draw() {
		g.clear();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCUIBooleanMethodTest.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}
