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
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.font.CCCharSet;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.CCVectorFont;
import cc.creativecomputing.graphics.font.text.CCText;

public class CCVectorFontTest extends CCApp {
	
	@CCControl(name = "font size", min = 0, max = 200)
	private float _cFontSize = 0;

	private float _myTextWidth;
	private CCText _myText;
	
	public void setup() {
		CCVectorFont myFont = CCFontIO.createVectorFont("arial", 24, CCCharSet.EXTENDED_CHARSET);
		
		String myText = "CCCatch";
		_myTextWidth = myFont.width(myText);
		
		System.out.println(_myTextWidth);
		
		_myText = new CCText(myFont);
		_myText.text(myText);
		
		addControls("app", "app", this);
	}

	@Override
	public void update(final float theDeltaTime) {
	}

	@Override
	public void draw() {
		g.clear();
		_myText.size(_cFontSize);
		_myText.draw(g);
		
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCVectorFontTest.class);
		myManager.settings().size(500, 500);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
