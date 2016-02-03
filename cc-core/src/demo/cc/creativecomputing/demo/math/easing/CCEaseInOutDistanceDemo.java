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
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.easing.CCEasing.CCEaseFormular;

public class CCEaseInOutDistanceDemo extends CCApp {
	
	@CCControl(name = "center", min = -400, max = 400)
	private float _cCenter = 0;
	
	@CCControl(name = "const radius", min = 0, max = 400)
	private float _cConstRadius = 0;
	
	@CCControl(name = "speed", min = 0, max = 100)
	private float _cSpeed = 0;
	
	@CCControl(name = "fade radius", min = 0, max = 400)
	private float _cFadeRadius = 0;
	
	@CCControl(name = "amp", min = 0, max = 100)
	private float _cAmp = 0;
	
	@CCControl(name = "ease mode" )
	private CCEaseFormular _myFormular = CCEaseFormular.LINEAR;
	

	
	private float _myX;

	@Override
	public void setup() {
		addControls("app", "app", this);
		_myX = -width/2;
	}

	@Override
	public void update(final float theDeltaTime) {
		if(_myFormular == null)_myFormular = CCEaseFormular.LINEAR;
		_myX += theDeltaTime * _cSpeed; 
		if(_myX > width/2)_myX-=width;
	}
	
	private float fade(float theInput){
		return _myFormular.easing().easeInOut(1 - CCMath.saturate(CCMath.max(0,CCMath.abs(_cCenter - theInput) - _cConstRadius) / _cFadeRadius));
	}

	@Override
	public void draw() {

		g.clear();
		
		g.line(_cCenter,-height/2, _cCenter, height/2);
		g.line(_cCenter + _cConstRadius,-height/2, _cCenter + _cConstRadius, height/2);
		g.line(_cCenter - _cConstRadius,-height/2, _cCenter - _cConstRadius, height/2);
		
		g.line(_cCenter + _cConstRadius + _cFadeRadius,-height/2, _cCenter + _cConstRadius + _cFadeRadius, height/2);
		g.line(_cCenter - _cConstRadius - _cFadeRadius,-height/2, _cCenter - _cConstRadius - _cFadeRadius, height/2);
		
		g.line(_myX,-height/2, _myX, height/2);
		g.color(255);
		g.beginShape(CCDrawMode.POINTS);
		for(float x = -width/2;  x < width/2;x++){
			System.out.println(x+":" + fade(x));
			g.vertex(x,fade(x) * _cAmp);
		}
		g.endShape();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCEaseInOutDistanceDemo.class);
		myManager.settings().size(1500, 500);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
