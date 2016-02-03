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
import cc.creativecomputing.graphics.font.CCCharSet;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.CCVectorFont;
import cc.creativecomputing.graphics.font.text.CCText;
import cc.creativecomputing.util.logging.CCLog;

public class CCKerningTest extends CCApp {

	CCVectorFont _myFont;
	CCVectorFont _myFont2;
	
	private CCText _myText;
	private CCText _myText2;
	
	public void setup() {
		float mySize = 70;
		
		_myFont = CCFontIO.createVectorFont("arial", mySize, CCCharSet.EXTENDED_CHARSET);
		

		_myText = new CCText(_myFont);
		_myText.text("L L ATAW.F. without kerning");
		_myText.position(-300,200);
		
		_myFont2 = CCFontIO.createVectorFont("demo/font/Arial.ttf", mySize, CCCharSet.EXTENDED_CHARSET);
		
		for(char myChar1:CCCharSet.EXTENDED_CHARSET.chars()) {
			for(char myChar2:CCCharSet.EXTENDED_CHARSET.chars()) {
				float myKerning = _myFont2.kerning(myChar1, myChar2) * mySize;
				if(myKerning < 0)CCLog.info(myChar1 + ":" + myChar2 + ":" +myKerning);
			}
		}

		_myText2 = new CCText(_myFont2);
		_myText2.text("L L ATAW.F. with kerning");
		_myText2.position(-300,270);
	}

	public void draw() {
		g.clear();
		g.color(255);
		
		_myText.draw(g);
		_myText2.draw(g);
	}
	
	

	public static void main(String[] args) {
		final CCApplicationManager myManager = new CCApplicationManager(CCKerningTest.class);
		myManager.settings().size(800, 800);
		myManager.settings().antialiasing(8);
		myManager.settings().frameRate(30);
		myManager.start();
	}
}
