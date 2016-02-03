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
import cc.creativecomputing.input.CCInputButtonListener;
import cc.creativecomputing.input.CCInputDevice;
import cc.creativecomputing.input.CCInputIO;
import cc.creativecomputing.input.CCInputStick;

public class CCInputTest extends CCApp {

	private CCInputStick _myStick1;
	private CCInputStick _myStick2;

	public void setup() {
		// init CCInput
		CCInputIO myInputIO = new CCInputIO(this);

		// print out devices to get your device name and see if the
		// devices are proper installed
		myInputIO.printDevices();

		// get the input device
		// you can do that by name or by id
		CCInputDevice myJoypad = myInputIO.device("Controller");
		
		myJoypad.printDeviceInfo();

		// add a listener to the second button
		// to change color
		myJoypad.button(1).addListener(new CCInputButtonListener() {

			public void onRelease() {
				g.color(255);
			}

			public void onPress() {
				g.color(255, 0, 0);
			}
		});

		// get the first stick of the joypad for translation
		_myStick1 = myJoypad.stick(0);

		// translate by 100 pixels per second
		_myStick1.multiplier(100);

		// get the second stick for rotation
		_myStick2 = myJoypad.stick(1);

		// set tolerance to ignore to small values
		_myStick2.tolerance(0.06f);

		// rotate by 90 degrees per second
		_myStick2.multiplier(90f);
	}

	public void draw() {
		g.clear();

		// apply translation
		g.translate(_myStick1.totalX(), -_myStick1.totalY(), 0);

		// apply rotation
		g.rotateX(_myStick2.totalY());
		g.rotateY(_myStick2.totalX());
		g.box(200);
	}

	static public void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCInputTest.class);
		myManager.start();
	}
}
