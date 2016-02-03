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
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.util.CCShadowTextRenderer;
import cc.creativecomputing.graphics.font.util.CCTextTexture;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.util.logging.CCLog;


public class CCShadowTextRendererTest extends CCApp{

	private CCTextTexture _myShadowTextTexture;
	private CCTextTexture _myShadowTextTexture2;

	public void setup(){
		for(String myFont:CCFontIO.list()) {
			CCLog.info(myFont);
		}
		_myShadowTextTexture = CCFontIO.createBitmapText(new CCShadowTextRenderer(20), "TEXONE", "Arial", 96);	
		_myShadowTextTexture = CCFontIO.createBitmapText(new CCShadowTextRenderer(3), "largeintestine1", "Arial", 40);
		_myShadowTextTexture2 = CCFontIO.createBitmapText(new CCShadowTextRenderer(3,0f), "largeintestine2", "Arial", 40);
		g.clearColor(255,0,0);
	}
	
	public void draw(){
		g.clear();
		g.color(255);
		g.blend();
		CCMath.randomSeed(0);
		g.beginShape(CCDrawMode.POINTS);
		for(int i = 0; i < 30000;i++){
			g.vertex(CCMath.random(-width/2, width/2),CCMath.random(-height/2, height/2));
		}
		g.endShape();
//		for(int i = 0; i < 50;i++) {
//			g.pushMatrix();
//			g.translate(CCMath.random(-width/2, width/2), CCMath.random(-height/2, height/2));
//			g.scale(CCMath.random(1));
//			_myShadowTextTexture.draw(g,0,0);
//			g.popMatrix();
//		}
		
		g.beginShape(CCDrawMode.LINES);
		g.color(1.0f, 0.8f);
		g.vertex(-width/2,0);
		g.color(1.0f, 0.0f);
		g.vertex(width/2,0);
		g.endShape();
		g.color(255);
		
		g.pushMatrix();
		g.translate(-width/2,  -10);
		_myShadowTextTexture.draw(g,0,0);
		_myShadowTextTexture2.draw(g,0,40);
		g.popMatrix();
	}
	
	public static void main(String[] args){
		final CCApplicationManager myManager = new CCApplicationManager(CCShadowTextRendererTest.class);
		myManager.settings().antialiasing(8);
		myManager.settings().size(400, 400);
		myManager.start();
	}
}
