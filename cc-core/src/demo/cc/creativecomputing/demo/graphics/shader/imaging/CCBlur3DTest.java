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
package cc.creativecomputing.demo.graphics.shader.imaging;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.shader.imaging.CCGPUSeperateGaussianBlur;
import cc.creativecomputing.math.util.CCArcball;

public class CCBlur3DTest extends CCApp {
	
	public final static float MAXIMUM_BLUR_RADIUS = 150;
	
	@CCControl(name = "blur radius", min = 0, max = MAXIMUM_BLUR_RADIUS)
	private float _cBlurRadius = MAXIMUM_BLUR_RADIUS;
	
	private CCGPUSeperateGaussianBlur _myBlur;
	private CCArcball _myArcball;

	public void setup() {
		addControls("blur", "blur", this);

		_myBlur = new CCGPUSeperateGaussianBlur(120, width, height, 1);
		
		_myArcball = new CCArcball(this);
	}
	float _myTime = 0;
	public void update(final float theTime){
		_myTime += theTime;
		_myBlur.radius(_cBlurRadius);
	}

	public void draw() {
		g.color(255);
		g.clear();
		
		_myBlur.beginDraw(g);
		g.clear();
		_myArcball.draw(g);
		g.box(300);
		_myBlur.endDraw(g);
		
		g.color(0);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCBlur3DTest.class);
		myManager.settings().size(512, 512);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
