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
package cc.creativecomputing.demo.math.signal;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.signal.filter.CCFilterManager;
import cc.creativecomputing.math.signal.filter.CCFilterManager.CCFilteredValue;
import cc.creativecomputing.util.logging.CCLog;

public class CCFilterManagerDemo extends CCApp {

	@CCControl(name = "jitter", min = 0, max = 100)
	private float _cJitter = 0;

	@CCControl(name = "filter manager", tabName = "filter")
	private CCFilterManager _myFilterManager;
	
	private CCFilteredValue _myMouseXFilter;
	private CCFilteredValue _myMouseYFilter;
	
	
	private float _myMouseX;
	private float _myMouseY;

	public void setup() {
		g.textFont(CCFontIO.createTextureMapFont("arial", 12));
		_myFilterManager = new CCFilterManager(this);
		addControls("app", "app", this);
		
		_myMouseXFilter = _myFilterManager.createFilteredValue();
		_myMouseYFilter = _myFilterManager.createFilteredValue();
	}
	
	@Override
	public void update(float theDeltaTime) {
		_myFilterManager.update(theDeltaTime);
		_myMouseX = mouseX - width/2 + CCMath.random(-_cJitter, _cJitter);
		_myMouseY = height/2 - mouseY+ CCMath.random(-_cJitter, _cJitter);
		_myMouseXFilter.value(_myMouseX, theDeltaTime);
		_myMouseYFilter.value(_myMouseY, theDeltaTime);
	}

	public void draw() {
		g.clear();
		
		g.color(255);
		g.ellipse(_myMouseX, _myMouseY, 20);
		g.color(255,0,0);
		g.ellipse(_myMouseXFilter.value(), _myMouseYFilter.value(), 10);
	}

	public static void main(String[] args) {
		final CCApplicationManager myManager = new CCApplicationManager(CCFilterManagerDemo.class);
		myManager.settings().size(1800, 1000);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
