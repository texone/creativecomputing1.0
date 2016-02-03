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
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.font.CCCharSet;
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.CCVectorFont;
import cc.creativecomputing.graphics.font.text.CCLineBreakMode;
import cc.creativecomputing.graphics.font.text.CCMultiFontText;
import cc.creativecomputing.graphics.font.util.CCLoremIpsumGenerator;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.util.CCStopWatch;
import cc.creativecomputing.util.logging.CCLog;

public class CCMultiFontTextTest extends CCApp {

	CCVectorFont _myVectorFont;
	
	private CCMultiFontText _myText = new CCMultiFontText();
	
	private String[] myFontList = CCFontIO.list();
	
	public void setup() {
		
		_myText.position().set(-300,300,0);
		_myText.lineBreak(CCLineBreakMode.BLOCK);
		_myText.position(-300,300);
		_myText.dimension(300, 400);

		createText();
		
		noCursor();
		cursor();
	}
	
	int i = 0;
	
	private void createText(){
		// 4 sehr grosses leading checken
		// 6,8 leading komisch
		
		CCMath.randomSeed(i);
		_myText.reset();
		for(int i = 0; i < 5;i++){
			String myFont = myFontList[(int)CCMath.random(myFontList.length)];
			int mySize = (int)CCMath.random(10,30);
			String myText = CCLoremIpsumGenerator.generate((int)CCMath.random(5,20));
			CCLog.info(myFont + " " + mySize + " " + myText);
			CCStopWatch.instance().startWatch("create font");
			CCFont<?> myFontObject = CCFontIO.createTextureMapFont(myFont, mySize, CCCharSet.EXTENDED_CHARSET);
			CCLog.info(CCStopWatch.instance().endWatch("create font"));
			_myText.addText(
				myText + " ", 
				myFontObject,
				mySize,
				CCColor.random()
			);
		}
		_myText.breakText();
		CCLog.info(i);
		i++;
	}
	
	@Override
	public void keyPressed(CCKeyEvent theKeyEvent) {
		switch(theKeyEvent.keyCode()){
		case VK_C:
			createText();
			break;
		default:
		}
	}
	
	
	public void draw() {
		g.clear();
		g.color(255);
		
		_myText.draw(g);
		
		g.color(255,50);
		_myText.boundingBox().draw(g);
		_myText.textGrid().drawGrid(g);
		
		int myIndex = _myText.textGrid().gridIndex(new CCVector2f(mouseX - width/2, height/2 - mouseY));
		CCVector2f myPos = _myText.textGrid().gridPosition(myIndex);
		
		g.color(255,0,0);
		g.line(myPos.x, myPos.y, myPos.x, myPos.y - 20);
		g.line(-width/2, 300, width/2, 300);
	}
	

	public static void main(String[] args) {
		final CCApplicationManager myManager = new CCApplicationManager(CCMultiFontTextTest.class);
		myManager.settings().size(800, 800);
		myManager.settings().antialiasing(8);
		myManager.settings().frameRate(5);
		myManager.start();
	}
}
