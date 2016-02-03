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
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.spline.CCCatmulRomSpline;
import cc.creativecomputing.math.util.CCArcball;

public class CCCatmulRomSplineRibbonDemo extends CCApp {
	
	@CCControl(name = "tension", min = -3, max = 3)
	private float _cCurveTension;
	
	@CCControl(name = "interpolation", min = 0, max = 1)
	private float _cInterpolation = 0;
	
	private CCCatmulRomSpline _mySpline;
	
	private CCArcball _myArcball;

	@Override
	public void setup() {
		CCMath.randomSeed(0);
		_mySpline = new CCCatmulRomSpline(0.5f, false);
		for(int i = 0; i < 10;i++){
			_mySpline.addPoint(new CCVector3f(CCMath.random(-width/3, width/3), CCMath.random(-height/3, height/3), CCMath.random(-height/3, height/3)));
		}
		
		addControls("app", "app", this);
		g.pointSize(8);
		
		_myArcball = new CCArcball(this);
	}
	
	@CCControl(name = "close")
	public void closeCurve(boolean theIsClosed){
		_mySpline.isClosed(theIsClosed);
	}
	
	@CCControl(name = "reset")
	public void resetCurve(boolean theIsClosed){
		_mySpline = new CCCatmulRomSpline(0.5f, false);
		for(int i = 0; i < 10;i++){
			_mySpline.addPoint(new CCVector3f(CCMath.random(-width/3, width/3), CCMath.random(-height/3, height/3), CCMath.random(-height/3, height/3)));
		}
	}

	@Override
	public void update(final float theDeltaTime) {
		_mySpline.curveTension(_cCurveTension);
	}

	@Override
	public void draw() {
		g.clear();
		_myArcball.draw(g);
		
		g.color(255,50);
		_mySpline.draw(g);
		
		g.beginShape(CCDrawMode.LINES);
		
		
		for(int i = 0; i <= 1000; i++){
			float myBlend = i / 1000f;
			
//			CCVector3f myLastPoint1 =  _mySpline.interpolate(myBlend == 1 ? 0.9f : myBlend - 0.1f);
//			CCVector3f myLastPoint2 =  _mySpline.interpolate(myBlend);
//			CCVector3f myLastPoint3 =  _mySpline.interpolate(myBlend == 1 ? 0.1f : myBlend + 0.1f);
//			CCVector3f myNormal = CCVecMath.normal(myLastPoint1, myLastPoint2, myLastPoint3);
			
			CCVector3f myLastPoint2 =  _mySpline.interpolate(myBlend);
			CCVector3f myLastPoint3 =  _mySpline.interpolate(myBlend == 1 ? 0.1f : myBlend + 0.1f);
			
			CCVector3f myNormal = CCVecMath.normal(myLastPoint2.clone().add(0,1,0), myLastPoint2, myLastPoint3);
			CCVector3f myPoint1 = myLastPoint2.clone().add(myNormal.clone().scale(-10));
			CCVector3f myPoint2 = myLastPoint2.clone().add(myNormal.clone().scale(10));
			g.vertex(myPoint1);
			g.vertex(myPoint2);
		}
			
		
		g.endShape();
		
		g.beginShape(CCDrawMode.POINTS);
		g.color(255,0,0);
		g.vertex(_mySpline.points().get(0));
		g.color(255);
		for(int i = 1; i < _mySpline.points().size() - 1;i++){
			g.vertex(_mySpline.points().get(i));
		}
		g.color(0,0,255);
		g.vertex(_mySpline.points().get(_mySpline.points().size() - 1));
		g.endShape();
		
		CCVector3f myInterploatedValue = _mySpline.interpolate(_cInterpolation);
		g.ellipse(myInterploatedValue, 10);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCCatmulRomSplineRibbonDemo.class);
		myManager.settings().size(500, 500);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

