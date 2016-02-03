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
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureIO;

public class CCTextureFilterDemo extends CCApp {
	
	private CCTexture2D _myTexture;
	private CCTexture2D _myTexture2;
	
	@CCControl(name = "scale", min = 1, max = 100)
	private float _cScale = 1;

	@Override
	public void setup() {
		_myTexture = new CCTexture2D(CCTextureIO.newTextureData("demo/textures/pointgrid.png"));
		_myTexture.generateMipmaps(true);
		_myTexture2 = new CCTexture2D(CCTextureIO.newTextureData("demo/textures/pointgrid.png"));
		
		addControls("app", "app", this);
	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {
		g.clear();
		
		float myTexWidth = _myTexture.width() / _cScale;
		float myTexHeight =  _myTexture.height() / _cScale;
		
		_myTexture.textureFilter(CCTextureFilter.LINEAR);
		g.image(_myTexture, -myTexWidth,-myTexHeight, myTexWidth,myTexHeight);
		_myTexture.textureFilter(CCTextureFilter.NEAREST);
		g.image(_myTexture,0,-myTexHeight, myTexWidth,myTexHeight);
		
		_myTexture2.textureFilter(CCTextureFilter.LINEAR);
		g.image(_myTexture2, -myTexWidth,0, myTexWidth,myTexHeight);
		_myTexture2.textureFilter(CCTextureFilter.NEAREST);
		g.image(_myTexture2,0,0, myTexWidth,myTexHeight);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCTextureFilterDemo.class);
		myManager.settings().size(1500, 500);
		myManager.start();
	}
}

