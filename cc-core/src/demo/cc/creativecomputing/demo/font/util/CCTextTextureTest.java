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
package cc.creativecomputing.demo.font.util;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.util.CCTextTexture;
import cc.creativecomputing.util.logging.CCLog;


public class CCTextTextureTest extends CCApp{

	private CCTextTexture _myTextTexture;

	public void setup(){
		for(String myFont:CCFontIO.list())CCLog.info(myFont);;
		_myTextTexture = CCFontIO.createBitmapText("Texone", "Times", 60);
		g.clearColor(255);
	}
	
	public void draw(){
		g.clear();
		
		g.color(0);
		_myTextTexture.draw(g,-100,0);
		g.line(-100,-height/2,-100,height/2);
		g.line(-width/2,0,width/2,0);
	}
	
	public static void main(String[] args){
		final CCApplicationManager myManager = new CCApplicationManager(CCTextTextureTest.class);
		myManager.settings().antialiasing(8);
		myManager.settings().size(400, 400);
		myManager.start();
	}
}
