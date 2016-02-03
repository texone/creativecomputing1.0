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
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.CCUnicodeBlock;
import cc.creativecomputing.graphics.font.text.CCText;
import cc.creativecomputing.util.logging.CCLog;

public class CCUnicodeBlockTest extends CCApp {
	
	private CCText _myText;

	@Override
	public void setup() {
		CCFont<?> myFont = CCFontIO.createTextureMapFont("Helvetica", 20, true, CCCharSet.EXTENDED_CHARSET);
		
		StringBuffer myBuffer = new StringBuffer("test");
		
		int myCounter = 1;
		
		for (char myChar : CCUnicodeBlock.LATIN_1_SUPPLEMENT.chars()) {
			if (!myFont.canDisplay(myChar)) {
				CCLog.info("cannot display:" + (int) myChar + " " + myChar);
			} else {
				CCLog.info("can display:" + (int) myChar + " " + myChar);
				myBuffer.append(myChar);
			}
			if(myCounter % 30 == 0) {
				myBuffer.append("\n");
			}
			myCounter++;
		}
		
		_myText = new CCText(myFont);
		_myText.text(myBuffer.toString());
		_myText.position(-width/2 + 80, height/2 - 80);
	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {
		g.clear();
		_myText.draw(g);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCUnicodeBlockTest.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}
