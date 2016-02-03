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
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;

public class CCTextureWrapTest extends CCApp {

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

	@CCControl(name = "wrap 1")
	private static CCTextureWrap _cWrap1 = CCTextureWrap.CLAMP;
	@CCControl(name = "wrap 2")
	private static CCTextureWrap _cWrap2 = CCTextureWrap.CLAMP;

	@Override
	public void setup() {
		addControls("app", "app", this);
		_myTexture1 = new CCTexture2D(CCTextureIO.newTextureData("textures/noisetexture2.png"));
		_myTexture2 = new CCTexture2D(CCTextureIO.newTextureData("textures/noisetexture2.png"));
	}

	@Override
	public void update(final float theDeltaTime) {
		_myTexture1.wrap(_cWrap1);
		_myTexture2.wrap(_cWrap2);

		_myTexture1.textureBorderColor(new CCColor(_cBR, _cBG, _cBB));
		_myTexture2.textureBorderColor(new CCColor(_cBR, _cBG, _cBB));
	}

	@Override
	public void draw() {
		g.clearColor(0f, 1f, 0f);
		g.clear();
		// g.gl.glEnable(CCTextureTarget.TEXTURE_2D.glID);
		g.color(_cR, _cG, _cB);
		g.texture(_myTexture1);
		g.beginShape(CCDrawMode.QUADS);
		g.vertex(-200, -100, -0.5f, -0.5f);
		g.vertex(0, -100, 1.5f, -0.5f);
		g.vertex(0, 100, 1.5f, 1.5f);
		g.vertex(-200, 100, -0.5f, 1.5f);
		g.endShape();
		g.noTexture();

		g.texture(_myTexture2);
		g.beginShape(CCDrawMode.QUADS);
		g.vertex(0, -100, -0.5f, -0.5f);
		g.vertex(200, -100, 1.5f, -0.5f);
		g.vertex(200, 100, 1.5f, 1.5f);
		g.vertex(0, 100, -0.5f, 1.5f);
		g.endShape();
		g.noTexture();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCTextureWrapTest.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}
