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
import cc.creativecomputing.graphics.font.CCTextureMapFont;
import cc.creativecomputing.graphics.font.text.CCText;

public class CCTextWidthDemo extends CCApp {

	private float _myTextWidth;
	private CCText _myText;
	
	public void setup() {
		CCTextureMapFont myFont = CCFontIO.createTextureMapFont("arial", 24, true, CCCharSet.EXTENDED_CHARSET);
		
		String myText = "CCCatch";
		
		_myText = new CCText(myFont);
		_myText.text(myText);
		_myTextWidth = _myText.width();
		
		System.out.println(_myTextWidth);
	}

	@Override
	public void update(final float theDeltaTime) {
	}

	@Override
	public void draw() {
		g.clear();
		_myText.draw(g);
		g.line(_myTextWidth,0,_myTextWidth,100);
		
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCTextWidthDemo.class);
		myManager.settings().size(500, 500);
		myManager.settings().frameRate(5);
		myManager.start();
	}
}

