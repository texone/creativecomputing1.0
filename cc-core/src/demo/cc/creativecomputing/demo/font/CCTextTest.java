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

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.font.CCCharSet;
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.CCVectorFont;
import cc.creativecomputing.graphics.font.CCGlutFont.CCGlutFontType;
import cc.creativecomputing.graphics.font.text.CCLineBreakMode;
import cc.creativecomputing.graphics.font.text.CCText;
import cc.creativecomputing.graphics.font.util.CCLoremIpsumGenerator;
import cc.creativecomputing.math.CCVector2f;

public class CCTextTest extends CCApp {

	CCVectorFont _myVectorFont;
	
	private List<CCText> _myTexts = new ArrayList<CCText>();
	private int _myTextID = 0;
	
	private String _myText = "";
	
	public void setup() {
		String myFont = "arial";
		float mySize = 24;
		
		_myText = CCLoremIpsumGenerator.generate(40);
		
		_myTexts.add(createText("GLUT",CCFontIO.createGlutFont(CCGlutFontType.BITMAP_TIMES_ROMAN_24, CCCharSet.EXTENDED_CHARSET)));
		_myTexts.add(createText("OUTLINE",CCFontIO.createOutlineFont(myFont, mySize, CCCharSet.EXTENDED_CHARSET, 30)));
		_myTexts.add(createText("VECTOR",CCFontIO.createVectorFont(myFont, mySize, CCCharSet.EXTENDED_CHARSET)));
		_myTexts.add(createText("TEXTURE MAP",CCFontIO.createTextureMapFont(myFont, mySize, true, CCCharSet.EXTENDED_CHARSET)));
	}
	
	private CCText createText(final String theType, final CCFont<?> theFont) {
		CCText myText = new CCText(theFont);
		myText.position(-300,300);
		myText.dimension(300, 400);
		myText.text("Type "+theType +"\n" + _myText);
		myText.lineBreak(CCLineBreakMode.BLOCK);
		return myText;
	}
	
	public void draw() {
		g.clear();
		g.color(255);
		
		_myTexts.get(_myTextID).draw(g);
		
		g.color(255,50);
		_myTexts.get(_myTextID).boundingBox().draw(g);
		_myTexts.get(_myTextID).textGrid().drawGrid(g);
		
		int myIndex = _myTexts.get(_myTextID).textGrid().gridIndex(new CCVector2f(mouseX - width/2, height/2 - mouseY));
		CCVector2f myPos = _myTexts.get(_myTextID).textGrid().gridPosition(myIndex);
		
		g.color(255,0,0);
		g.line(myPos.x, myPos.y, myPos.x, myPos.y - 20);
		g.line(-width/2, 300, width/2, 300);
	}
	
	@Override
	public void keyPressed(final CCKeyEvent theEvent) {
		switch (theEvent.keyCode()) {
		case VK_UP:
			_myTextID++;
			_myTextID %= _myTexts.size();
			break;
		case VK_DOWN:
			_myTextID--;
			if(_myTextID < 0)_myTextID += _myTexts.size();
			break;
		default:
		}
	}

	public static void main(String[] args) {
		final CCApplicationManager myManager = new CCApplicationManager(CCTextTest.class);
		myManager.settings().size(800, 800);
		myManager.settings().antialiasing(8);
		myManager.settings().frameRate(5);
		myManager.start();
	}
}
