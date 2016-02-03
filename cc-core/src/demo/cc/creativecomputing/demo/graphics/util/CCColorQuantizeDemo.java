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
package cc.creativecomputing.demo.graphics.util;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;

public class CCColorQuantizeDemo extends CCApp {

	private CCTextureData _myTextureData;
	private CCColorQuantizer _myQuantizer;

	@Override
	public void setup() {
		_myTextureData = CCTextureIO.newTextureData(CCIOUtil.classPath(this, "wave.jpg"));
	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {
		g.clear();
		g.translate(-width / 2, -height / 2);
		int colorCount = CCMath.max(2, mouseX / 4);
		_myQuantizer = new CCColorQuantizer(colorCount, true);
		_myQuantizer.init(_myTextureData);

		g.beginShape(CCDrawMode.POINTS);
		for (int x = 0; x < _myTextureData.width(); x++) {
			for (int y = 0; y < _myTextureData.height(); y++) {
				CCColor myColor = _myQuantizer.convert(_myTextureData.getPixel(x, y));
				g.color(myColor.r, myColor.g, myColor.b);
				g.vertex(x, y);
			}
		}
		g.endShape();

		CCColor[] map = _myQuantizer.colorMap();
		int sz = width / map.length;
		for (int i = 0; i < map.length; i++) {
			g.color(map[i]);
			g.rect(i * sz, height - 16, (i + 1) * sz, height);
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCColorQuantizeDemo.class);
		myManager.settings().size(500, 338);
		myManager.start();
	}
}
