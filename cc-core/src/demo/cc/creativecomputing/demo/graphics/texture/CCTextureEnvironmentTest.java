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
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureEnvironmentMode;

public class CCTextureEnvironmentTest extends CCApp {
	
	private CCTexture2D _myTexture1;
	private CCTexture2D _myTexture2;
	
	@CCControl(name = "red", min = 0, max = 1)
	private static float _cR = 0f;
	@CCControl(name = "green", min = 0, max = 1)
	private static float _cG = 0f;
	@CCControl(name = "blue", min = 0, max = 1)
	private static float _cB = 0f;
		
	@CCControl(name = "blend red", min = 0, max = 1)
	private static float _cBR = 0f;
	@CCControl(name = "blend green", min = 0, max = 1)
	private static float _cBG = 0f;
	@CCControl(name = "blend blue", min = 0, max = 1)
	private static float _cBB = 0f;
		
	@CCControl(name = "env mode 1")
	private static CCTextureEnvironmentMode environment1 = CCTextureEnvironmentMode.DECAL;
	@CCControl(name = "env mode 2")
	private static CCTextureEnvironmentMode environment2 = CCTextureEnvironmentMode.DECAL;

	@Override
	public void setup() {
		addControls("app", "app", this);
		_myTexture1 = new CCTexture2D(CCTextureIO.newTextureData("textures/noisetexture2.png"));
		_myTexture2 = new CCTexture2D(CCTextureIO.newTextureData("textures/noisetexture2.png"));
	}

	@Override
	public void update(final float theDeltaTime) {
		_myTexture1.textureEnvironmentMode(environment1);
		_myTexture2.textureEnvironmentMode(environment2);
		
		_myTexture1.blendColor(new CCColor(_cBR, _cBG, _cBB));
		_myTexture2.blendColor(new CCColor(_cBR, _cBG, _cBB));
	}

	@Override
	public void draw() {
		g.clearColor(0f,1f,0f);
		g.clear();
//		g.gl.glEnable(CCTextureTarget.TEXTURE_2D.glID);
		g.color(_cR, _cG, _cB);
		g.image(_myTexture1,-200,-100,200,200);
		g.image(_myTexture2,0,-100,200,200);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCTextureEnvironmentTest.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}

