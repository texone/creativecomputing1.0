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
package cc.creativecomputing.demo.topic.color;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCColor;

public class CCColorHSB extends CCApp {
	
	@CCControl(name = "hue", min = 0, max = 1)
	private float _cHue;
	
	@CCControl(name = "saturation", min = 0, max = 1)
	private float _cSaturation;
	
	@CCControl(name = "brightness", min = 0, max = 1)
	private float _cBrightness;
	
	private CCColor _myColor;

	@Override
	public void setup() {
		_myColor = new CCColor();
		addControls("app", "app", this);
	}

	@Override
	public void update(final float theDeltaTime) {
		_myColor.setHSB(_cHue, _cSaturation, _cBrightness);
	}

	@Override
	public void draw() {
		g.clearColor(_myColor);
		g.clear();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCColorHSB.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}

