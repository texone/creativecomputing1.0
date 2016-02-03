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
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.texture.CCTexture1D;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;

public class CCTexture1DUpdateTest extends CCApp {
	
	private CCTextureData _myTextureData1;
	private CCTextureData _myTextureData2;
	private CCTexture1D _myTexture;

	@Override
	public void setup() {
		_myTextureData1 = CCTextureIO.newTextureData("demo/textures/1d_texture_colors.png");
		_myTextureData2 = CCTextureIO.newTextureData("demo/textures/1d_texture.png");
		
		_myTexture = new CCTexture1D();
		_myTexture.data(_myTextureData1);
		_myTexture.updateData(_myTextureData2,100,100,150);
		_myTexture.wrap(CCTextureWrap.MIRRORED_REPEAT);
	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {
		g.clear();
		g.texture(_myTexture);
		g.beginShape(CCDrawMode.QUADS);
		g.textureCoords(0);
		g.vertex(0,0);
		g.textureCoords(1);
		g.vertex(200,0);
		g.textureCoords(1);
		g.vertex(200,200);
		g.textureCoords(0);
		g.vertex(0,200);
		g.endShape();
		g.noTexture();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCTexture1DUpdateTest.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}

