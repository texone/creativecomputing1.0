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
import cc.creativecomputing.CCAppContextShared;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.CCApplicationSettings.CCCloseOperation;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureIO;

/**
 * This demonstrates how to open one app from another app and set the close operation to hide.
 * @author christianriekoff
 *
 */
public class CCTwoAppsTest extends CCApp {
	
	public static class CCInnerApp extends CCApp{
		private CCTexture2D _myTexture;
		
		public void texture(CCTexture2D theTexture) {
			_myTexture = theTexture;
		}
		
		@Override
		public void setup() {
		}
		
		@Override
		public void draw() {
			System.out.println("DRAW");
			g.clear();
			g.image(_myTexture, -_myTexture.width() / 2, -_myTexture.height() / 2);
		}
	}
	
	private CCInnerApp _myInnerApp;
	
	private CCTexture2D _myTexture;

	@Override
	public void setup() {

		_myTexture = new CCTexture2D(CCTextureIO.newTextureData("textures/waltz.png"));
		
		
		CCApplicationManager myManager = new CCApplicationManager(CCInnerApp.class);
		myManager.settings().size(500, 500);
		myManager.settings().closeOperation(CCCloseOperation.HIDE_ON_CLOSE);
		myManager.settings().location(600,200);
		myManager.settings().appContext(sharedContext);
		myManager.start();
		
		_myInnerApp = (CCInnerApp)myManager.app();
		_myInnerApp.texture(_myTexture);
//		frameRate(1);
	}

	@Override
	public void update(final float theDeltaTime) {
	}

	@Override
	public void draw() {
		System.out.println(frameRate);
		
		g.clear();
		g.image(_myTexture, -_myTexture.width() / 2, -_myTexture.height() / 2);
	}
	
	@Override
	public void keyPressed(CCKeyEvent theKeyEvent) {
		switch(theKeyEvent.keyCode()) {
		case VK_S:
			_myInnerApp.show();
			break;
		default:
		}
	}
	
	private static CCAppContextShared sharedContext = new CCAppContextShared();

	public static void main(String[] args) {
		sharedContext = new CCAppContextShared();
		CCApplicationManager myManager = new CCApplicationManager(CCTwoAppsTest.class);
		myManager.settings().size(500, 500);
		myManager.settings().location(100,200);
		myManager.settings().appContext(sharedContext);
		myManager.start();
	}
}

