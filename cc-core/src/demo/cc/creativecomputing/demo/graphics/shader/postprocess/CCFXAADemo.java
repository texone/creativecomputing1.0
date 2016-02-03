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
package cc.creativecomputing.demo.graphics.shader.postprocess;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.shader.postprocess.CCFXAA;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureIO;

public class CCFXAADemo extends CCApp {
	
	private CCTexture2D _myTestContent;
	private CCFXAA _myCCFXAA;

	@Override
	public void setup() {
		_myTestContent = new CCTexture2D(CCTextureIO.newTextureData("demo/textures/fxaa_demo_02.jpg"));
		_myCCFXAA = new CCFXAA(_myTestContent);
		
		addControls("fxaa", "fxaa", _myCCFXAA);
	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {
		g.clear();
		g.pushMatrix();
		g.translate(-width/4,0);
		_myCCFXAA.draw(g);
		g.popMatrix();
		
		g.image(_myTestContent,0,-height/2);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCFXAADemo.class);
		myManager.settings().size(1000, 500);
		myManager.start();
	}
}

