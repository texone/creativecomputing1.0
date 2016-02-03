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
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.text.CCLineBreakMode;
import cc.creativecomputing.graphics.font.text.CCText;
import cc.creativecomputing.graphics.font.text.CCTextAlign;

public class CCAlignmentTest extends CCApp {

	private CCFont<?> _myFont;
	private CCText _myBlockText;
	private float _myTextBlockHeight;
	
	@CCControl(name = "Align")
	private CCTextAlign _myAlign = CCTextAlign.JUSTIFY;
	
	public void setup() {
		_myFont = CCFontIO.createTextureMapFont( "Arial", 20);
		
		_myBlockText = new CCText(_myFont);
		_myBlockText.lineBreak(CCLineBreakMode.BLOCK);
		_myBlockText.position(0,0);
		_myBlockText.dimension(150, 100);
		_myBlockText.align(CCTextAlign.JUSTIFY);
		_myBlockText.text("Use CCBlockText to display text in a defined block.");
		
		_myTextBlockHeight = _myBlockText.height();
		
		addControls("app", "app", this);
	}
	
	public void update(final float theDeltaTime){
		_myBlockText.align(_myAlign);
	}

	public void draw() {
		g.clearColor(0);
		g.clear();
		g.color(255);
		_myBlockText.draw(g);
		g.line(-width/2, -_myTextBlockHeight, width/2, -_myTextBlockHeight);
	}

	public static void main(String[] args) {
		final CCApplicationManager myManager = new CCApplicationManager(CCAlignmentTest.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}
