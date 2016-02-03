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
package cc.creativecomputing.demo.topic.animation;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.easing.CCEasing;
import cc.creativecomputing.math.easing.CCEasing.CCEaseFormular;

public class CCJumpyLine extends CCApp {
	
	private CCEasing _myEasing = CCEaseFormular.CUBIC.easing();

	@Override
	public void setup() {
	}
	
	private float _myAmplitude = 0;
	private float _myFrequency = 1;
	
	private float _myPhase = 0;
	private float _myTimer = 0;

	@Override
	public void update(final float theDeltaTime) {
		_myTimer += theDeltaTime;
		_myFrequency = _myEasing.easeIn(_myTimer / 5) * 4;
		_myPhase = _myEasing.easeOut(_myTimer / 20) * CCMath.PI * 10;
		_myAmplitude = (CCMath.cos(_myTimer * CCMath.PI * _myFrequency + CCMath.PI) + 1)/2 * 100 * _myEasing.easeOut(1 - _myTimer / 5);
		
		if(_myTimer > 5){
			_myTimer = 0;
			_myFrequency = 1;
		}
	}

	@Override
	public void draw() {
		g.clear();
		g.pushMatrix();
		g.translate(-width/2, 0);
		
		g.beginShape(CCDrawMode.LINE_STRIP);
		for(int x = 0; x < width;x++){
			float blend = 1 - CCMath.abs(width/2 - x)/(float)width/2f;
			blend = CCMath.pow(blend, 5);
			float y = CCMath.sin(x / (float)width * CCMath.TWO_PI * 5 + _myPhase) * _myAmplitude * blend;
			g.vertex(x,y);
		}
		g.endShape();
		
		g.popMatrix();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCJumpyLine.class);
		myManager.settings().size(1200, 400);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
