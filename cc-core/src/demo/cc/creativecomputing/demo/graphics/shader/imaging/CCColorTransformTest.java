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
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.control.CCControlUI;
import cc.creativecomputing.graphics.shader.imaging.CCGPUColorTransform;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureIO;

public class CCColorTransformTest extends CCApp {
	
	public static class ColorSettings{
		@CCControl(name="contrast", min = -1, max = 1)
		public static float contrast = 0;

		@CCControl(name="brightness", min = -1, max = 1)
		public static float brightness = 0;
	}
	
	private CCTexture2D _myTexture;
	private CCGPUColorTransform _myColorFilter;
	private CCControlUI _myUI;

	public void setup() {
		_myTexture = new CCTexture2D(CCTextureIO.newTextureData("demo/gpu/imaging/test1.jpg"));
		_myColorFilter = new CCGPUColorTransform(g);
		
		_myUI = new CCControlUI(this);
		_myUI.addControls("color settings", new ColorSettings());
	}
	
	public void update(final float theDeltaTime) {
		_myColorFilter.reset();
		_myColorFilter.contrast(ColorSettings.contrast);
		_myColorFilter.brightness(ColorSettings.brightness);
	}

	public void draw() {
		g.clear();
		_myColorFilter.start();
		g.image(_myTexture, -width/2, -height/2);
		_myColorFilter.end();

		g.image(_myTexture, 0, -height/2);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCColorTransformTest.class);
		myManager.settings().size(1000, 500);
		myManager.start();
	}
}
