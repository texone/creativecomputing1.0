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
package cc.creativecomputing.demo.graphics.texture;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.graphics.texture.CCTextureIO;

public class CCTextureDataGetPixelDemo extends CCApp {
	
	private CCTextureData _myData;
	private CCTexture2D _myTexture;

	@Override
	public void setup() {
		_myData = CCTextureIO.newTextureData("demo/textures/testAlpha.png");
		
		_myTexture = new CCTexture2D(_myData);
	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {
		g.noBlend();
		g.clearColor(255,0,0);
		g.clear();
		g.image(_myTexture, 0,0);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCTextureDataGetPixelDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}

