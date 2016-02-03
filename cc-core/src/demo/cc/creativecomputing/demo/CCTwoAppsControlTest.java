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
import cc.creativecomputing.CCApplicationSettings.CCCloseOperation;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCKeyEvent;

/**
 * This demonstrates how to open one app from another app and set the close operation to hide.
 * @author christianriekoff
 *
 */
public class CCTwoAppsControlTest extends CCApp {
	
	public static class CCInnerApp extends CCApp{
		
		
		@Override
		public void setup() {
		}
		
		@Override
		public void draw() {
			g.clear();
		}
	}
	
	private CCInnerApp _myInnerApp;
	
	@CCControl(name = "back", min = 0, max = 1)
	private float _cBack = 0;

	@Override
	public void setup() {

		
		
		CCApplicationManager myManager = new CCApplicationManager(CCInnerApp.class);
		myManager.settings().size(500, 500);
		myManager.settings().closeOperation(CCCloseOperation.HIDE_ON_CLOSE);
		myManager.settings().location(600,200);
		myManager.start();
		
		_myInnerApp = (CCInnerApp)myManager.app();
		_myInnerApp.addControls("app", "app", this);
//		frameRate(1);
	}

	@Override
	public void update(final float theDeltaTime) {
	}

	@Override
	public void draw() {
		System.out.println(frameRate);
		g.clearColor(_cBack);
		g.clear();
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.CCApp#keyPressed(cc.creativecomputing.events.CCKeyEvent)
	 */
	@Override
	public void keyPressed(CCKeyEvent theKeyEvent) {
		switch(theKeyEvent.keyCode()) {
		case VK_S:
			_myInnerApp.show();
			break;
		default:
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCTwoAppsControlTest.class);
		myManager.settings().size(500, 500);
		myManager.settings().location(100,200);
		myManager.start();
	}
}

