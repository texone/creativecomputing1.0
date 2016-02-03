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
import cc.creativecomputing.graphics.font.text.CCLineBreakMode;
import cc.creativecomputing.graphics.font.text.CCVectorMeshText;
import cc.creativecomputing.graphics.font.util.CCLoremIpsumGenerator;
import cc.creativecomputing.math.CCVector2f;

public class CCVectorMeshTextDemo extends CCApp {
	
	private CCVectorMeshText _myVectorMeshText;
	
	private String _myText = "";
	
	public void setup() {
		String myFont = "arial";
		float mySize = 24;
		
		_myText = CCLoremIpsumGenerator.generate(40);
		_myVectorMeshText = new CCVectorMeshText(CCFontIO.createVectorFont(myFont, mySize, CCCharSet.EXTENDED_CHARSET));
		_myVectorMeshText.position(-300,300);
		_myVectorMeshText.dimension(300, 400);
		_myVectorMeshText.text(_myText);
		_myVectorMeshText.lineBreak(CCLineBreakMode.BLOCK);
	}
	
	public void draw() {
		g.clear();
		g.color(255);
		_myVectorMeshText.draw(g);
		
		
		g.color(255,50);
		_myVectorMeshText.boundingBox().draw(g);
		_myVectorMeshText.textGrid().drawGrid(g);
		
		int myIndex = _myVectorMeshText.textGrid().gridIndex(new CCVector2f(mouseX - width/2, height/2 - mouseY));
		CCVector2f myPos = _myVectorMeshText.textGrid().gridPosition(myIndex);
		
		g.color(255,0,0);
		g.line(myPos.x, myPos.y, myPos.x, myPos.y - 20);
		g.line(-width/2, 300, width/2, 300);
	}
	
	

	public static void main(String[] args) {
		final CCApplicationManager myManager = new CCApplicationManager(CCVectorMeshTextDemo.class);
		myManager.settings().size(800, 800);
		myManager.settings().antialiasing(8);
		myManager.settings().frameRate(5);
		myManager.start();
	}
}
