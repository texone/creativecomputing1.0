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
import cc.creativecomputing.graphics.CCShapeMode;
import cc.creativecomputing.input.CCInputButton;
import cc.creativecomputing.input.CCInputButtonListener;
import cc.creativecomputing.input.CCInputDevice;
import cc.creativecomputing.input.CCInputIO;
import cc.creativecomputing.input.CCInputStick;
import cc.creativecomputing.math.CCMath;

public class CCInputStickDemo extends CCApp {

	private CCInputDevice _myJoypad;
	private CCInputStick _myStick;
	private CCInputButton _myButton;

	public void setup() {

		CCInputIO myInputIO = new CCInputIO(this);

		_myJoypad = myInputIO.device("Controller");
		_myJoypad.button(1).addListener(new CCInputButtonListener() {

			public void onRelease() {
				g.color(255);
			}

			public void onPress() {
				g.color(255, 0, 0);
			}
		});

		_myJoypad.printSticks();
		_myJoypad.tolerance(0.05f);

		_myStick = _myJoypad.stick(0);

		_myButton = _myJoypad.button(0);

		g.rectMode(CCShapeMode.CENTER);
		g.clearColor(255);
	}

	private float _myTotalX = 0;
	private float _myTotalY = 0;

	public void draw() {
		g.clear();
		if (_myButton.pressed()) {
			g.color(255, 0, 0);
		} else {
			g.color(0);
		}

		_myTotalX = CCMath.constrain(_myTotalX + _myStick.x(), -width/2 + 10, width/2 - 10);
		_myTotalY = CCMath.constrain(_myTotalY - _myStick.y(), -height/2 + 10, height/2 - 10);

		g.rect(_myTotalX, _myTotalY, 20, 20);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCInputStickDemo.class);
		myManager.settings().size(600, 600);
		myManager.start();
	}
}
