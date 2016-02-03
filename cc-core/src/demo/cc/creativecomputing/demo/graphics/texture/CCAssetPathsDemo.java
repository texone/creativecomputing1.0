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
import cc.creativecomputing.graphics.texture.CCTextureIO;

public class CCAssetPathsDemo extends CCApp {
	
	private CCTexture2D _myTexture;

	@Override
	public void setup() {
		_myTexture = new CCTexture2D(CCTextureIO.newTextureData("mic seilwinde.jpg"));
		
		addControls("app", "app", this);
	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {
		g.clear();
		
		g.image(_myTexture, -_myTexture.width()/2,-_myTexture.height()/2, _myTexture.width(),_myTexture.height());
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCAssetPathsDemo.class);
		myManager.settings().assetPaths("/Users/christianr/Downloads;/Users/christianr/");
		myManager.settings().size(1500, 500);
		myManager.start();
	}
}

