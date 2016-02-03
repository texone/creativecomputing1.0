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
package cc.creativecomputing.demo.graphics;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.CCGraphics.CCBlendFactor;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureIO;

public class CCBlendMatrixTest extends CCApp {

	private CCTexture2D _myTexture1;
	private CCTexture2D _myTexture2;
	
	private float _myPartSize;

	public void setup() {
		_myTexture1 = new CCTexture2D(CCTextureIO.newTextureData("demo/textures/small_04.jpg"));
		_myTexture2 = new CCTexture2D(CCTextureIO.newTextureData("demo/textures/small_02.jpg"));
		
		_myPartSize = 75;//(float)width / CCBlendFactor.values().length;
		
		g.textFont(CCFontIO.createGlutFont(CCFontIO.BITMAP_HELVETICA_10));
	}

	public void draw() {
		g.clear();
		g.noDepthTest();
		
		CCBlendFactor mySrcFactor;
		CCBlendFactor myDstFactor;

		for(int x = 0; x < CCBlendFactor.values().length - 1;x++) {
			myDstFactor = CCBlendFactor.values()[x];
			for(int y = 0; y < CCBlendFactor.values().length;y++) {
			mySrcFactor = CCBlendFactor.values()[y];

				g.blend();
				g.blendMode(mySrcFactor, myDstFactor);
				g.checkError("mySrcFactor:"+mySrcFactor.name()+" myDstFactor:"+myDstFactor.name());

				g.image(_myTexture1, -width / 2 + x * _myPartSize, -height / 2 + y * _myPartSize, _myPartSize, _myPartSize);
				g.image(_myTexture2, -width / 2 + x * _myPartSize, -height / 2 + y * _myPartSize, _myPartSize, _myPartSize);
			}
		}
		g.noBlend();
		
		for(int y = 0; y < CCBlendFactor.values().length;y++) {
			mySrcFactor = CCBlendFactor.values()[y];
			g.color(0);
			g.text(mySrcFactor.name(), -width / 2 + 11, -height / 2 + y * _myPartSize+11);
			g.color(255);
			g.text(mySrcFactor.name(), -width / 2 + 10, -height / 2 + y * _myPartSize+10);
		}
		
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCBlendMatrixTest.class);
		myManager.settings().size(75 * 14, 900);
		myManager.start();
	}
}
