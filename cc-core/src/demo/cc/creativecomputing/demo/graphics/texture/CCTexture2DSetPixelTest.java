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
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.math.CCMath;

public class CCTexture2DSetPixelTest extends CCApp {
	
	private CCTexture2D _myTexture;

	@Override
	public void setup() {
		_myTexture = new CCTexture2D(100,100);
		for(int x = 0; x < 100;x++) {
			for(int y = 0; y < 100;y++) {
				_myTexture.setPixel(x,y, new CCColor(CCMath.random(),CCMath.random(),CCMath.random()));
			}
		}
	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {
		g.clear();
		g.image(_myTexture,0,0,200,200);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCTexture2DSetPixelTest.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}

