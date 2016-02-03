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
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureAttributes;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureMipmapFilter;

public class CCTextureAnisotropicFilterTest extends CCApp {
	
	private CCTexture2D _myTexture1;
	private CCTexture2D _myTexture2;
	
	@CCControl(name = "red", min = 0, max = 1)
	private float _cR = 0f;
	@CCControl(name = "green", min = 0, max = 1)
	private float _cG = 0f;
	@CCControl(name = "blue", min = 0, max = 1)
	private float _cB = 0f;
	
	@CCControl(name = "anisotropic filtering", min = 0, max = 1)
	private float _cAnisotropicFiltering = 0f;
		
	@CCControl(name = "filter")
	private CCTextureFilter _cFilter = CCTextureFilter.NEAREST;
	@CCControl(name = "mipmap filter")
	private CCTextureMipmapFilter _cMipmap_filter = CCTextureMipmapFilter.NEAREST;

	@Override
	public void setup() {
		addControls("app", "color", this);
		
		CCTextureAttributes myAttributes = new CCTextureAttributes();
		myAttributes.generateMipmaps(false);
		
		_myTexture1 = new CCTexture2D(CCTextureIO.newTextureData("textures/dice.jpg"), myAttributes);
		_myTexture2 = new CCTexture2D(CCTextureIO.newTextureData("textures/dice.jpg"), myAttributes);
	}

	@Override
	public void update(final float theDeltaTime) {
		_myTexture1.textureFilter(_cFilter);
		_myTexture1.textureMipmapFilter(_cMipmap_filter);
		_myTexture1.anisotropicFiltering(_cAnisotropicFiltering);

		_myTexture2.textureFilter(_cFilter);
		_myTexture2.textureMipmapFilter(_cMipmap_filter);
	}

	@Override
	public void draw() {
		g.clearColor(0f,1f,0f);
		g.clear();
//		g.gl.glEnable(CCTextureTarget.TEXTURE_2D.glID);
		
		g.rotateX(70);
		g.scale(mouseX / (float)width);
		
		g.color(_cR, _cG, _cB);
		g.texture(_myTexture1);
		g.beginShape(CCDrawMode.QUADS);
		g.vertex(-500, -333, 0f, 0f);
		g.vertex( 0, -333, 1, 0f);
		g.vertex( 0,  334, 1, 1f);
		g.vertex(-500,  334, 0, 1);
		g.endShape();
		g.noTexture();

		g.texture(_myTexture2);
		g.beginShape(CCDrawMode.QUADS);
		g.vertex(0, -333, 0f, 0);
		g.vertex(500, -333, 1f, 0f);
		g.vertex(500,  334, 1f, 1f);
		g.vertex(0,  334, 0f, 1f);
		g.endShape();
		g.noTexture();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCTextureAnisotropicFilterTest.class);
		myManager.settings().size(1000, 667);
		myManager.start();
	}
}

