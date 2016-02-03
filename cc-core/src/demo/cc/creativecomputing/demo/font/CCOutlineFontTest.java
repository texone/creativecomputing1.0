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
import cc.creativecomputing.graphics.font.text.CCTextAlign;


public class CCOutlineFontTest extends CCApp{

	CCFont<?> font0;
	CCFont<?> font1;
	public void setup(){
		
		font0 = CCFontIO.createOutlineFont("Arial",48, 30);
		font1 = CCFontIO.createVectorFont("Arial",48);
		
		g.textAlign(CCTextAlign.CENTER);
		g.clearColor(1f,0,0);
		g.bezierDetail(31);
	}
	
	public void draw(){
		g.clear();
		
		g.color(255);

		g.textFont(font1);
		g.textSize(192);
		g.text("texone",0,0);
		

		g.strokeWeight(2);
		g.color(0);

		g.textFont(font0);
		g.textSize(192);
		g.text("texone",0,0);
	}
	
	public static void main(String[] args){
		final CCApplicationManager myManager = new CCApplicationManager(CCOutlineFontTest.class);
		myManager.settings().size(1200, 400);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
