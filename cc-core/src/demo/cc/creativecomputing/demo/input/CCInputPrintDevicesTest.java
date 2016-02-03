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
package cc.creativecomputing.demo.input;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.input.CCInputDevice;
import cc.creativecomputing.input.CCInputIO;
import cc.creativecomputing.util.logging.CCLog;

public class CCInputPrintDevicesTest extends CCApp {

	@Override
	public void setup() {
		CCInputIO myInputIO = new CCInputIO(this);
		
		myInputIO.printDevices();

		for (int i = 0; i < myInputIO.numberOfDevices(); i++) {
			CCInputDevice device = myInputIO.device(i);

			CCLog.info(device.name() + " has:");
			CCLog.info(" " + device.numberOfSliders() + " sliders");
			CCLog.info(" " + device.numberOfButtons() + " buttons");
			CCLog.info(" " + device.numberOfSticks() + " sticks");

			device.printSliders();
			device.printButtons();
			device.printSticks();
		}
	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {

	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCInputPrintDevicesTest.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}
