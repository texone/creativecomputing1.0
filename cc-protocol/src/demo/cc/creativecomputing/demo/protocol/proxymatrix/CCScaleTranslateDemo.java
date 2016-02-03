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
package cc.creativecomputing.demo.protocol.proxymatrix;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.math.CCMatrix32f;
import cc.creativecomputing.math.CCVector2f;

public class CCScaleTranslateDemo extends CCApp {
	
	@CCControl(name = "matrix offset", minX = -1, maxX = 1, minY = -1, maxY = 1)
	private CCVector2f _cOffset = new CCVector2f(0, 0); // as a percentage of screenWidth, so 0.1 would be 10% of the
														// screen size offset
	@CCControl(name = "offset scale", min = 0, max = 1)
	private float _cOffsetScale = 1f;
	
	@CCControl(name = "scale point", minX = -1, maxX = 1, minY = -1, maxY = 1)
	private CCVector2f _cScalePoint = new CCVector2f(0, 0); // as a percentage of screenWidth, so 0.1 would be 10% of the
														// screen size offset
	@CCControl(name = "scale pointscale", min = 0, max = 1)
	private float _cScalePointScale = 1f;

	@CCControl(name = "scaling", minX = 0.5f, maxX = 2f, minY = 0.5f, maxY = 2f, x = 1, y = 1)
	private CCVector2f _cScaling = new CCVector2f(1, 1); // multiplier
	
	@CCControl(name = "scaling scale", min = 0, max = 1)
	private float _cScalingScale = 1f;
	
	private CCMatrix32f _myMatrix;

	@Override
	public void setup() {
		addControls("sensors", "Calibration", this);
	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {
		g.clear();
		
		CCVector2f myOffset = _cOffset.clone();
		myOffset.scale(_cOffsetScale);
		
		CCVector2f myScalePoint = _cScalePoint.clone();
		myScalePoint.scale(_cScalePointScale);
		
		CCVector2f myScaling = _cScaling.clone();
		myScaling.scale(_cScalingScale);
		
		_myMatrix = new CCMatrix32f();
		_myMatrix.translate(-width * 0.5f, height * 0.5f);
		_myMatrix.translate(myOffset.x * width, -myOffset.y * height);
		_myMatrix.scale(myScaling.x, myScaling.y);
		_myMatrix.translate(myScalePoint.x * width, -myScalePoint.y * height);
		
		CCVector2f myPoint = new CCVector2f(50,50);
		myPoint = _myMatrix.transform(myPoint);
		
		g.pushMatrix();

		g.translate(-width * 0.5f, height * 0.5f);
		g.translate(myOffset.x * width, -myOffset.y * height);
		g.scale(myScaling.x, myScaling.y);
		g.translate(myScalePoint.x * width, -myScalePoint.y * height);
		
		g.color(255);
		g.rect(0,0,100,100);
		g.color(255,0,0);
		g.ellipse( -myScalePoint.x * width, myScalePoint.y * height, 3);
		
		
		g.popMatrix();

		g.color(0,255,0);
		g.ellipse(myPoint.x, myPoint.y, 3);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCScaleTranslateDemo.class);
		myManager.settings().size(500, 800);
		myManager.start();
	}
}
