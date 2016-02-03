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


import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.CCOutlineFont;
import cc.creativecomputing.graphics.font.text.CCTextAlign;
import cc.creativecomputing.math.CCVector3f;



public class CCOutlineTextPath extends CCApp{

	private List<CCVector3f> _myTextPath;

	public void setup(){
		CCOutlineFont font = CCFontIO.createOutlineFont("Arial",48, 30);
		g.bezierDetail(31);
		_myTextPath = font.getPath("TEXONE", CCTextAlign.CENTER, 192,0, 0, 0);
		g.clearColor(0.3f);
	}
	
	public void draw(){
		g.clear();
		g.beginShape(CCDrawMode.POINTS);
		for(CCVector3f myPoint:_myTextPath){
			g.vertex(myPoint);
		}
		g.endShape();
	}
	
	public static void main(String[] args){
		final CCApplicationManager myManager = new CCApplicationManager(CCOutlineTextPath.class);
		myManager.settings().antialiasing(8);
		myManager.settings().size(1200, 400);
		myManager.start();
	}
}
