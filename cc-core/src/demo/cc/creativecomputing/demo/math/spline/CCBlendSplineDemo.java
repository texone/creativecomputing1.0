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
package cc.creativecomputing.demo.math.spline;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.spline.CCBlendSpline;
import cc.creativecomputing.math.spline.CCCatmulRomSpline;
import cc.creativecomputing.math.util.CCArcball;

public class CCBlendSplineDemo extends CCApp {
	
	@CCControl(name = "tension1", min = -3, max = 3)
	private float _cCurveTension1;
	
	@CCControl(name = "tension2", min = -3, max = 3)
	private float _cCurveTension2;
	
	@CCControl(name = "spline blend", min = 0, max = 1)
	private float _cSpineBlend;
	
	@CCControl(name = "interpolation", min = 0, max = 1)
	private float _cInterpolation = 0;
	
	private CCCatmulRomSpline _mySpline1;
	private CCCatmulRomSpline _mySpline2;
	
	private CCBlendSpline _myBlendSpline;
	
	private CCArcball _myArcball;

	@Override
	public void setup() {
		CCMath.randomSeed(0);
		_mySpline1 = new CCCatmulRomSpline(0.5f, false);
		_mySpline2 = new CCCatmulRomSpline(0.5f, false);
		for(int i = 0; i < 30;i++){
			_mySpline1.addPoint(randomPoint());
			_mySpline2.addPoint(randomPoint());
		}
		
		_myBlendSpline = new CCBlendSpline(_mySpline1, _mySpline2);
		
		addControls("app", "app", this);
		g.pointSize(8);
		
		_myArcball = new CCArcball(this);
	}
	
	private CCVector3f randomPoint(){
		return new CCVector3f(CCMath.random(-width/3, width/3), CCMath.random(-height/3, height/3), CCMath.random(-height/3, height/3));
	}
	
	@CCControl(name = "close")
	public void closeCurve(boolean theIsClosed){
		_mySpline1.isClosed(theIsClosed);
		_mySpline2.isClosed(theIsClosed);
	}

	@Override
	public void update(final float theDeltaTime) {
		_mySpline1.curveTension(_cCurveTension1);
		_mySpline2.curveTension(_cCurveTension2);
		_myBlendSpline.blend(_cSpineBlend);
	}

	@Override
	public void draw() {
		g.clear();
		
		_myArcball.draw(g);
		g.color(255);
		_myBlendSpline.draw(g);
		
		CCVector3f myInterploatedValue = _myBlendSpline.interpolate(_cInterpolation);
		g.ellipse(myInterploatedValue, 10);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCBlendSplineDemo.class);
		myManager.settings().size(500, 500);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

