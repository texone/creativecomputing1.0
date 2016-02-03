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

import com.jogamp.opengl.util.Gamma;

public class CCGammaDemo extends CCApp {

	@CCControl(name = "gamma", min = 1, max = 10)
	private float _cGamma = 1;

	@CCControl(name = "brightness", min = -1, max = 1)
	private float _cBrightness = 0;

	@CCControl(name = "contrast", min = 0, max = 2)
	private float _cContrast = 1;
	
	@Override
	public void setup() {
		addControls("app", "gamma", this);
	}

	@Override
	public void update(final float theDeltaTime) {
		CCLog.info("_cGamma:"+_cGamma);
		CCLog.info("_cBrightness:"+_cBrightness);
		CCLog.info("_cContrast:"+_cContrast);
		Gamma.setDisplayGamma(g.gl, _cGamma, _cBrightness, _cContrast);
	}

	@Override
	public void draw() {

	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCGammaDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}

