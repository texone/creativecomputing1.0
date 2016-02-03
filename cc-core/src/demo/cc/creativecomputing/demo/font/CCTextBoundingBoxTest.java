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
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.text.CCText;

public class CCTextBoundingBoxTest extends CCApp {
	
	private CCFont<?> _myFont;
    private CCText _myText;

	@Override
	public void setup() {
		_myFont = CCFontIO.createVectorFont("Helvetica", 50);
		_myText = new CCText(_myFont);
		_myText.text("TEXONEggg");
	}

	@Override
	public void update(final float theDeltaTime) {
	}

	@Override
	public void draw() {
		g.clear();
		g.color(255,0,0);
		_myText.boundingBox().draw(g);
		g.color(255);
		_myText.draw(g);
		
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCTextBoundingBoxTest.class);
		myManager.settings().size(1000, 700);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

