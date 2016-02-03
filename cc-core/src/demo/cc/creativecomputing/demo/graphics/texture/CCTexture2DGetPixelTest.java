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
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.graphics.texture.CCTextureIO;


public class CCTexture2DGetPixelTest extends CCApp{

	private CCTextureData _myTextureData;
	private CCTexture2D _myTexture;

	int nNumPoints = 4;


	public void setup(){
		_myTextureData = CCTextureIO.newTextureData("textures/waltz.jpg");
		_myTexture = new CCTexture2D();
		_myTexture.data(_myTextureData);
	}
	
	public void draw(){
		g.clearColor(0.3f);
		g.clear();
		g.translate(-width/2, -height/2);
		for(int i = 0; i < _myTexture.width() - 1;i+=3){
			for(int j = 0; j < _myTexture.height() - 1; j+=3){
				g.color(_myTexture.getPixel(i,j));
				g.rect(i, j, 3, 3);
			}
		}
	}
	
	public static void main(String[] args){
		final CCApplicationManager myManager = new CCApplicationManager(CCTexture2DGetPixelTest.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}
