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
package cc.creativecomputing.demo.math.easing;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.math.easing.CCEasing;
import cc.creativecomputing.math.easing.CCEasing.CCEaseFormular;

public class CCEasingTest extends CCApp{
	
	private CCEasing _myEasing;
	
	@CCControl(name = "ease mode" )
	private CCEaseFormular _myFormular = CCEaseFormular.LINEAR;

	@Override
	public void setup() {
		_myEasing = CCEaseFormular.CUBIC.easing();
		
		addControls("easing", "easing", this);
		showControls();
		_myUI.drawBackground(false);
	}
	
	@Override
	public void update(final float theDeltaTime) {
		_myEasing = _myFormular.easing();
	}

	@Override
	public void draw() {
		g.clear();
		
		g.translate(-200, -200);
		g.scale(400);
		g.color(255,0,0);
		
		g.beginShape(CCDrawMode.LINE_STRIP);
		for(float a = 0; a < 1; a +=0.01f){
			g.vertex(a, _myEasing.easeIn(a));
		}
		g.endShape();
		
		g.color(0,255,0);
		g.beginShape(CCDrawMode.LINE_STRIP);
		for(float a = 0; a < 1; a +=0.01f){
			g.vertex(a, _myEasing.easeOut(a));
		}
		g.endShape();
		
		g.color(0,0,255);
		g.beginShape(CCDrawMode.LINE_STRIP);
		for(float a = 0; a < 1; a +=0.01f){
			g.vertex(a, _myEasing.easeInOut(a));
		}
		g.endShape();
	}
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCEasingTest.class);
		myManager.settings().size(600, 600);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
