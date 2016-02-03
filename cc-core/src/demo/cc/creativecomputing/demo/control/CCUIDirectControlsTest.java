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
import cc.creativecomputing.control.CCFloatControl;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.util.logging.CCLog;

public class CCUIDirectControlsTest extends CCApp {
	
	private static class CCControls{
		@CCControl (name = "vec", height = 100)
		private CCVector2f vector2f = new CCVector2f();
		
		@CCControl (name = "enum")
		private CCBlendMode blendMode;
		
		@CCControl (name = "number", min = 0, max = 1)
		private float number = 0.5f;
		
		@CCControl (name = "inter", min = 0, max = 10)
		private int inter = 5;
		
		@CCControl (name = "boolean")
		private boolean booler = false;
	}
	
	@CCControl(name = "controls")
	private CCControls _myControl = new CCControls();
	
	private CCFloatControl _myFloatControl = new CCFloatControl("test1", 2f, 0f, 10f, 100f, 17f);

	@Override
	public void setup() {
		addControls("app", "app", this);
		
		_myFloatControl.min(-1);
		_myFloatControl.max(20);
	}

	@Override
	public void update(final float theDeltaTime) {
		CCLog.info(_myFloatControl.value());
	
	}

	@Override
	public void draw() {
		g.clearColor(_myControl.number);
		g.clear();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCUIDirectControlsTest.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}

