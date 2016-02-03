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
package cc.creativecomputing.demo.graphics;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.util.logging.CCLog;

public class CCInfoTest extends CCApp {

	@Override
	public void setup() {
		CCLog.info("VENDOR:"+g.vendor());
		CCLog.info("RENDERER:"+g.renderer());
		CCLog.info("VERSION:"+g.version());
		
		for(String myExtension:g.extensions()){
			CCLog.info(myExtension);
		}
	}

	@Override
	public void draw() {
	}

	public static void main(String[] args) {
		final CCApplicationManager myManager = new CCApplicationManager(CCInfoTest.class);
		myManager.start();
	}
}
