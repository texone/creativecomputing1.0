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
package cc.creativecomputing.demo.font;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.CCFontSettings;
import cc.creativecomputing.graphics.font.CCTextureMapFont;
import cc.creativecomputing.graphics.font.text.CCText;
import cc.creativecomputing.graphics.font.util.CCLoremIpsumGenerator;

public class CCTextureMapFontTest extends CCApp {

	CCTextureMapFont _myFont;
	CCTextureMapFont _myFont2;
	
	private CCText _myText;
	private CCText _myText2;
	
	public void setup() {
		String myFont = "DeuBaUnivers-Regular";
		float mySize = 30;
		
		String myLorem = CCLoremIpsumGenerator.generate(40);
		
		CCFontSettings mySettings = new CCFontSettings(myFont, mySize);
		mySettings.blurRadius(10);
		_myFont = CCFontIO.createTextureMapFont(mySettings);
		CCFontSettings mySettings2 = new CCFontSettings(myFont, mySize);
		_myFont2 = CCFontIO.createTextureMapFont(mySettings2);
		_myText = new CCText(_myFont);
//		_myText.lineBreak(CCLineBreakMode.BLOCK);
		_myText.text(myLorem);
		_myText.position(-300,300);

		_myText2 = new CCText(_myFont2);
//		_myText.lineBreak(CCLineBreakMode.BLOCK);
		_myText2.text(myLorem);
		_myText2.position(-300,300);
		
		g.clearColor(255);
	}

	public void draw() {
		g.clear();
		
		g.blend();
		g.color(0);
//		g.image(_myFont.texture(), -400,-400);

		
		_myText.draw(g);
		g.color(255);
		_myText2.draw(g);
	}
	
	

	public static void main(String[] args) {
		final CCApplicationManager myManager = new CCApplicationManager(CCTextureMapFontTest.class);
		myManager.settings().size(800, 800);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
