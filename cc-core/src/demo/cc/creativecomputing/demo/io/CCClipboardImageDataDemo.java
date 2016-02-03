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
package cc.creativecomputing.demo.io;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.events.CCKeyEvent.CCKeyCode;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCClipboard;

public class CCClipboardImageDataDemo extends CCApp {
	
	CCClipboard _myClipboard;
	CCTexture2D _myTexture;

	@Override
	public void setup() {
		_myClipboard = CCClipboard.instance();
		_myTexture = new CCTexture2D(100, 100);
	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {
		g.clear();
		g.image(_myTexture, 0,0);
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.CCApp#keyPressed(cc.creativecomputing.events.CCKeyEvent)
	 */
	@Override
	public void keyPressed(CCKeyEvent theKeyEvent) {
		if((theKeyEvent.isMetaDown() || theKeyEvent.isControlDown()) && theKeyEvent.keyCode() == CCKeyCode.VK_V) {
			_myTexture.data(_myClipboard.getTextureData());
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCClipboardImageDataDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}

