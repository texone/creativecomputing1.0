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

public class CCTextureCompressionTest extends CCApp {
	
	private CCTexture2D _myTexture1;
	private CCTexture2D _myTexture2;
	
	@CCControl(name = "red", min = 0, max = 1)
	private float _cR = 0f;
	@CCControl(name = "green", min = 0, max = 1)
	private float _cG = 0f;
	@CCControl(name = "blue", min = 0, max = 1)
	private float _cB = 0f;
		
	@CCControl(name = "filter")
	private CCTextureFilter _cFilter = CCTextureFilter.NEAREST;
	@CCControl(name = "mipmap filter")
	private CCTextureMipmapFilter _cMipmap_filter = CCTextureMipmapFilter.NEAREST;

	@Override
	public void setup() {
		addControls("app", "app", this);

		CCTextureAttributes myAttributes = new CCTextureAttributes();
		myAttributes.generateMipmaps(true);
		
		_myTexture1 = new CCTexture2D(myAttributes);
		_myTexture1.compressData(CCTextureIO.newTextureData("demo/textures/waltz.jpg"));
		_myTexture2 = new CCTexture2D(CCTextureIO.newTextureData("demo/textures/waltz.dds"), myAttributes);
	}

	@Override
	public void update(final float theDeltaTime) {
		_myTexture1.textureFilter(_cFilter);
		_myTexture1.textureMipmapFilter(_cMipmap_filter);
	}

	@Override
	public void draw() {
		g.clearColor(0f,1f,0f);
		g.clear();
//		g.gl.glEnable(CCTextureTarget.TEXTURE_2D.glID);
		
//		g.scale(mouseX / (float)width);
		
		g.color(_cR, _cG, _cB);
		g.texture(_myTexture2);
		g.beginShape(CCDrawMode.QUADS);
		g.vertex(-_myTexture1.width()/2, -_myTexture1.height()/2, 0f, 0f);
		g.vertex( 0, -_myTexture1.height()/2, 0.5f, 0f);
		g.vertex( 0,  _myTexture1.height()/2, 0.5f, 1f);
		g.vertex(-_myTexture1.width()/2,  _myTexture1.height()/2, 0, 1);
		g.endShape();
		g.noTexture();
		
		g.line(0, -height/2, 0, height/2);

		g.texture(_myTexture1);
		g.beginShape(CCDrawMode.QUADS);
		g.vertex(0, -_myTexture2.height()/2, 0.5f, 0);
		g.vertex(_myTexture2.width()/2, -_myTexture2.height()/2, 1f, 0f);
		g.vertex(_myTexture2.width()/2,  _myTexture2.height()/2, 1f, 1f);
		g.vertex(0,  _myTexture2.height()/2, 0.5f, 1f);
		g.endShape();
		g.noTexture();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCTextureCompressionTest.class);
		myManager.settings().size(1000, 667);
		myManager.start();
	}
}

