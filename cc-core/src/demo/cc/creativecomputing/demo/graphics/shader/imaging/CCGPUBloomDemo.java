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
package cc.creativecomputing.demo.graphics.shader.imaging;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.shader.imaging.CCGPUBloom;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureIO;

public class CCGPUBloomDemo extends CCApp {
	
	private CCGPUBloom _myBloom;
	private CCTexture2D _myTexture;

	@Override
	public void setup() {
		_myTexture = new CCTexture2D(CCTextureIO.newTextureData("demo/gpu/imaging/test1.jpg"));
		_myBloom = new CCGPUBloom(this);
		addControls("bloom", "bloom", _myBloom);
	}

	@Override
	public void update(final float theDeltaTime) {
	}

	@Override
	public void draw() {
		g.clear();
		_myBloom.start();
		g.image(_myTexture, -width/2, -height/2);
		_myBloom.end();

		g.image(_myTexture, 0, -height/2);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(
				CCGPUBloomDemo.class);
		myManager.settings().size(1000, 500);
		myManager.start();
	}
}

